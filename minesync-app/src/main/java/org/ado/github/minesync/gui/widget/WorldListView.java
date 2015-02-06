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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.ado.github.minesync.R;
import org.ado.github.minesync.commons.ALog;
import org.ado.github.minesync.commons.FileSizeConverter;
import org.ado.github.minesync.db.SyncTypeEnum;
import org.ado.github.minesync.gui.fragment.MinecraftWorldUnit;
import org.ado.github.minesync.minecraft.MinecraftData;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.commons.lang.Validate.notNull;

/**
 * Class description here.
 *
 * @author andoni
 * @since 1.2.0
 */
public class WorldListView extends LinearLayout {

    private static final String TAG = WorldListView.class.getName();

    private ViewHolder views;
    private MinecraftData minecraftData;

    public WorldListView(Context context, MinecraftWorldUnit minecraftWorld) {
        super(context);
        notNull(minecraftWorld, "minecraftWorld cannot be null");

        minecraftData = new MinecraftData();
        View inflate = inflate(context, R.layout.list_worlds_entry, null);
        inflate.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(inflate);
        setData(minecraftWorld);
    }

    public void setData(MinecraftWorldUnit minecraftWorld) {
        if (views == null) {
            ALog.d(TAG, "create new ViewHolder instance for minecraftWorld " + minecraftWorld);
            views = new ViewHolder();
            views.layout = (LinearLayout) findViewById(R.id.listWorldsEntry);
            views.name = (TextView) findViewById(R.id.textViewWorldListWorldName);
            views.date = (TextView) findViewById(R.id.textViewWorldListDate);
            views.size = (TextView) findViewById(R.id.textViewWorldListSize);
            views.syncType = (ImageView) findViewById(R.id.imageViewSyncType);
        }
        views.name.setText(minecraftWorld.getName());
        views.date.setText(getFormattedDate(minecraftWorld.getModificationDate()));
        views.size.setText(getSize(minecraftWorld));
        views.syncType.setBackgroundColor(getSyncBackgroundColor(minecraftWorld.getSyncType()));
    }

    private int getSyncBackgroundColor(SyncTypeEnum syncType) {
        switch (syncType) {
            case AUTO:
                return getAutoSyncColor();
            case MANUAL:
                return getManualSyncColor();
        }
        return android.R.color.white;
    }

    private int getManualSyncColor() {
        return getResources().getColor(android.R.color.darker_gray);
    }

    private int getAutoSyncColor() {
        return getResources().getColor(R.color.dropbox_blue);
    }

    private String getFormattedDate(Date date) {
        if (date != null) {
            return new SimpleDateFormat(getResources().getString(R.string.date_time_format)).format(date);
        } else {
            return "";
        }
    }

    private String getSize(MinecraftWorldUnit minecraftWorld) {
        return FileSizeConverter.getSize(minecraftWorld != null ? minecraftWorld.getSize() : minecraftWorld.getSize(), FileSizeConverter.SizeUnit.MB);
    }

    /**
     * Used to further optimize the getting of Views
     */
    private static class ViewHolder {
        LinearLayout layout;
        TextView name;
        TextView date;
        TextView size;
        ImageView syncType;
    }
}