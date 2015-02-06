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

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.dropbox.sync.android.DbxAccountManager;
import org.ado.github.minesync.R;
import org.ado.github.minesync.commons.ALog;
import org.ado.github.minesync.config.AppConfiguration;
import org.ado.github.minesync.db.MineSyncDbOpenHelper;
import org.ado.github.minesync.db.SyncTypeEnum;
import org.ado.github.minesync.db.WorldEntity;
import org.ado.github.minesync.gui.receiver.JsonUpdateReceiver;
import org.ado.github.minesync.gui.widget.WorldListAdapter;
import org.ado.github.minesync.minecraft.MinecraftUtils;
import org.ado.github.minesync.minecraft.MinecraftWorldManager;
import org.ado.github.minesync.service.OperationTypeEnum;
import org.ado.github.minesync.service.UploadDownloadService;

import java.util.ArrayList;
import java.util.List;

/**
 * This fragment shows the list of worlds.
 *
 * @author andoni
 * @since 1.2.0
 */
public class WorldsFragment extends Fragment {

    private static final String TAG = WorldsFragment.class.getName();

    private MineSyncDbOpenHelper dbHelper;
    private MinecraftWorldManager minecraftWorldManager;
    private MinecraftWorldUnitFactory minecraftWorldUnitFactory;

    private SwipeRefreshLayout swipeLayoutWorlds;
    private ListView listViewWorlds;
    private List<WorldEntity> worldList;
    private JsonUpdateReceiver jsonUpdateReceiver;
    private IntentFilter jsonUpdateFilter;
    private int selectedWorld = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ALog.d(TAG, "onCreateView. inflater [" + inflater
                + "] container [" + container
                + "] savedInstanceState [" + savedInstanceState + "].");

