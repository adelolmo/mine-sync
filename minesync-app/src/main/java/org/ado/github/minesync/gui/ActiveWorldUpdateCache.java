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

package org.ado.github.minesync.gui;

import org.ado.github.minesync.commons.ALog;
import org.ado.github.minesync.gui.widget.WorldListView;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds the list of worlds that are shown as being synced in the World's UI view.
 *
 * @author andoni
 * @since 1.2.0
 */
public class ActiveWorldUpdateCache {

    private static final String TAG = ActiveWorldUpdateCache.class.getName();
    private static ActiveWorldUpdateCache ourInstance = new ActiveWorldUpdateCache();
    private Map<String, WorldListView> map;

    private ActiveWorldUpdateCache() {
        map = new HashMap<String, WorldListView>();
    }

    public static ActiveWorldUpdateCache getInstance() {
        return ourInstance;
    }

    public void add(String worldName, WorldListView worldListView) {
        ALog.v(TAG, "add world [" + worldName + "]");
        map.put(worldName, worldListView);
    }

    public WorldListView get(String worldName) {
        return map.get(worldName);
    }

    public void remove(String worldName) {
        ALog.v(TAG, "remove world [" + worldName + "]");
        map.remove(worldName);
    }
}