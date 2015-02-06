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

package org.ado.github.minesync.github;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import org.ado.atf.AndroidTestFramework;

import java.io.File;
import java.io.IOException;

/**
 * Class description here.
 *
 * @author andoni
 * @since 14.03.2014
 */
public class AndroidTestCase<T> extends ClassTestCase<T> {

    private static final String TAG = AndroidTestCase.class.getName();
    private static final File CURRENT_DIRECTORY = new File(System.getProperty("user.dir"));

    static {
        try {
            AndroidTestFramework.setAndroidManifestFile(new File(CURRENT_DIRECTORY, "AndroidManifest.xml"));
            AndroidTestFramework.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //    @Mock
    protected Context contextMock;
    //    @Mock
    protected PackageManager packageManagerMock;
    protected PackageInfo packageInfoMock;

    @Override
    public void createUnitUnderTest() {
        try {
            setupEnvironment();
            super.createUnitUnderTest();
        } catch (Exception e) {
            // ignore
        }
    }

    @Override
    protected void createUnitUnderTest(T unitUnderTest) {
        try {
            setupEnvironment();
            super.createUnitUnderTest(unitUnderTest);
        } catch (Exception e) {
            // ignore
        }
    }

    protected void addSharedPreference(String key, Object value) {

    }

    private void setupEnvironment() {
        System.out.println("Setup Android environment.");
        try {
            contextMock = AndroidTestFramework.getContext();
//            addMock(contextMock);

//            when(contextMock.getFilesDir()).thenReturn(APPLICATION_FILES_DIR);
//            when(contextMock.getPackageManager()).thenReturn(packageManagerMock);
//            packageInfoMock = new PackageInfo();
//            when(packageManagerMock.getPackageInfo("org.ado.minesync", 0)).thenReturn(packageInfoMock);
        } catch (Exception e) {
            throw new RuntimeException("Cannot initialize Android Test Framework", e);
        }
    }
}