        return inflater.inflate(R.layout.fragment_page_worlds, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ALog.d(TAG, "onActivityCreated. savedInstanceState [" + savedInstanceState + "].");
        minecraftWorldManager = new MinecraftWorldManager(
                AppConfiguration.getDropboxAccountManager(getActivity()),
                getActivity());
        minecraftWorldUnitFactory = new MinecraftWorldUnitFactory(getActivity());

        worldList = new ArrayList<WorldEntity>();
        jsonUpdateReceiver = new JsonUpdateReceiver();
        jsonUpdateFilter = new IntentFilter(JsonUpdateReceiver.JSON_UPDATE);
        swipeLayoutWorlds = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeLayoutWorlds);
        swipeLayoutWorlds.setColorScheme(R.color.holo_blue_dark,
                R.color.holo_blue_light,
                R.color.holo_green_light,
                R.color.holo_green_dark);
        swipeLayoutWorlds.setOnRefreshListener(new OnRefreshWorldsListener());
    }

    @Override
    public void onResume() {
        super.onResume();
        ALog.d(TAG, "onResume");
        dbHelper = MineSyncDbOpenHelper.getInstance(getActivity());
        worldList = dbHelper.getWorldAll();
        displayListView();
        getActivity().registerReceiver(jsonUpdateReceiver, jsonUpdateFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        ALog.d(TAG, "onPause");
        getActivity().unregisterReceiver(jsonUpdateReceiver);
    }

    private void displayListView() {
        listViewWorlds = (ListView) getActivity().findViewById(R.id.listViewWorlds);
        if (listViewWorlds != null) {
            listViewWorlds.setFastScrollEnabled(true);
            final DbxAccountManager dropboxAccountManager = AppConfiguration.getDropboxAccountManager(getActivity());
            if (dropboxAccountManager.hasLinkedAccount()) {
                listViewWorlds.setOnItemClickListener(new OnWorldClick());
            }
            populateWorldListContent();
        }
    }

    private void showToast(int messageId) {
        Toast.makeText(getActivity(), getResources().getString(messageId), Toast.LENGTH_LONG).show();
    }

    private void populateWorldListContent() {
        try {
            List<MinecraftWorldUnit> minecraftWorldUnitList = minecraftWorldUnitFactory.getList();
            if (getListAdapter() == null) {
                setListAdapter(new WorldListAdapter(this, minecraftWorldUnitList));
            } else {
                getListAdapter().replace(minecraftWorldUnitList);
            }
        } catch (Exception e) {
            ALog.e(TAG, e, "Cannot load world's list");
        }
    }

    private WorldListAdapter getListAdapter() {
        return (WorldListAdapter) listViewWorlds.getAdapter();
    }

    private void setListAdapter(WorldListAdapter worldListAdapter) {
        listViewWorlds.setAdapter(worldListAdapter);
    }

    private void refreshWorldList() {
        populateWorldListContent();
    }

    private void uploadWorld(String worldName, SyncTypeEnum syncType) {
        Intent service = new Intent(getActivity(), UploadDownloadService.class);
        service.putExtra(UploadDownloadService.OPERATION_TYPE, OperationTypeEnum.UPLOAD);
        service.putExtra(UploadDownloadService.OPERATION_WORLD_NAME, worldName);
        service.putExtra(UploadDownloadService.OPERATION_WORLD_SYNC_TYPE, syncType);
        getActivity().startService(service);
    }

    private void downloadWorld(String worldName, SyncTypeEnum syncType) {
        Intent service = new Intent(getActivity(), UploadDownloadService.class);
        service.putExtra(UploadDownloadService.OPERATION_TYPE, OperationTypeEnum.DOWNLOAD);
        service.putExtra(UploadDownloadService.OPERATION_WORLD_NAME, worldName);
        service.putExtra(UploadDownloadService.OPERATION_WORLD_SYNC_TYPE, syncType);
        getActivity().startService(service);
    }

    private class OnRefreshWorldsListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            (new Handler()).postDelayed(new Runnable() {

                @Override
                public void run() {
                    swipeLayoutWorlds.setRefreshing(true);
                    refreshWorldList();
                    swipeLayoutWorlds.setRefreshing(false);
                }
            }, 1000);
        }
    }

    private class OnWorldClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            selectedWorld = position;
            view.setSelected(true);
            getActivity().startActionMode(new ModeCallBack());
        }
    }

    private class ModeCallBack implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.fragment_worlds_cab, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            if (selectedWorld != -1) {
                MinecraftWorldUnit selected = (MinecraftWorldUnit) listViewWorlds.getAdapter().getItem(selectedWorld);
                if (selected.getSyncType() == SyncTypeEnum.AUTO) {
                    menu.findItem(R.id.action_sync_auto).setVisible(false);
                    menu.findItem(R.id.action_sync_manual).setVisible(true);
                } else {
                    menu.findItem(R.id.action_sync_auto).setVisible(true);
                    menu.findItem(R.id.action_sync_manual).setVisible(false);
                }
                if (!MinecraftUtils.worldExist(selected.getName())) {
                    menu.findItem(R.id.action_upload).setIcon(R.drawable.ic_action_upload_disable);
                }
            }
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            MinecraftWorldUnit selected = (MinecraftWorldUnit) listViewWorlds.getAdapter().getItem(selectedWorld);
            ALog.d(TAG, "Menu option selected [" + item.toString() + "] world [" + selected + "]");

            // TODO check if dropbox account is linked
            switch (item.getItemId()) {
                case R.id.action_sync_auto:
                    if (selected.getSyncType() == SyncTypeEnum.MANUAL) {
                        mode.finish();
                        showToast(R.string.toast_sync_auto_set);
                        minecraftWorldManager.changeSyncType(selected.getName(), SyncTypeEnum.AUTO, selected.getModificationDate(), selected.getSize());
                        refreshWorldList();
                    } else {
                        mode.finish();
                        showToast(R.string.toast_sync_auto_already);
                    }
                    break;
                case R.id.action_sync_manual:
                    if (selected.getSyncType() == SyncTypeEnum.AUTO) {
                        mode.finish();
                        showToast(R.string.toast_sync_manual_set);
                        minecraftWorldManager.changeSyncType(selected.getName(), SyncTypeEnum.MANUAL, selected.getModificationDate(), selected.getSize());
                        refreshWorldList();
                    } else {
                        mode.finish();
                        showToast(R.string.toast_sync_manual_already);
                    }
                    break;
                case R.id.action_upload:
                    if (MinecraftUtils.worldExist(selected.getName())) {
                        mode.finish();
                        showToast(R.string.toast_upload_world);
                        uploadWorld(selected.getName(), selected.getSyncType());
                    } else {
                        showToast(R.string.toast_upload_world_disable);

                    }
                    break;
                case R.id.action_download:
                    mode.finish();
                    showToast(R.string.toast_download_world);
                    downloadWorld(selected.getName(), selected.getSyncType());
                    break;
                default:
                    mode.finish();
                    return false;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    }
}