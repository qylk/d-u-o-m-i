package com.qylk.app.musicplayer.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.qylk.app.musicplayer.R;
import com.qylk.app.musicplayer.activity.MainActivity;
import com.qylk.app.musicplayer.utils.MediaDatabase;

public class MediaPlaybackService extends Service implements MusicFocusable {
	private static final String TAG = MediaPlaybackService.class
			.getSimpleName();

	static enum AudioFocus {
		Focused, NoFocusCanDuck, NoFocusNoDuck
	}

	static enum CMD {
		CMDEMPTY, CMDNEXT, CMDPAUSE, CMDPLAY, CMDPREVIOUS, CMDSTOP, CMDTOGGLEPAUSE
	}

	public static final String CMDNAME = "com";

	static final int TRACK_ENDED = 1;
	static final int RELEASE_WAKELOCK = 2;
	static final int SERVER_DIED = 3;
	static final int FADEDOWN = 4;
	static final int FADEUP = 5;

	private static final int SEC = 1000;
	private static final long IDLE_DELAY = 10 * SEC;// 30秒超时销毁service
	private static final int PLAYBACKSERVICE_STATUS = 2014;

	public static final String SERVICECMD = "com.qylk.music.service.action";

	public static final String PLAYSTATE_CHANGED = "com.qylk.music.playstatechanged";

	public static final String META_CHANGED = "com.qylk.music.metachanged3";

	private AudioFocus mAudioFocus = AudioFocus.NoFocusNoDuck;
	private AudioFocusHelper mAudioFocusHelper;
	private AudioManager mAudioManager;
	private final IBinder mBinder = new ServiceStub(this);
	private Cursor mCursor;
	private String mFileToPlay;
	private float mCurrentVolume = 0f;
	private int mOpenFailedCounter;
	private boolean mPausedByTransientLossOfFocus;
	private MultiPlayer mPlayer;
	private SharedPreferences mPreferences;
	private boolean mServiceInUse;
	private int mServiceStartId;
	private BroadcastReceiver mUnmountReceiver = null;
	private int nowId;
	private TrackIdProvider trackIdProvider;

	String[] mCursorCols = new String[] { "audio._id AS _id",
			MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
			MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
			MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.ARTIST_ID };

	private Handler mDelayedStopHandler = new Handler() {
		public void handleMessage(Message paramMessage) {
			if (isPlaying() || mPausedByTransientLossOfFocus || mServiceInUse) {
				return;
			}
			saveStatus();
			trackIdProvider.close();
			stopSelf(mServiceStartId);
		}
	};

