package deemiensa.com.learnhub.gui.businessobjects;


import deemiensa.com.learnhub.businessobjects.YouTube.POJOs.YouTubePlaylist;

/**
 * Interface for an object that will respond to a Playlist being clicked on
 */
public interface PlaylistClickListener {
	void onClickPlaylist(YouTubePlaylist playlist);
}
