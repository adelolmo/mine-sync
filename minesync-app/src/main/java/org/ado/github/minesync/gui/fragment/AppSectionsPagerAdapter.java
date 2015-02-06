/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Andoni del Olmo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.ado.github.minesync.gui.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import org.ado.github.minesync.R;
import org.ado.github.minesync.gui.view.FragmentDetails;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
 * sections of the app.
 *
 * @author andoni
 * @since 1.2.0
 */
public class AppSectionsPagerAdapter extends FragmentPagerAdapter {

    private static final FragmentDetails DROPBOX_FRAGMENT =
            new FragmentDetails(new DropboxFragment(), R.string.label_dropbox_fragment_title, R.layout.fragment_page_dropbox);
    private static final FragmentDetails HISTORY_FRAGMENT =
            new FragmentDetails(new HistoryFragment(), R.string.label_history_fragment_title, R.layout.fragment_page_history);
    private static final FragmentDetails WORLDS_FRAGMENT =
            new FragmentDetails(new WorldsFragment(), R.string.label_worlds_fragment_title, R.layout.fragment_page_worlds);

    private static final FragmentDetails[] FRAGMENT_ARRAY =
            new FragmentDetails[]{DROPBOX_FRAGMENT, HISTORY_FRAGMENT, WORLDS_FRAGMENT};

    private Context context;

    public AppSectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int i) {
        if (i < FRAGMENT_ARRAY.length) {
            return FRAGMENT_ARRAY[i].getFragment();
        } else {
            throw new IllegalStateException(String.format("No tab for index \"%d\"", i));
        }
    }

    @Override
    public int getCount() {
        return FRAGMENT_ARRAY.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getResources().getString(FRAGMENT_ARRAY[position].getStringResourceId());
    }
}
