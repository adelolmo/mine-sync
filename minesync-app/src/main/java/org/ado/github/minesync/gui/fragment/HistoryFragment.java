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

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import org.ado.github.minesync.R;
import org.ado.github.minesync.commons.ALog;
import org.ado.github.minesync.commons.DateUtils;
import org.ado.github.minesync.commons.FileSizeConverter;
import org.ado.github.minesync.db.HistoryActionEnum;
import org.ado.github.minesync.db.MineSyncDbOpenHelper;
import org.ado.github.minesync.db.TableHistoryColumns;
import org.ado.github.minesync.db.TableWorldColumns;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Fragment to display worlds' activity.
 *
 * @author andoni
 * @since 1.2.0
 */
public class HistoryFragment extends Fragment {

    private static final String TAG = HistoryFragment.class.getName();
    private static final int LIST_VIEW_SIZE = 50;

    private MineSyncDbOpenHelper dbHelper;
    private SwipeRefreshLayout swipeLayoutHistory;
    private ListView listViewHistory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ALog.d(TAG, "onCreateView. inflater [" + inflater
                + "] container [" + container
                + "] savedInstanceState [" + savedInstanceState + "].");

        return inflater.inflate(R.layout.fragment_page_history, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        swipeLayoutHistory = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeLayoutHistory);
        swipeLayoutHistory.setColorScheme(R.color.holo_blue_dark,
                R.color.holo_blue_light,
                R.color.holo_green_light,
                R.color.holo_green_dark);
        swipeLayoutHistory.setOnRefreshListener(new OnRefreshWorldsListener());
    }

    @Override
    public void onResume() {
        super.onResume();
        dbHelper = MineSyncDbOpenHelper.getInstance(getActivity());
        ALog.d(TAG, "onResume");
        displayListView(getActivity());
    }

    private void displayListView(Activity activity) {
        String[] fromColumns =
                new String[]{TableWorldColumns.WORLD_NAME_COLUMN,
                        TableHistoryColumns.HISTORY_DATE,
                        TableHistoryColumns.HISTORY_ACTION,
                        TableHistoryColumns.HISTORY_SIZE};
        int[] toLayoutIDs =
                new int[]{R.id.textViewWorldName,
                        R.id.textViewHistoryDate,
                        R.id.imageViewHistoryAction,
                        R.id.textViewHistorySize};
        Cursor cursor = dbHelper.getHistoryViewCursorAll(LIST_VIEW_SIZE);
        SimpleCursorAdapter cursorAdapter =
                new CustomSimpleCursorAdapter(activity,
                        R.layout.list_history_entry,
                        cursor,
                        fromColumns,
                        toLayoutIDs,
                        CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        listViewHistory = (ListView) activity.findViewById(R.id.listViewHistory);
        listViewHistory.setFastScrollEnabled(true);
        listViewHistory.setAdapter(cursorAdapter);
    }

    private class CustomSimpleCursorAdapter extends SimpleCursorAdapter {
        private Resources resources;

        public CustomSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from,
                                         int[] to, int flags) {
            super(context, layout, c, from, to, flags);
            resources = context.getResources();
        }

        @Override
        public void setViewText(TextView v, String text) {
            super.setViewText(v, text);
            if (R.id.textViewHistoryDate == v.getId()) {
                super.setViewText(v, getFormattedDate(DateUtils.parseSqlLiteDate(text)));
            } else if (R.id.textViewHistorySize == v.getId()) {
                super.setViewText(v, FileSizeConverter.getSize(Long.valueOf(text), FileSizeConverter.SizeUnit.MB));
            }
        }

        @Override
        public void setViewImage(ImageView v, String value) {
            if (R.id.imageViewHistoryAction == v.getId()) {
                if (StringUtils.equals(String.valueOf(HistoryActionEnum.DOWNLOAD.getActionId()), value)) {
                    v.setBackgroundColor(getResources().getColor(R.color.dropbox_blue));
                } else {
                    v.setBackgroundColor(getResources().getColor(R.color.green));
                }
            }
        }

        private String getFormattedDate(Date date) {
            String formattedDate;
            Calendar todayCalendar = Calendar.getInstance();

            Calendar yesterdayCalendar = Calendar.getInstance();
            yesterdayCalendar.add(Calendar.DAY_OF_MONTH, -1);

            Calendar historyCalendar = Calendar.getInstance();
            historyCalendar.setTime(date);

            if (todayCalendar.get(Calendar.DAY_OF_MONTH) == historyCalendar.get(Calendar.DAY_OF_MONTH)) {
                formattedDate = String.format(resources.getString(R.string.txt_date_today_time),
                        new SimpleDateFormat(resources.getString(R.string.time_format)).format(date));

            } else if (yesterdayCalendar.get(Calendar.DAY_OF_MONTH) == historyCalendar.get(Calendar.DAY_OF_MONTH)) {
                formattedDate = String.format(resources.getString(R.string.txt_date_yesterday_time),
                        new SimpleDateFormat(resources.getString(R.string.time_format)).format(date));

            } else {
                formattedDate = new SimpleDateFormat(resources.getString(R.string.date_time_format)).format(date);
            }

            return formattedDate;
        }
    }

    private class OnRefreshWorldsListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeLayoutHistory.setRefreshing(true);
                    SimpleCursorAdapter adapter = (SimpleCursorAdapter) listViewHistory.getAdapter();
                    adapter.changeCursor(dbHelper.getHistoryViewCursorAll(LIST_VIEW_SIZE));
                    swipeLayoutHistory.setRefreshing(false);
                }
            }, 1000);
        }
    }
}