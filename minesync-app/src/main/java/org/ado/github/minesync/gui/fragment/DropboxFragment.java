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
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import org.ado.github.minesync.ActivityTracker;
import org.ado.github.minesync.R;
import org.ado.github.minesync.commons.ALog;
import org.ado.github.minesync.gui.MineSyncConfigActivity;
import org.ado.github.minesync.gui.MineSyncMainActivity;
import org.ado.github.minesync.gui.MinecraftDropboxStatus;
import org.ado.github.minesync.minecraft.MinecraftUtils;
import org.ado.github.minesync.service.MineSyncService;

import static org.ado.github.minesync.config.AppConfiguration.getDropboxAccountManager;
import static org.ado.github.minesync.config.AppConstants.*;

/**
 * Fragment to show the dropbox configuration.
 *
 * @author andoni
 * @since 1.2.0
 */
public class DropboxFragment extends Fragment {

    private static final String TAG = DropboxFragment.class.getName();
    private static final int REQUEST_LINK_TO_DBX = 0;
    private Activity activity;
    private DbxAccountManager accountManager;
    private ActivityTracker activityTracker;
    private MinecraftDropboxStatus minecraftDropboxStatus;

    private Button linkButton;
    private Button unlinkButton;
    private TextView textDropboxStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ALog.d(TAG, "onCreateView. inflater [" + inflater
                + "] container [" + container
                + "] savedInstanceState [" + savedInstanceState + "].");

        accountManager = getDropboxAccountManager(getActivity());
        accountManager.addListener(new DropboxAccountStateListener(getActivity()));
        activityTracker = new ActivityTracker(getActivity().getApplicationContext());
        minecraftDropboxStatus = new MinecraftDropboxStatus(getActivity());
        return inflater.inflate(R.layout.fragment_page_dropbox, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
        initView();
        ALog.d(TAG, "onCreateView. savedInstanceState [" + savedInstanceState + "].");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (accountManager.hasLinkedAccount()) {
            setViewDropboxAccountLinked();
            notifyDropboxAccountLinked(getActivity());
        } else {
            setViewDropboxAccountNotLinked();
            notifyDropboxAccountUnlinked(getActivity());
        }
        showConfigurationFinishedDialogIfNeeded();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initView() {
        linkButton = (Button) activity.findViewById(R.id.button_dropbox_link);
        linkButton.setOnClickListener(new LinkButtonClickListener(activity));
        unlinkButton = (Button) activity.findViewById(R.id.button_dropbox_unlink);
        unlinkButton.setOnClickListener(new UnlinkButtonClickListener(activity));
        textDropboxStatus = (TextView) getActivity().findViewById(R.id.textDropboxStatus);
        if (accountManager.hasLinkedAccount()) {
            setViewDropboxAccountLinked();
        } else {
            setViewDropboxAccountNotLinked();
        }
    }

    private void showConfigurationFinishedDialogIfNeeded() {
        boolean isNeeded = activityTracker.isShowConfigurationProcessFinishedDialogNeeded();
        ALog.d(TAG, "isShowConfigurationProcessFinishedDialogNeeded? [" + isNeeded + "]");
        if (isNeeded) {
            showConfigurationFinishedDialog();
        }
    }

    private void unlinkDropboxAccount(Activity fragmentActivity) {
        stopServices();
        accountManager.unlink();
        resetConfigurationProcess();
        notifyDropboxAccountUnlinked(fragmentActivity);
    }

    private void resetConfigurationProcess() {
        activityTracker.setNeedToShowConfigurationProcessFinishedDialog(false);
        activityTracker.setConfigurationProcess(false);
    }

    private void stopServices() {
        stopMineSyncService();
    }

    private void stopMineSyncService() {
        activity.getApplicationContext()
                .stopService(new Intent(activity.getApplicationContext(), MineSyncService.class));
    }

    private void setViewDropboxAccountLinked() {
        linkButton.setEnabled(false);
        unlinkButton.setEnabled(true);
        textDropboxStatus
                .setText(String.format(getResources().getString(R.string.txt_link_status_linked_to),
                        getDropboxUserAccountName()));
    }

    private void setViewDropboxAccountNotLinked() {
        linkButton.setEnabled(true);
        unlinkButton.setEnabled(false);
        textDropboxStatus.setText(getResources().getString(R.string.txt_link_status_not_linked));
    }

    private void notifyDropboxAccountLinked(Context fragmentActivity) {
        Intent dropboxAccountIntent = new Intent(INTENT_DROPBOX_ACCOUNT);
        dropboxAccountIntent.putExtra(INTENT_PARAMETER_ACCOUNT_STATUS, INTENT_PARAMETER_VALUE_LINKED);
        fragmentActivity.sendBroadcast(dropboxAccountIntent);
    }

    private void notifyDropboxAccountUnlinked(Activity fragmentActivity) {
        Intent dropboxAccountIntent = new Intent(INTENT_DROPBOX_ACCOUNT);
        dropboxAccountIntent.putExtra(INTENT_PARAMETER_ACCOUNT_STATUS, INTENT_PARAMETER_VALUE_UNLINKED);
        fragmentActivity.sendBroadcast(dropboxAccountIntent);
    }

    private String getDropboxUserAccountName() {
        String displayName = getResources().getString(R.string.txt_dropbox_account_unknown);
        if (accountManager.getLinkedAccount() != null) {
            displayName = accountManager.getLinkedAccount().getUserId();
        }
        if (accountManager.getLinkedAccount().getAccountInfo() != null) {
            displayName = accountManager.getLinkedAccount().getAccountInfo().displayName;
        }
        return displayName;
    }

    private void startServicesIfNeeded() {
        if (this.accountManager.hasLinkedAccount()) {
            if (this.activityTracker.isConfigurationProcessFinished()) {
                startMineSyncService();
            }
        }
    }

    private void processActivityResultDropboxLink(int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
            ALog.i(TAG, "Successfully link to Dropbox.");
            setViewDropboxAccountLinked();
            startConfigActivity();

        } else {
            Log.w(TAG, "Link to Dropbox failed or was cancelled.");
            setViewDropboxAccountNotLinked();
        }
    }

