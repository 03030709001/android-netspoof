/*
 * This file is part of Network Spoofer for Android.
 * Network Spoofer - change and mess with webpages and the internet on
 * other people's computers
 * Copyright (C) 2011 Will Shackleton
 *
 * Network Spoofer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Network Spoofer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Network Spoofer, in the file COPYING.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package uk.digitalsquid.netspoofer.spoofs;

import java.io.Serializable;

import android.app.Dialog;
import android.content.Context;

public abstract class Spoof implements Serializable {
	private static final long serialVersionUID = -3207729013734241941L;
	
	private final String title, description;
	
	public Spoof(String title, String description) {
		this.description = description;
		this.title = title;
	}

	public abstract String getSpoofCmd(String victim, String router);
	public abstract String getStopCmd();
	
	public abstract Dialog displayExtraDialog(Context context, OnExtraDialogDoneListener onDone);
	
	public String getDescription() {
		return description;
	}
	
	public String getTitle() {
		return title;
	}

	public static interface OnExtraDialogDoneListener {
		void onDone();
	}
}
