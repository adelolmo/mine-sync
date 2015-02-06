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

package org.ado.github.minesync.gui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Android Pull to Refresh list view widget.
 * <p/>
 * from: http://www.survivingwithandroid.com/2013/06/android-listview-pull-to-refresh.html
 *
 * @author andoni
 * @since 1.2.0
 */
public class PullDownListView extends ListView {

    private static final String TAG = PullDownListView.class.getName();
    private static final float THRESHOLD = 120;

    private boolean STATE_REFRESHING = false;
    private boolean STATE_REFRESH_ENABLED = false;
    private float startY;
    private OnRefreshListListener refreshListListener;

    public PullDownListView(Context context) {
        super(context);
    }

    public PullDownListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullDownListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setRefreshListListener(OnRefreshListListener refreshListListener) {
        this.refreshListListener = refreshListListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        ALog.d(TAG, "First [" + this.getFirstVisiblePosition() + "]");
//        System.out.println("First [" + this.getFirstVisiblePosition() + "]");
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE: {
                if ((y - startY) > THRESHOLD && STATE_REFRESH_ENABLED && !STATE_REFRESHING) {
                    refreshListListener.onRefresh();
                }
            }
            break;
            case MotionEvent.ACTION_DOWN: {
                startY = y;
                STATE_REFRESH_ENABLED = getFirstVisiblePosition() == 0; // We are on the first element so we can enable refresh
            }
            case MotionEvent.ACTION_UP: {
                STATE_REFRESHING = false;
            }

        }
        return super.onTouchEvent(event);
    }

    public interface OnRefreshListListener {
        public void onRefresh();
    }
}