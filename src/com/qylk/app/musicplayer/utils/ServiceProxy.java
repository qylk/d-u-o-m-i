package com.qylk.app.musicplayer.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.qylk.app.musicplayer.service.IMediaPlaybackService;
import com.qylk.app.musicplayer.service.MediaPlaybackService;

public class ServiceProxy {
	public static class ServiceToken {
		ContextWrapper mWrappedContext;

		ServiceToken(ContextWrapper paramContextWrapper) {
			this.mWrappedContext = paramContextWrapper;
		}
	}

	private static HashMap<Context, ServiceProxyRegisterListener> sConnectionMap = new HashMap<Context, ServiceProxyRegisterListener>();
	private static IMediaPlaybackService sService = null;

	public static IMediaPlaybackService getService() {
		return sService;
	}

	// private static class ServiceBinder implements ServiceConnection {
	// ServiceConnection mCallback;
	//
	// ServiceBinder(ServiceConnection callback) {
	// mCallback = callback;
	// }
	//
	// public void onServiceConnected(ComponentName className,
	// android.os.IBinder service) {
	// if (mCallback != null) {
	// mCallback.onServiceConnected(className, service);
	// }
	// }
	//
	// public void onServiceDisconnected(ComponentName className) {
	// if (mCallback != null) {
	// mCallback.onServiceDisconnected(className);
	// }
	// }
	// }

	private static ServiceConnection localsc = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			sService = IMediaPlaybackService.Stub.asInterface(service);
			Iterator<Entry<Context, ServiceProxyRegisterListener>> iter = sConnectionMap
					.entrySet().iterator();
			while (iter.hasNext()) {
				HashMap.Entry<Context, ServiceProxyRegisterListener> entry = (HashMap.Entry<Context, ServiceProxyRegisterListener>) iter
						.next();
				ServiceProxyRegisterListener listener = entry.getValue();
				if (listener != null)
					listener.onServiceProxyReady();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			sService = null;
		}
	};

	public interface ServiceProxyRegisterListener {
		public void onServiceProxyReady();

		public void onUnRegisterFromServiceProxy();
	}

	public static ServiceToken register(Context context,
			ServiceProxyRegisterListener callback) {
		ContextWrapper cw = new ContextWrapper(context);
		if (sConnectionMap.isEmpty()) {
			Context app = context.getApplicationContext();
			app.startService(new Intent(MediaPlaybackService.SERVICECMD));
			app.bindService(new Intent(MediaPlaybackService.SERVICECMD),
					localsc, Context.BIND_AUTO_CREATE);
		} else if (sService != null) {
			callback.onServiceProxyReady();
		}
		sConnectionMap.put(cw, callback);
		return new ServiceToken(cw);
	}

	public static void unregister(ServiceToken token,
			ServiceProxyRegisterListener listener) {
		if (token != null)
			sConnectionMap.remove(token.mWrappedContext);
		System.out.println("ssss:" + sConnectionMap.size());
		if (sConnectionMap.isEmpty()) {
			Context app = token.mWrappedContext;
			app.getApplicationContext().unbindService(localsc);
			if (!isPlaying())
				app.stopService(new Intent(MediaPlaybackService.SERVICECMD));
			sService = null;
		}
		if (listener != null)
			listener.onUnRegisterFromServiceProxy();
	}

	// public static void AddToEnqueue(int id) {
	// try {
	// sService.enqueue(id);
	// } catch (RemoteException e) {
	// e.printStackTrace();
	// }
	// }

	// public static void EnqueueSelected(int paramInt) {
	// try {
	// sService.enqueue(paramInt);
	// return;
	// } catch (RemoteException localRemoteException) {
	// localRemoteException.printStackTrace();
	// }
	// }

	public static int getTrackId() {
		try {
			return sService.getAudioId();
		} catch (RemoteException e) {
			return -1;
		}
	}

	public static boolean MediaPlayerHasInitialized() {
		try {
			return sService.isInitialized();
		} catch (RemoteException e) {
			return false;
		}
	}

	// public static void PlaySelected(int id) {
	// try {
	// sService.playSelected(id);
	// } catch (RemoteException e) {
	// }
	// }

	public static void play() {
		try {
			sService.startplay();
		} catch (RemoteException e) {
		}
	}

	public static void stop() {
		try {
			sService.stop();
		} catch (RemoteException e) {
		}
	}

	public static void pre() {
		try {
			if (sService.position() < 10000)
				sService.prev();
			else {
				sService.seek(0L);// ÖØ²¥
				sService.play();
			}
		} catch (RemoteException e) {
		}
	}

	public static void togglePlay() {
		try {
			if (sService.isPlaying())
				sService.pause();
			else
				sService.play();
		} catch (RemoteException e) {
		}
	}

	public static void next() {
		try {
			sService.next();
		} catch (RemoteException e) {
		}
	}

	public static void seek(long position) {
		try {
			sService.seek(position);
		} catch (RemoteException e) {
		}
	}

	public static boolean isPlaying() {
		try {
			return sService.isPlaying();
		} catch (RemoteException e) {
		}
		return false;
	}

	public static long getPosition() {
		try {
			return sService.position();
		} catch (RemoteException e) {
		}
		return 0;
	}

	public static long getDuration() {
		try {
			return sService.duration();
		} catch (RemoteException e) {
		}
		return 0;
	}

	public static String getTrackTitle() {
		try {
			return sService.getTrackName();
		} catch (RemoteException e) {
		}
		return "";
	}

	public static String getArtist() {
		try {
			return sService.getArtistName();
		} catch (RemoteException e) {
		}
		return "";
	}

	public static int getArtistId() {
		try {
			return sService.getArtistId();
		} catch (RemoteException e) {
		}
		return 0;
	}

	public static String getAlbum() {
		try {
			return sService.getAlbumName();
		} catch (RemoteException e) {
		}
		return "";
	}

	// public static void playAll(String sel, int offset, boolean shuffle) {
	// try {
	// sService.playAll(sel, offset, shuffle);
	// } catch (RemoteException e) {
	// e.printStackTrace();
	// }
	// }

	// public static void setPlayPosition(int index) {
	// try {
	// sService.setQueuePosition(index);
	// } catch (RemoteException e) {
	// e.printStackTrace();
	// }
	// }

	// public static void setMode(int mode) {
	// try {
	// sService.setMode(mode);
	// } catch (RemoteException e) {
	// e.printStackTrace();
	// }
	// }

}
