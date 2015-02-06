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

package org.ado.github.minesync;

/**
 * Class description here.
 *
 * @author andoni
 * @since 12.06.2014
 */

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import org.ado.github.minesync.gui.MineSyncMainActivity;
import org.junit.Ignore;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.*;

/**
 * Highlights basic
 * {@link com.google.android.apps.common.testing.ui.espresso.Espresso#onView(org.hamcrest.Matcher)}
 * functionality.
 */
@Ignore
@LargeTest
public class BasicTest extends ActivityInstrumentationTestCase2<MineSyncMainActivity> {

//    @SuppressWarnings("deprecation")
    public BasicTest() {
        // This constructor was deprecated - but we want to support lower API levels.
        super(MineSyncMainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        // Espresso will not launch our activity for us, we must launch it via getActivity().
        getActivity();
    }

    public void testDropboxUnlink() {
        onView(withId(R.id.button_dropbox_unlink))
                .perform(click());

        onView(withId(R.id.button_dropbox_link))
                .check(matches(isEnabled()));
    }

    public void testDropboxLink() {
        onView(withId(R.id.button_dropbox_link))
                .perform(click());

        onView(withText("Zulassen"))
                .perform(click());

        onView(withId(R.id.button_upload_worlds))
                .perform(click());

        onView(withText(R.string.label_upload))
                .check(matches(isDisplayed()));
//        onView(withId(R.id.button_dropbox_unlink))
//                .check(matches(isEnabled()));
    }

   /* public void testTypingAndPressBack() {
        // Close soft keyboard after type to avoid issues on devices with soft keyboard.
        onView(withId(R.id.sendtext_simple))
                .perform(typeText("Have a cup of Espresso."), closeSoftKeyboard());

        onView(withId(R.id.send_simple))
                .perform(click());

        // Clicking launches a new activity that shows the text entered above. You don't need to do
        // anything special to handle the activity transitions. Espresso takes care of waiting for the
        // new activity to be resumed and its view hierarchy to be laid out.
        onView(withId(R.id.display_data))
                .check(matches(withText(("Have a cup of Espresso."))));

        // Going back to the previous activity - lets make sure our text was perserved.
        pressBack();

        onView(withId(R.id.sendtext_simple))
                .check(matches(withText(containsString("Espresso"))));
    }

    @SuppressWarnings("unchecked")
    public void testClickOnSpinnerItemAmericano(){
        // Open the spinner.
        onView(withId(R.id.spinner_simple))
                .perform(click());
        // Spinner creates a List View with its contents - this can be very long and the element not
        // contributed to the ViewHierarchy - by using onData we force our desired element into the
        // view hierarchy.
        onData(allOf(is(instanceOf(String.class)), is("Americano")))
                .perform(click());

        onView(withId(R.id.spinnertext_simple))
                .check(matches(withText(containsString("Americano"))));
    }*/
}