	private Handler mMediaplayerHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FADEDOWN:
				mCurrentVolume -= .05f;
				if (mCurrentVolume > .4f) {
					mMediaplayerHandler.sendEmptyMessageDelayed(FADEDOWN, 10);
				} else {
					mCurrentVolume = .4f;
				}
				mPlayer.setVolume(mCurrentVolume * mCurrentVolume);
				break;
			case FADEUP:
				mCurrentVolume += .02f;
				if (mCurrentVolume < 1.0f) {
					mMediaplayerHandler.sendEmptyMessageDelayed(FADEUP, 10);
				} else {
					mCurrentVolume = 1.0f;
				}
				mPlayer.setVolume(mCurrentVolume * mCurrentVolume);// 上抛物线形音量模型
				break;
			case SERVER_DIED:
				next(true);
				break;
			case TRACK_ENDED:
				recordPlayTimes();
				next(true);
				break;
			default:
				break;
			}
		}
	};

	public void closeExternalStorageFiles(String storagePath) {
		stop(true);
		// notifyChange(META_CHANGED);
		notifyChange(PLAYSTATE_CHANGED);
	}

	private void recordPlayTimes() {
		MediaDatabase.updatePlayTimes(this, trackIdProvider.getId());
	}

	public long duration() {
		if (mPlayer.isInitialized())
			return mPlayer.duration();
		else
			return -1;
	}

	// public void enqueue(int id) {
	// trackIdProvider.add(id);
	// notifyChange(QUEUE_CHANGED);
	// }

	public int getAlbumId() {
		if (mCursor == null) {
			return -1;
		}
		return mCursor.getInt(mCursor
				.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
	}

	public String getAlbumName() {
		if (mCursor == null) {
			return null;
		}
		return mCursor.getString(mCursor
				.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
	}

	public int getArtistId() {
		if (mCursor == null) {
			return -1;
		}
		return mCursor.getInt(mCursor
				.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID));
	}

	public String getArtistName() {
		if (mCursor == null) {
			return null;
		}
		return mCursor.getString(mCursor
				.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
	}

	// public TrackIdProvider getListProvider() {
	// return trackIdProvider;
	// }

	public String getPath() {
		return mFileToPlay;
	}

	public int getQueuePosition() {
		return trackIdProvider.getCurPosition();
	}

	public int getTrackId() {
		return nowId;
	}

	public String getTrackName() {
		if (mCursor == null) {
			return null;
		}
		return mCursor.getString(mCursor
				.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
	}

	private void giveUpAudioFocus() {
		if ((mAudioFocus == AudioFocus.Focused) && (mAudioFocusHelper != null)
				&& (mAudioFocusHelper.abandonFocus()))
			mAudioFocus = AudioFocus.NoFocusNoDuck;
	}

	private void gotoIdleState() {
		Notification();
		stopForeground(false);
		mDelayedStopHandler.removeCallbacksAndMessages(null);
		Message msg = mDelayedStopHandler.obtainMessage();
		mDelayedStopHandler.sendMessageDelayed(msg, IDLE_DELAY);
	}

	public boolean isPlaying() {
		if (mPlayer.isInitialized())
			return mPlayer.isPlaying();
		else
			return false;
	}

	public boolean MediaPlayHasInitialized() {
		return mPlayer.isInitialized();
	}

	// TODO
	public void next(boolean IdleIfEmpty) {
		stop(false);
		if (trackIdProvider.hasNext()) {
			openCurrent(trackIdProvider.next());
			notifyChange(META_CHANGED);
			play();
		} else {
			notifyChange(PLAYSTATE_CHANGED);
			Toast.makeText(this, R.string.trackended, Toast.LENGTH_LONG).show();
		}
	}

	private void Notification() {
		RemoteViews views = new RemoteViews(getPackageName(),
				R.layout.notification_view);
		views.setTextViewText(R.id.track, getTrackName());
		if (isPlaying())
			views.setImageViewResource(R.id.statusbar_pause,
					R.drawable.st_pause_selector);
		else
			views.setImageViewResource(R.id.statusbar_pause,
					R.drawable.st_play_selector);
		views.setOnClickPendingIntent(R.id.statusbar_pause, PendingIntent
				.getService(this, 0, new Intent(SERVICECMD).putExtra(CMDNAME,
						CMD.CMDTOGGLEPAUSE), PendingIntent.FLAG_UPDATE_CURRENT));
		views.setOnClickPendingIntent(R.id.statusbar_next, PendingIntent
				.getService(this, 1,
						new Intent(SERVICECMD).putExtra(CMDNAME, CMD.CMDNEXT),
						PendingIntent.FLAG_UPDATE_CURRENT));

		Notification.Builder mBuilder = new Notification.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher).setContent(views);
		mBuilder.setOngoing(isPlaying());
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, MainActivity.class), 0);
		mBuilder.setContentIntent(contentIntent);

		// deprecated code
		// Notification notify = new Notification();
		// notify.flags |= Notification.FLAG_ONGOING_EVENT;
		// notify.icon = R.drawable.ic_launcher;//必须设置，否则不显示
		// notify.contentView = views;
		// notify.contentIntent = PendingIntent.getActivity(this, 0,
		// new Intent("com.qylk.music.PLAYBACK_VIEWER")
		// /* .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) */, 0);
		// startForeground(PLAYBACKSERVICE_STATUS, notify);
		// if (isPlaying())
		startForeground(PLAYBACKSERVICE_STATUS, mBuilder.build());// API 16
	}

	private void notifyChange(String what) {
		Intent intent = new Intent(what);
		intent.putExtra("id", nowId);
		intent.putExtra("track", getTrackName());
		intent.putExtra("artist", getArtistName());
		intent.putExtra("album", getAlbumName());
		intent.putExtra("playing", isPlaying());
		sendBroadcast(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// onCreate() will be called and this bind takes a little time.
		Log.v(TAG, "a MediaPlaybackService client has connected");
		mDelayedStopHandler.removeCallbacksAndMessages(null);
		mServiceInUse = true;
		return mBinder;
	}

	public void onCreate() {
		super.onCreate();
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		ComponentName rec = new ComponentName(getPackageName(),
				MediaButtonIntentReceiver.class.getName());
		mAudioManager.registerMediaButtonEventReceiver(rec);

		mAudioFocusHelper = new AudioFocusHelper(getApplicationContext(), this);
		registerExternalStorageListener();
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		mPlayer = new MultiPlayer(this);
		mPlayer.setHandler(mMediaplayerHandler);
		trackIdProvider = TrackIdProvider.getInstance(this);
		// trackIdProvider = new TrackIdProvider(this, true);
		if (!trackIdProvider.isEmpty()) {// 恢复状态
			openCurrent(trackIdProvider.getId());
			restoreStatus();
		}
		mPlayer.setVolume(0f);
		requestAudioFocus();
		// mWakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
		// .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass()
		// .getName());
		// mWakeLock.setReferenceCounted(false);
		Message localMessage = mDelayedStopHandler.obtainMessage();
		mDelayedStopHandler.sendMessageDelayed(localMessage, IDLE_DELAY);
		Log.v(TAG, "MediaPlaybackService created");
	}

	@Override
	public void onDestroy() {
		stopForeground(true);
		mPlayer.release();
		mPlayer = null;
		giveUpAudioFocus();
		mDelayedStopHandler.removeCallbacksAndMessages(null);
		mMediaplayerHandler.removeCallbacksAndMessages(null);
		if (mCursor != null) {
			mCursor.close();
			mCursor = null;
		}
		if (mUnmountReceiver != null) {
			unregisterReceiver(mUnmountReceiver);
			mUnmountReceiver = null;
		}
		// mWakeLock.release();
		super.onDestroy();
	}

	public void onGainedAudioFocus() {
		if ((!isPlaying()) && (this.mPausedByTransientLossOfFocus)) {
			mPausedByTransientLossOfFocus = false;
			mPlayer.setVolume(0.0F);
			play();
			return;
		}
		mPlayer.setVolume(0);
		mCurrentVolume = 0f;
		mMediaplayerHandler.removeMessages(FADEDOWN);
		mMediaplayerHandler.sendEmptyMessage(FADEUP);
	}

	public void onLostAudioFocus(boolean canduck) {
		if (canduck) {
			mMediaplayerHandler.removeMessages(FADEUP);// 逐步减小音量
			mMediaplayerHandler.sendEmptyMessage(FADEDOWN);
		} else if (isPlaying()) {
			mPausedByTransientLossOfFocus = true;
			pause();
		}
	}

	@Override
	public void onRebind(Intent paramIntent) {
		// previous client,onCreate() will not be called as service was not
		// destroyed,and this bind takes little time.
		Log.v(TAG, "an previous MediaPlaybackService client has disconnected");
		mDelayedStopHandler.removeCallbacksAndMessages(null);
		mServiceInUse = true;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mServiceStartId = startId;
		mDelayedStopHandler.removeCallbacksAndMessages(null);
		if (intent != null) {
			CMD cmd = (CMD) intent.getSerializableExtra(CMDNAME);
			if (CMD.CMDNEXT == cmd) {
				next(false);
			} else if (CMD.CMDPREVIOUS == cmd) {
				if (position() < 10 * SEC) {
					prev();
				} else {
					seek(0L);
					play();
				}
			} else if (CMD.CMDTOGGLEPAUSE == cmd) {
				if (isPlaying()) {
					pause();
					mPausedByTransientLossOfFocus = false;
				} else {
					play();
				}
			} else if (CMD.CMDPAUSE == cmd) {
				pause();
				mPausedByTransientLossOfFocus = false;
			} else if (CMD.CMDPLAY == cmd) {
				play();
			} else if (CMD.CMDSTOP == cmd) {
				pause();
				mPausedByTransientLossOfFocus = false;
				seek(0);
			}
		}
		mDelayedStopHandler.removeCallbacksAndMessages(null);
		Message msg = mDelayedStopHandler.obtainMessage();
		mDelayedStopHandler.sendMessageDelayed(msg, IDLE_DELAY);
		return START_STICKY;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.v(TAG, "all MediaPlaybackService clients have disconnected");
		mServiceInUse = false;
		saveStatus();
		if (isPlaying() || mPausedByTransientLossOfFocus) {
			return true;
		}
		if (trackIdProvider.hasNext()
				|| mMediaplayerHandler.hasMessages(TRACK_ENDED)) {
			Message msg = mDelayedStopHandler.obtainMessage();
			mDelayedStopHandler.sendMessageDelayed(msg, IDLE_DELAY);
			return true;
		}
		stopSelf(mServiceStartId);
		return true;
	}

	public void open(String path) {
		if (path == null) {
			return;
		}
		boolean fromListProvider;
		ContentResolver resolver = getContentResolver();
		Uri uri;
		String where;
		String selectionArgs[];
		if (path.startsWith("content://media/")) {
			uri = Uri.parse(path);
			where = null;
			selectionArgs = null;
			fromListProvider = true;
		} else {
			uri = MediaStore.Audio.Media.getContentUriForPath(path);
			where = MediaStore.Audio.Media.DATA + "=?";
			selectionArgs = new String[] { path };
			fromListProvider = false;
		}
		mFileToPlay = path;
		if (mCursor != null)
			mCursor.close();
		mCursor = resolver.query(uri, mCursorCols, where, selectionArgs, null);
		if (mCursor != null) {
			if (mCursor.moveToFirst()) {
				nowId = mCursor.getInt(mCursor
						.getColumnIndex(MediaStore.Audio.Media._ID));
				if (!fromListProvider)
					trackIdProvider.addToNext(nowId);// TODO 这一句逻辑对吗？？
			} else {
				// TODO 异常处理?
				mCursor.close();
				mCursor = null;
			}
		}
		mPlayer.setDataSource(mFileToPlay);
		if (!mPlayer.isInitialized()) {
			if (mOpenFailedCounter++ < 10 && trackIdProvider.hasNext()) {
				next(true);
			}
			if (!mPlayer.isInitialized() && mOpenFailedCounter != 0) {
				mOpenFailedCounter = 0;
				Toast.makeText(this, R.string.playback_failed,
						Toast.LENGTH_SHORT).show();
			}
		} else {
			mOpenFailedCounter = 0;
		}
	}

	private void openCurrent(int id) {
		open(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "/" + id);
	}

	public void pause() {
		mMediaplayerHandler.removeMessages(FADEUP);
		if (isPlaying()) {
			mPlayer.setVolume(0f);
			mPlayer.pause();
			gotoIdleState();
			notifyChange(PLAYSTATE_CHANGED);
		}
	}

	public void play() {
		if (mPlayer.isInitialized()) {
			mPlayer.start();
			mCurrentVolume = 0f;
			mMediaplayerHandler.removeMessages(FADEDOWN);
			mMediaplayerHandler.sendEmptyMessage(FADEUP);
			Notification();
			notifyChange(PLAYSTATE_CHANGED);
		}
	}

	public long position() {
		if (mPlayer.isInitialized())
			return this.mPlayer.position();
		return -1;
	}

	public void prev() {
		if (trackIdProvider.hasPrevious()) {
			trackIdProvider.previous();
			startplay();
		}
	}

	public void registerExternalStorageListener() {
		if (mUnmountReceiver == null) {
			mUnmountReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					String action = intent.getAction();
					if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
						trackIdProvider.close();
						closeExternalStorageFiles(intent.getData().getPath());
					} else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
						reloadQueue();
						notifyChange(META_CHANGED);
					}
				}
			};
			IntentFilter iFilter = new IntentFilter();
			iFilter.addAction(Intent.ACTION_MEDIA_EJECT);
			iFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
			iFilter.addDataScheme("file");
			registerReceiver(mUnmountReceiver, iFilter);
		}
	}

	private void reloadQueue() {
		trackIdProvider.reload();
		openCurrent(trackIdProvider.getId());
		seek(mPreferences.getLong("seekpos", 0L));
		notifyChange(META_CHANGED);
	}

	@Deprecated
	public boolean removeTracks(int from, int to) {
		int idx = trackIdProvider.getCurPosition();
		boolean suc = trackIdProvider.remove(from, to);
		if (idx >= from && idx <= to) {
			if (suc) {
				boolean isplaying = isPlaying();// 原先正在播放
				if (!trackIdProvider.isEmpty()) {
					startplay();
					if (!isplaying)// 还原暂停状态
						pause();
				} else
					gotoIdleState();
			}
		}
		return suc;
	}

	private void requestAudioFocus() {
		if ((mAudioFocus != AudioFocus.Focused) && (mAudioFocusHelper != null)
				&& (mAudioFocusHelper.requestFocus()))
			mAudioFocus = AudioFocus.Focused;
	}

	private void restoreStatus() {
		long pos = mPreferences.getLong("seekpos", 0);
		if (mPlayer.isInitialized())
			mPlayer.seek(pos);
	}

	private void saveStatus() {
		if (mPlayer.isInitialized()) {
			SharedPreferences.Editor ed = mPreferences.edit();
			if (mPlayer.isInitialized())
				ed.putLong("seekpos", mPlayer.position());
			ed.commit();
		}
	}

	public long seek(long position) {
		if (mPlayer.isInitialized()) {
			if (position < 0)
				throw new IllegalArgumentException("position must be positive");
			if (position > mPlayer.duration())
				position = mPlayer.duration();
			mCurrentVolume = 0f;
			mPlayer.setVolume(0f);
			mMediaplayerHandler.removeMessages(FADEDOWN);
			mMediaplayerHandler.sendEmptyMessage(FADEUP);
			return mPlayer.seek(position);
		}
		return -1;
	}

	public void startplay() {
		stop(false);
		openCurrent(trackIdProvider.getId());
		notifyChange(META_CHANGED);
		play();
	}

	public void stop(boolean remove_status_icon) {
		if (mPlayer.isInitialized()) {
			mPlayer.stop();
		}
		mFileToPlay = null;
		if (mCursor != null) {
			mCursor.close();
			mCursor = null;
		}
		if (remove_status_icon) {
			stopForeground(true);
		} else {
			gotoIdleState();
		}
	}

}
