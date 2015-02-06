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

package org.ado.github.minesync.dropbox;

import android.content.Context;
import com.dropbox.sync.android.DbxFile;
import org.ado.github.minesync.github.ClassTestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.when;

public class JsonFileHandlerTest extends ClassTestCase<JsonFileHandler> {

    @Mock
    private DbxFile dbxFileMock;
    @Mock
    private FileInfo fileInfoMock;
    @Mock
    private Context contextMock;

    @Before
    public void setUp() throws Exception {
        createUnitUnderTest();
    }

    @Test
    public void testCanHandle_noJsonFileMatch() throws Exception {
        when(fileInfoMock.getPath()).thenReturn("something");

        assertFalse("can't handle", unitUnderTest.canHandle(fileInfoMock));
    }

    @Test
    public void testCanHandle() throws Exception {
        when(fileInfoMock.getPath()).thenReturn(".worlds.json");

        assertTrue("can handle", unitUnderTest.canHandle(fileInfoMock));
    }
}