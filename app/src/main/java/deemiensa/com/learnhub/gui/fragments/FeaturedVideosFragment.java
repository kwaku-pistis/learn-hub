/*
 * SkyTube
 * Copyright (C) 2017  Ramon Mifsud
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

package deemiensa.com.learnhub.gui.fragments;


import deemiensa.com.learnhub.App.SkyTubeApp;
import deemiensa.com.learnhub.R;
import deemiensa.com.learnhub.businessobjects.VideoCategory;

/**
 * A fragment that holds featured videos.
 */
public class FeaturedVideosFragment extends VideosGridFragment {

	@Override
	protected VideoCategory getVideoCategory() {
		return VideoCategory.FEATURED;
	}


	@Override
	public String getFragmentName() {
		return SkyTubeApp.getStr(R.string.featured);
	}

}
