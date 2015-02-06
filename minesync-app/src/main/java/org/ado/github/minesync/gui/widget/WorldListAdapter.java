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

package org.ado.github.minesync.gui.widget;

import android.view.View;
import android.view.ViewGroup;
import org.ado.github.minesync.gui.fragment.MinecraftWorldUnit;
import org.ado.github.minesync.gui.fragment.WorldsFragment;
import org.ado.github.minesync.gui.util.ArrayAdapter;

import java.util.List;

/**
 * View list adapter for <code>WorldEntity</code>.
 *
 * @author andoni
 * @since 1.2.0
 */
public class WorldListAdapter extends ArrayAdapter<MinecraftWorldUnit> {

    WorldsFragment worldsFragment;

    public WorldListAdapter(WorldsFragment worldsFragment, List<MinecraftWorldUnit> worldEntityList) {
        super(worldsFragment.getActivity(), worldEntityList);
        this.worldsFragment = worldsFragment;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // Create a new view
            return new WorldListView(getContext(), getItem(position));
        } else {
            // Reuse view
            WorldListView setView = (WorldListView) convertView;
            setView.setData(getItem(position));
            return setView;
        }
    }
}
