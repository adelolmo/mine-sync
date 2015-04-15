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

package org.ado.minesync.minecraft;

import org.ado.minesync.db.WorldEntity;
import org.ado.minesync.github.MockitoTestCase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Class description here.
 *
 * @author andoni
 * @since 08.01.2014
 */
@Ignore
public class MinecraftUtilsTest extends MockitoTestCase {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private static final String SIXTH_MAY = "06.05.1981 00:40:00";
    private static final String SEVENTH_MAY = "07.05.1981 00:40:00";

    @Mock
    private MinecraftWorld worldMock;
    @Mock
    private WorldEntity worldEntity;
    @Mock
    private File fileMock;

    @Before
    public void setUp(){
        when(worldMock.getName()).thenReturn("theWorld");
        when(worldEntity.getName()).thenReturn("world_name");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsWorldChanged_nullMinecraftWorld() throws Exception {
        MinecraftUtils.isWorldChanged(null, worldEntity);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsWorldChanged_nullCacheMinecraftWorld() throws Exception {
        MinecraftUtils.isWorldChanged(worldMock, null);
    }

    @Test
    public void testIsWorldChanged_minecraftWorldEqualsCachedWorld() throws Exception {
        when(worldEntity.getModificationDate()).thenReturn(getDate(SIXTH_MAY));
        when(worldMock.getModificationDate()).thenReturn(getDate(SIXTH_MAY));

        assertFalse("no change", MinecraftUtils.isWorldChanged(worldMock, worldEntity));
    }

    @Test
    public void testIsWorldChanged_minecraftWorldOlderThanCachedWorld() throws Exception {
        when(worldEntity.getModificationDate()).thenReturn(getDate(SEVENTH_MAY));
        when(worldMock.getModificationDate()).thenReturn(getDate(SIXTH_MAY));

        assertFalse("no change", MinecraftUtils.isWorldChanged(worldMock, worldEntity));
    }

    @Test
    public void testIsWorldChanged_minecraftWorldNewerThanCachedWorld() throws Exception {
        when(worldEntity.getModificationDate()).thenReturn(getDate(SIXTH_MAY));
        when(worldMock.getModificationDate()).thenReturn(getDate(SEVENTH_MAY));

        assertTrue("changed", MinecraftUtils.isWorldChanged(worldMock, worldEntity));
    }

    private Date getDate(String stringDate) throws ParseException {
        return SIMPLE_DATE_FORMAT.parse(stringDate);
    }
}