/*
 * SkyTube
 * Copyright (C) 2018  Ramon Mifsud
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

import android.content.Context;
import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import deemiensa.com.learnhub.R;

/**
 * Material dialog for SkyTube in which the theme colors are pre-set for convenience.
 */
public class SkyTubeMaterialDialog extends MaterialDialog.Builder {

	public SkyTubeMaterialDialog(@NonNull Context context) {
		super(context);

		titleColorRes(R.color.dialog_title);
		backgroundColorRes(R.color.dialog_backgound);
		contentColorRes(R.color.dialog_content_text);
		positiveColorRes(R.color.dialog_positive_text);
		negativeColorRes(R.color.dialog_negative_text);
		neutralColorRes(R.color.dialog_neutral_text);

		positiveText(R.string.ok);
		negativeText(R.string.cancel);

		onNegative(new MaterialDialog.SingleButtonCallback() {
			@Override
			public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
				dialog.dismiss();
			}
		});
	}

}
