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

package org.ado.minesync.gui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import com.dropbox.sync.android.DbxAccountManager;
import org.ado.minesync.ActivityTracker;
import org.ado.minesync.R;
import org.ado.minesync.commons.ALog;
import org.ado.minesync.gui.fragment.AppSectionsPagerAdapter;
import org.ado.minesync.service.MineSyncService;

import static org.ado.minesync.config.AppConfiguration.getDropboxAccountManager;
import static org.ado.minesync.config.AppConstants.INTENT_PARAMETER_FOREGROUND_WATCHER_ENABLE;
import static org.ado.minesync.config.AppConstants.NOTIFICATION_CONFIGURATION;

public class MineSyncMainActivity extends FragmentActivity implements ActionBar.TabListener {

    public static final int REQUEST_CONFIGURATION_FINISHED = 1;
    private static final String TAG = MineSyncMainActivity.class.getName();
    private static final int REQUEST_LINK_TO_DBX = 0;

    private ActivityTracker activityTracker;
    private MinecraftDropboxStatus minecraftDropboxStatus;
    private UpgradeManager upgradeManager;
    private DbxAccountManager accountManager;

    private boolean configProcessActive;

    private AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    private ViewPager mViewPager;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ALog.d(TAG, "onCreate. savedInstanceState [" + savedInstanceState + "].");
        setContentView(R.layout.activity_main);

        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getApplicationContext(), getSupportFragmentManager());
        final ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        accountManager = getDropboxAccountManager(getApplicationContext());
        activityTracker = new ActivityTracker(getApplicationContext());
        minecraftDropboxStatus = new MinecraftDropboxStatus(this);
        upgradeManager = new UpgradeManager(getApplicationContext());

        upgradeManager.upgradeIfNeeded();
        startServicesIfNeeded();

        ALog.d(TAG, "var configProcessActive [" + configProcessActive + "]");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ALog.d(TAG, "var configProcessActive [" + configProcessActive + "]");
        configProcessActive = isConfigProcessActive(savedInstanceState);
        ALog.d(TAG, "onRestoreInstanceState. savedInstanceState [" + savedInstanceState + "]");
    }

    @Override
    protected void onResume() {
        super.onResume();
        ALog.i(TAG, "onResume.");
        showConfigurationFinishedDialogIfNeeded();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ALog.d(TAG, "onSaveInstanceState");
        outState.putBoolean("config_process_active", configProcessActive);
        ALog.d(TAG, "outState saved [" + outState + "]");
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ALog.d(TAG, "onActivityResult - requestCode[" + requestCode + "] resultCode[" + resultCode + "] data[" + data + "].");
        if (requestCode == REQUEST_LINK_TO_DBX) {
            processActivityResultDropboxLink(resultCode);
        } else if (requestCode == REQUEST_CONFIGURATION_FINISHED) {
            processActivityResultConfigurationFinishedIfNeeded(resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                startActivity(new Intent(this, HelpActivity.class));
                return true;

            case R.id.action_faq:
                startActivity(new Intent(this, FaqActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isConfigProcessActive(Bundle savedInstanceState) {
        return configProcessActive || savedInstanceState != null && savedInstanceState.getBoolean("config_process_active", false);
    }

    private void startServicesIfNeeded() {
        if (accountManager.hasLinkedAccount()) {
            if (activityTracker.isConfigurationProcessFinished()) {
                startMineSyncService();
            }
        }
    }

    private void showConfigurationFinishedDialogIfNeeded() {
        boolean isNeeded = activityTracker.isShowConfigurationProcessFinishedDialogNeeded();
        ALog.d(TAG, "isShowConfigurationProcessFinishedDialogNeeded? [" + isNeeded + "]");
        if (isNeeded) {
            showConfigurationFinishedDialog();
        }
    }

    private void showConfigurationFinishedDialog() {
        ALog.d(TAG, "show configuration finished dialog.");
        startServicesIfNeeded();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.label_dialog_content_config_finished)
                .setTitle(R.string.label_dialog_title_config_finished)
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ALog.d(TAG, "ok button selected");
                        activityTracker.setNeedToShowConfigurationProcessFinishedDialog(false);
                        getNotificationManager(getApplicationContext()).cancel(NOTIFICATION_CONFIGURATION);
                    }
                });
        runOnUiThread(new Runnable() {
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

    private void startMineSyncService() {
        Intent service = new Intent(getApplicationContext(), MineSyncService.class);
        service.setAction(MineSyncService.START_SYNC_ACTION);
        service.putExtra(INTENT_PARAMETER_FOREGROUND_WATCHER_ENABLE, isScreenOn());
        getApplicationContext().startService(service);
    }

    private boolean isScreenOn() {
        return ((PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE)).isScreenOn();
    }

    private void processActivityResultDropboxLink(int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
            ALog.i(TAG, "Successfully link to Dropbox.");
            startConfigActivity();

        } else {
            ALog.w(TAG, "Link to Dropbox failed or was cancelled.");
        }
    }

    private void startConfigActivity() {
        Intent configIntent = new Intent(this, MineSyncConfigActivity.class);
        configIntent.putExtra("status", minecraftDropboxStatus.getStatus());
        startActivityForResult(configIntent, MineSyncMainActivity.REQUEST_CONFIGURATION_FINISHED);
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
}