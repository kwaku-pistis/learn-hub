package deemiensa.com.learnhub.gui.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import deemiensa.com.learnhub.App.SkyTubeApp;
import deemiensa.com.learnhub.R;
import deemiensa.com.learnhub.businessobjects.VideoCategory;
import deemiensa.com.learnhub.businessobjects.YouTube.POJOs.YouTubeChannel;
import deemiensa.com.learnhub.businessobjects.YouTube.POJOs.YouTubePlaylist;
import deemiensa.com.learnhub.gui.businessobjects.MainActivityListener;
import deemiensa.com.learnhub.gui.businessobjects.PlaylistClickListener;
import deemiensa.com.learnhub.gui.businessobjects.adapters.PlaylistsGridAdapter;


/**
 * A fragment that displays the Playlists belonging to a Channel
 */
public class ChannelPlaylistsFragment extends VideosGridFragment implements PlaylistClickListener, SwipeRefreshLayout.OnRefreshListener {
	private PlaylistsGridAdapter playlistsGridAdapter;
	private YouTubeChannel channel;
	private MainActivityListener mainActivityListener;


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

		swipeRefreshLayout.setOnRefreshListener(this);

		if (playlistsGridAdapter == null) {
			playlistsGridAdapter = new PlaylistsGridAdapter(getActivity(), this);
		} else {
			playlistsGridAdapter.setContext(getActivity());
		}

		channel = (YouTubeChannel)getArguments().getSerializable(ChannelBrowserFragment.CHANNEL_OBJ);
		playlistsGridAdapter.setYouTubeChannel(channel);

		gridView.setAdapter(playlistsGridAdapter);

		return view;
	}

	@Override
	public String getFragmentName() {
		return SkyTubeApp.getStr(R.string.playlists);
	}

	@Override
	public void onClickPlaylist(YouTubePlaylist playlist) {
		if(mainActivityListener != null)
			mainActivityListener.onPlaylistClick(playlist);
	}

	public void setMainActivityListener(MainActivityListener mainActivityListener) {
		this.mainActivityListener = mainActivityListener;
	}

	@Override
	public void onRefresh() {
		playlistsGridAdapter.refresh(new Runnable() {
			@Override
			public void run() {
				swipeRefreshLayout.setRefreshing(false);
			}
		});
	}


	@Override
	protected VideoCategory getVideoCategory() {
		return null;
	}

}
