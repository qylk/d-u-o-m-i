package com.qylk.app.musicplayer.service;

//import com.qylk.app.musicplayer.service.TrackInfo;

interface IMediaPlaybackService
{
    //void openFile(String path);
    //int getQueuePosition();
    boolean isPlaying();
   // void playSelected(int id);
   // void playAll(String sel,int offset,boolean shuffle);
    void stop();
    void pause();
    //TrackInfo getTrackInfo();
    void play();
    void prev();
    void startplay();
    void next();
    long duration();
    long position();
    long seek(long pos);
    String getTrackName();
    String getAlbumName();
    int getAlbumId();
    String getArtistName();
    int getArtistId();
    //void enqueue(int id);
    //int [] getQueue();
   	//void setQueuePosition(int index);
    String getPath();
    int getAudioId();
    //void setMode(int mode);
    //int getMode();
    //boolean removeTracks(int first, int last);
    //boolean moveTracks(int first, int last);
    //void setRepeat(boolean repeat);
    //boolean isRepeat();
    boolean isInitialized();
    //int getMediaMountedCount();
    //int getAudioSessionId();
}

