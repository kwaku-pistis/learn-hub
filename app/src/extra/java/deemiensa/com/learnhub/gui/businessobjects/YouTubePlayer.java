/*
 * SkyTube
 * Copyright (C) 2016  Ramon Mifsud
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation (version 3 of the License).
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package deemiensa.com.learnhub.gui.businessobjects;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import deemiensa.com.learnhub.App.SkyTubeApp;
import deemiensa.com.learnhub.R;
import deemiensa.com.learnhub.businessobjects.YouTube.POJOs.YouTubeAPIKey;
import deemiensa.com.learnhub.businessobjects.YouTube.POJOs.YouTubeVideo;
import deemiensa.com.learnhub.gui.activities.YouTubePlayerActivity;

import static deemiensa.com.learnhub.gui.activities.YouTubePlayerActivity.YOUTUBE_VIDEO_OBJ;


/**
 * Launches YouTube player.
 */
public class YouTubePlayer {

	private static final String TAG = YouTubePlayer.class.getSimpleName();

	/**
	 * Launches the YouTube player so that the user can view the selected video.
	 *
	 * @param youTubeVideo HomeVideo to be viewed.
	 */
	public static void launch(YouTubeVideo youTubeVideo, Context context) {
		// if the user has selected to play the videos using the official YouTube player
		// (in the preferences/settings) ...
		if (useOfficialYouTubePlayer(context)) {
			launchOfficialYouTubePlayer(youTubeVideo.getId(), context);
		} else {
			launchCustomYouTubePlayer(youTubeVideo, context);
		}
	}


	/**
	 * Launches the custom-made YouTube player so that the user can view the selected video.
	 *
	 * @param videoUrl URL of the video to be watched.
	 */
	public static void launch(String videoUrl, Context context) {
		// if the user has selected to play the videos using the official YouTube player
		// (in the preferences/settings) ...
		if (useOfficialYouTubePlayer(context)) {
			launchOfficialYouTubePlayer(YouTubeVideo.getYouTubeIdFromUrl(videoUrl), context);
		} else {
			launchCustomYouTubePlayer(videoUrl, context);
		}
	}


	/**
	 * Read the user's preferences and determine if the user wants to use the official YouTube video
	 * player or not.
	 *
	 * @return True if the user wants to use the official player; false otherwise.
	 */
	private static boolean useOfficialYouTubePlayer(Context context) {
		SharedPreferences   sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String              str = sharedPref.getString(SkyTubeApp.getStr(R.string.pref_key_choose_player), SkyTubeApp.getStr(R.string.pref_default_player_value));

		return  (str.equals(SkyTubeApp.getStr(R.string.pref_official_player_value)));
	}


	/**
	 * Launch the official (i.e. non-free) YouTube player.
	 */
	private static void launchOfficialYouTubePlayer(String videoId, Context context) {
		try {
			// try to start the YouTube standalone player
			Intent intent = com.google.android.youtube.player.YouTubeStandalonePlayer.createVideoIntent((Activity) context, YouTubeAPIKey.get().getYouTubeAPIKey(), videoId);
			context.startActivity(intent);
		} catch (Exception e) {
			String errorMsg = context.getString(R.string.launch_offical_player_error);

			// log the error
			Log.e(TAG, errorMsg, e);

			// display the error in an AlertDialog
			new AlertDialog.Builder(context)
					.setTitle(R.string.error)
					.setMessage(errorMsg)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setNeutralButton(android.R.string.ok, null)
					.show();
		}
	}


	/**
	 * Launches the custom-made YouTube player so that the user can view the selected video.
	 *
	 * @param youTubeVideo HomeVideo to be viewed.
	 */
	public static void launchCustomYouTubePlayer(YouTubeVideo youTubeVideo, Context context) {
		Intent i = new Intent(context, YouTubePlayerActivity.class);
		i.putExtra(YOUTUBE_VIDEO_OBJ, youTubeVideo);
		context.startActivity(i);
	}


	/**
	 * Launch the custom-made YouTube player.
	 */
	private static void launchCustomYouTubePlayer(String videoUrl, Context context) {
		Intent i = new Intent(context, YouTubePlayerActivity.class);
		i.setAction(Intent.ACTION_VIEW);
		i.setData(Uri.parse(videoUrl));
		context.startActivity(i);
	}

}
