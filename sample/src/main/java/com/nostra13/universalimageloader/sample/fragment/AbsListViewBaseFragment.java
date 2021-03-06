/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nostra13.universalimageloader.sample.fragment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.sample.Constants;
import com.nostra13.universalimageloader.sample.R;
import com.nostra13.universalimageloader.sample.activity.SimpleImageActivity;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public abstract class AbsListViewBaseFragment extends BaseFragment {

    public static final int alpha_select_image = 100;
    private static final String TAG = AbsListViewBaseFragment.class.getSimpleName();

	protected static final String STATE_PAUSE_ON_SCROLL = "STATE_PAUSE_ON_SCROLL";
	protected static final String STATE_PAUSE_ON_FLING = "STATE_PAUSE_ON_FLING";

    public enum Modes {
        DISPLAY,
        EDIT
    }

	protected AbsListView listView;
    protected BaseAdapter imageAdapter;

	protected boolean pauseOnScroll = false;
	protected boolean pauseOnFling = true;

	protected Modes mode = Modes.DISPLAY;

    public static boolean[] selected_image = new boolean[Constants.IMAGES.length];

	@Override
	public void onResume() {
		super.onResume();
		applyScrollListener();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		MenuItem pauseOnScrollItem = menu.findItem(R.id.item_pause_on_scroll);
		pauseOnScrollItem.setVisible(true);
		pauseOnScrollItem.setChecked(pauseOnScroll);

		MenuItem pauseOnFlingItem = menu.findItem(R.id.item_pause_on_fling);
		pauseOnFlingItem.setVisible(true);
		pauseOnFlingItem.setChecked(pauseOnFling);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.item_pause_on_scroll:
				pauseOnScroll = !pauseOnScroll;
				item.setChecked(pauseOnScroll);
				applyScrollListener();
				return true;
			case R.id.item_pause_on_fling:
				pauseOnFling = !pauseOnFling;
				item.setChecked(pauseOnFling);
				applyScrollListener();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	protected void startImagePagerActivity(int position) {
		Intent intent = new Intent(getActivity(), SimpleImageActivity.class);
		intent.putExtra(Constants.Extra.FRAGMENT_INDEX, ImagePagerFragment.INDEX);
		intent.putExtra(Constants.Extra.IMAGE_POSITION, position);
		startActivity(intent);
	}

	// Atlas
	protected void onPressEventHandler(View view, int pos) {
        Log.d(TAG, "onPressEventHandler mode:" + mode.name());

        if (mode == Modes.DISPLAY) {
			startImagePagerActivity(pos);
		} else if (mode == Modes.EDIT) {
			revertTickSelect(view, pos);
		}
	}

	protected void revertTickSelect(View view, int pos) {
        Log.d(TAG, "selected_image pos[" + pos + "]=" + selected_image[pos]);

        if (selected_image[pos]) {
            unselectAction(view, pos);
        } else if (!selected_image[pos]) {
            selectAction(view, pos);
        }
	}

	protected void selectAction(View view, int pos) {
        if (!selected_image[pos]) {
            selected_image[pos] = true;
            Log.d(TAG, "selectAction pos:"+ pos + "=> " + selected_image[pos]);
            setSelectView(view);
        }
    }

    protected void unselectAction(View view, int pos) {
        if (selected_image[pos]) {

            selected_image[pos] = false;
            unsetSelectView(view);
            Log.d(TAG, "selectAction pos:"+ pos + "=> " + selected_image[pos]);
        }
    }

    public void setSelectView(View view) {
        ImageView icon = (ImageView) view.findViewById(R.id.tick);
        ImageView image = (ImageView) view.findViewById(R.id.image);
        icon.setVisibility(View.VISIBLE);
        image.setAlpha(alpha_select_image);
    }

    protected void unsetSelectView(View view) {
        ImageView icon = (ImageView) view.findViewById(R.id.tick);
        ImageView image = (ImageView) view.findViewById(R.id.image);
        icon.setVisibility(View.INVISIBLE);
        image.setAlpha(255);
    }

    protected void resetTickView() {

        for (int i = 0; i < selected_image.length; i++) {
            selected_image[i] = false;
        }
	}

	protected void onLongPressEventHandler(View view, int pos) {
        Log.d(TAG, "onLongPressEventHandler mode:" + mode.name());

        revertMode();
        if (Modes.EDIT == mode) {
            revertTickSelect(view, pos);
        } else if (Modes.DISPLAY == mode) {
            resetTickView();
        }

	}

	protected void revertMode() {
        mode = (mode == Modes.DISPLAY) ? Modes.EDIT: Modes.DISPLAY;
        Log.d(TAG, "revertMode to mode=" + mode.name());
    }

	private void applyScrollListener() {
		listView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), pauseOnScroll, pauseOnFling));
	}

    protected static int random() {
        int max = 255;
        int min = 0;
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    protected void updateUI(View view, int position) {
        if (selected_image[position]) {
            setSelectView(view);
        } else {
            unsetSelectView(view);
        }
    }
}
