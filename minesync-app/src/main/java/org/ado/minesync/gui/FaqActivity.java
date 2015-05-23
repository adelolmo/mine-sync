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

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.webkit.WebView;
import org.ado.minesync.R;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

/**
 * Activity to show the FAQ directly from the web.
 *
 * @author andoni
 * @since 17.11.2014
 */
public class FaqActivity extends FragmentActivity {

    private static final String FAQ_URL_TEMPLATE = "https://dl.dropboxusercontent.com/u/2294031/minesync/faq/content_%s.html";
    private static final String DEFAULT_LANGUAGE = Locale.ENGLISH.getLanguage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        final WebView webView = (WebView) findViewById(R.id.webViewFaq);
        webView.loadUrl(getFaqLocalizeUrl());
    }

    private String getFaqLocalizeUrl() {
        final String url = String.format(FAQ_URL_TEMPLATE, getUserLanguage());
        if (isUrlAvailable(url)) {
            return url;
        } else {
            return String.format(FAQ_URL_TEMPLATE, DEFAULT_LANGUAGE);
        }
    }

    private String getUserLanguage() {
        final Locale locale = Locale.getDefault();
        if (locale != null) {
            return StringUtils.isNotBlank(locale.getLanguage()) ? locale.getLanguage() : DEFAULT_LANGUAGE;
        } else {
            return DEFAULT_LANGUAGE;
        }
    }

    private boolean isUrlAvailable(String url) {
        try {
            return new UrlAvailableTask().execute(url).get();
        } catch (Exception e) {
            return false;
        }
    }

    class UrlAvailableTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            final String url = params[0];
            try {
                new URL(url).openConnection().getContent();
                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }
}