    private void processActivityResultConfigurationFinishedIfNeeded(int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            boolean isNeeded = activityTracker.isShowConfigurationProcessFinishedDialogNeeded();
            ALog.d(TAG, "isShowConfigurationProcessFinishedDialogNeeded? [" + isNeeded + "]");
            if (isNeeded) {
                showConfigurationFinishedDialog();
                activityTracker.setNeedToShowConfigurationProcessFinishedDialog(false);
            }
        }
    }

    private void showConfigurationFinishedDialog() {
        ALog.d(TAG, "show configuration finished dialog.");
        startServicesIfNeeded();
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.label_dialog_content_config_finished)
                .setTitle(R.string.label_dialog_title_config_finished)
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ALog.d(TAG, "ok button selected");
                        activityTracker.setNeedToShowConfigurationProcessFinishedDialog(false);
                        getNotificationManager(activity.getApplicationContext()).cancel(NOTIFICATION_CONFIGURATION);
                    }
                });
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void startConfigActivity() {
        Intent configIntent = new Intent(activity, MineSyncConfigActivity.class);
        configIntent.putExtra("status", minecraftDropboxStatus.getStatus());
        startActivityForResult(configIntent, MineSyncMainActivity.REQUEST_CONFIGURATION_FINISHED);
    }

    private void startMineSyncService() {
        Intent service = new Intent(activity, MineSyncService.class);
        service.setAction(MineSyncService.START_SYNC_ACTION);
        service.putExtra(INTENT_PARAMETER_FOREGROUND_WATCHER_ENABLE, isScreenOn());
        activity.startService(service);
    }

    private boolean isScreenOn() {
        return ((PowerManager) activity.getApplicationContext().getSystemService(Context.POWER_SERVICE)).isScreenOn();
    }

    private class DropboxAccountStateListener implements DbxAccountManager.AccountListener {
        private FragmentActivity fragmentActivity;

        public DropboxAccountStateListener(FragmentActivity activity) {
            this.fragmentActivity = activity;
        }

        @Override
        public void onLinkedAccountChange(DbxAccountManager dbxAccountManager, DbxAccount dbxAccount) {
            // TODO check if fragment is attach
            if (dbxAccount.isLinked()) {
                setViewDropboxAccountLinked();
                notifyDropboxAccountLinked(fragmentActivity);
            } else {
                setViewDropboxAccountNotLinked();
                notifyDropboxAccountUnlinked(fragmentActivity);
            }
        }
    }

    private class LinkButtonClickListener implements View.OnClickListener {
        private Activity fragmentActivity;

        private LinkButtonClickListener(Activity fragmentActivity) {
            this.fragmentActivity = fragmentActivity;
        }

        @Override
        public void onClick(View v) {
            Log.i(TAG, "button link to dropbox was clicked.");

            if (MinecraftUtils.isMinecraftInstalled()) {
                accountManager.startLink(fragmentActivity, REQUEST_LINK_TO_DBX);
            } else {
                final AlertDialog.Builder builder = new AlertDialog.Builder(fragmentActivity);
                builder.setMessage(R.string.label_dialog_content_minecraft_not_found)
                        .setTitle(R.string.label_dialog_title_minecraft_not_found)
                        .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ALog.d(TAG, "ok button selected");
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    private class UnlinkButtonClickListener implements View.OnClickListener {
        private Activity fragmentActivity;

        private UnlinkButtonClickListener(Activity fragmentActivity) {
            this.fragmentActivity = fragmentActivity;
        }

        @Override
        public void onClick(View arg0) {
            setViewDropboxAccountNotLinked();
            unlinkDropboxAccount(fragmentActivity);
        }
    }
}