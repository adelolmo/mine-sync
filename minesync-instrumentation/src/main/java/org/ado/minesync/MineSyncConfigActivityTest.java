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

package org.ado.minesync;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import com.google.android.apps.common.testing.ui.espresso.contrib.CountingIdlingResource;
import org.ado.minesync.gui.MineSyncConfigActivity;
import org.ado.minesync.service.UploadDownloadService;
import org.junit.Ignore;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.*;

/**
 * Class description here.
 *
 * @author andoni
 * @since 16.06.2014
 */
@Ignore
@LargeTest
public class MineSyncConfigActivityTest extends ActivityInstrumentationTestCase2<MineSyncConfigActivity> {

    public MineSyncConfigActivityTest() {
        super(MineSyncConfigActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
//        IntentServiceFactory.setUploadDownloadIntentServiceClass(UploadDownloadServiceMock.class);
        MineSyncConfigActivity activity = getActivity();
//        UploadDownloadService uploadDownloadService = activity.getUploadDownloadService();
        CountingIdlingResource countingResource = new CountingIdlingResource("UploadDownloadService");
//        activity.setUploadDownloadService(new MockUploadDownloadService(countingResource));
//        activity.setmConnection(new MockServiceConection(activity));
    }

    public void testUpload() {
        onView(withId(R.id.button_upload_worlds))
                .perform(click());

        onView(withText(R.string.label_upload))
                .check(matches(isDisplayed()));

//        pause(1000);

        onView(withText(R.string.button_ok))
                .perform(click());

    }

    private void pause(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private class MockUploadDownloadService extends UploadDownloadService {

        private CountingIdlingResource countingResource;

        public MockUploadDownloadService(CountingIdlingResource countingResource) {
            this.countingResource = countingResource;
        }

        public MockUploadDownloadService() {
            super(MockUploadDownloadService.class.getName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            log("onHandleIntent - intent [" + intent + "].");
            try {
                if (countingResource != null) {
                    countingResource.increment();
                }
                Intent mockIntent = new Intent(BROADCAST_ACTION);
                mockIntent.putExtra(BROADCAST_PROGRESS, 100);
                mockIntent.putExtra(OPERATION_TITLE, "hola");
                sendBroadcast(mockIntent);
            } finally {
                if (countingResource != null) {
                    countingResource.decrement();
                }
            }
        }

        private void log(String message) {
            Log.d(MockUploadDownloadService.class.getName(), message);
        }
    }

    private class MockServiceConection implements ServiceConnection {
        private MineSyncConfigActivity activity;

        public MockServiceConection(MineSyncConfigActivity activity) {
            this.activity = activity;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
//            activity.setUploadDownloadService(((UploadDownloadService.UploadDownloadBinder) service).getService());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}
