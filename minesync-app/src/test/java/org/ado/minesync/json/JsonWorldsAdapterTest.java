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

package org.ado.minesync.json;

import org.ado.minesync.commons.DateUtils;
import org.ado.minesync.db.SyncTypeEnum;
import org.ado.minesync.db.WorldEntity;
import org.ado.minesync.github.ClassTestCase;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JsonWorldsAdapterTest extends ClassTestCase<JsonWorldsAdapter> {

    @Before
    public void setUp() throws Exception {
        createUnitUnderTest();
    }

    @Test
    public void testGetWorldListToJson() throws Exception {
        JSONObject worldListToJson = unitUnderTest.getWorldListToJson(Arrays.asList(
                createWorldEntity("worldOne", "06.05.1981 00:40:22", 1000, SyncTypeEnum.AUTO),
                createWorldEntity("worldTwo", "21.12.1973 12:30:01", 2000, SyncTypeEnum.MANUAL)
        ));

        assertEquals("json world array",
                "{\"worlds\":[{\"syncType\":1,\"name\":\"worldOne\"},{\"syncType\":0,\"name\":\"worldTwo\"}]}",
                worldListToJson.toJSONString());
    }

    @Test
    public void testGetWorldList() throws Exception {
        File file = File.createTempFile("test", "json");
        FileUtils.write(file, "{\"worlds\":[{\"syncType\":1,\"name\":\"worldOne\"},{\"syncType\":0,\"name\":\"worldTwo\"}]}");

        List<JsonWorld> worldList = unitUnderTest.getWorldList(file);

        assertEquals("world one name", "worldOne", worldList.get(0).getName());
        assertEquals("world one syncType", SyncTypeEnum.AUTO, worldList.get(0).getSyncType());
        assertEquals("world two name", "worldTwo", worldList.get(1).getName());
        assertEquals("world two syncType", SyncTypeEnum.MANUAL, worldList.get(1).getSyncType());
    }

    private WorldEntity createWorldEntity(String name, String creationDate, int size, SyncTypeEnum syncType) {
        return new WorldEntity(name, DateUtils.parse(creationDate), size, syncType);
    }
}