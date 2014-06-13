package com.qylk.app.musicplayer.service;

import android.os.RemoteException;
import java.lang.ref.WeakReference;

class ServiceStub extends IMediaPlaybackService.Stub {
	WeakReference<MediaPlaybackService> mService;

	ServiceStub(MediaPlaybackService service) {
		mService = new WeakReference<MediaPlaybackService>(service);
	}

	public long duration() {
		return mService.get().duration();
	}

//	public void enqueue(int id) {
//		mService.get().enqueue(id);
//	}

	public int getAlbumId() {
		return mService.get().getAlbumId();
	}

	public String getAlbumName() {
		return mService.get().getAlbumName();
	}

	public int getArtistId() {
		return mService.get().getArtistId();
	}

	public String getArtistName() {
		return mService.get().getArtistName();
	}

	public int getAudioId() {
		return mService.get().getTrackId();
	}

//	public int getMode() {
//		return mService.get().getListProvider().getMode();
//	}

	public String getPath() {
		return mService.get().getPath();
	}

//	@Override
//	public int[] getQueue() throws RemoteException {
//		return mService.get().getListProvider().getCopyList();
//	}

	public int getQueuePosition() {
		return mService.get().getQueuePosition();
	}

	public String getTrackName() {
		return mService.get().getTrackName();
	}

	@Override
	public boolean isInitialized() throws RemoteException {
		return mService.get().MediaPlayHasInitialized();
	}

	public boolean isPlaying() {
		return mService.get().isPlaying();
	}

//	public boolean isRepeat() {
//		return mService.get().getListProvider().isRepeat();
//	}

	public void next() {
		mService.get().next(false);
	}

	public void openFile(String filepath) {
		mService.get().open(filepath);
	}

	public void pause() {
		mService.get().pause();
	}

	public void play() {
		mService.get().play();
	}

//	@Override
//	public void playSelected(int id) throws RemoteException {
//		mService.get().playTrack(id);
//	}

	public long position() {
		return mService.get().position();
	}

	public void prev() {
		mService.get().prev();
	}

//	@Override
//	public boolean removeTracks(int first, int last) throws RemoteException {
//		return mService.get().removeTracks(first, last);
//	}

	public long seek(long pos) {
		return mService.get().seek(pos);
	}

//	public void setMode(int mode) {
//		mService.get().getListProvider().setMode(mode);
//	}

//	public void setQueuePosition(int pisition) {
//		mService.get().setQueuePosition(pisition);
//	}

//	public void setRepeat(boolean repeat) {
//		mService.get().getListProvider().setRepeat(repeat);
//	}

	public void stop() {
		mService.get().stop(false);
	}

//	@Override
//	public void playAll(String sel, int offset, boolean shuffle)
//			throws RemoteException {
//		mService.get().playAll(sel, offset, shuffle);
//	}

//	@Override
//	public boolean moveTracks(int from, int to) throws RemoteException {
//		return mService.get().getListProvider().moveItems(from, to);
//	}

	@Override
	public void startplay() throws RemoteException {
		mService.get().startplay();
	}
}