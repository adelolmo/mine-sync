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

package org.ado.github.minesync.gui.fragment;

import android.support.v4.app.FragmentActivity;
import org.ado.github.minesync.db.MineSyncDbOpenHelper;
import org.ado.github.minesync.db.SyncTypeEnum;
import org.ado.github.minesync.db.WorldEntity;
import org.ado.github.minesync.exception.DropboxAccountException;
import org.ado.github.minesync.exception.MineSyncException;
import org.ado.github.minesync.json.JsonWorld;
import org.ado.github.minesync.json.JsonWorldManager;
import org.ado.github.minesync.minecraft.MinecraftData;
import org.ado.github.minesync.minecraft.MinecraftWorld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Class description here.
 *
 * @author andoni
 * @since 24.11.2014
 */
public class MinecraftWorldUnitFactory {

    private MinecraftData minecraftData;
    private MineSyncDbOpenHelper dbOpenHelper;
    private JsonWorldManager jsonWorldManager;

    public MinecraftWorldUnitFactory(FragmentActivity activity) {
        minecraftData = new MinecraftData();
        dbOpenHelper = MineSyncDbOpenHelper.getInstance(activity);
        jsonWorldManager = new JsonWorldManager(activity);
    }

    public List<MinecraftWorldUnit> getList() throws DropboxAccountException, MineSyncException {
        final List<WorldEntity> worldAll = dbOpenHelper.getWorldAll();
        final List<JsonWorld> jsonWorlds = getJsonWorldsOrEmpty();
        final List<MinecraftWorld> minecraftWorldList = minecraftData.getWorlds();

        final List<MinecraftWorldUnit> minecraftWorldUnitList = getMinecraftWorldUnit(minecraftWorldList, worldAll);
        for (JsonWorld jsonWorld : jsonWorlds) {
            if (!contains(minecraftWorldUnitList, jsonWorld)) {
                minecraftWorldUnitList.add(getMinecraftWorldUnit(jsonWorld));
            } else {
                MinecraftWorldUnit minecraftWorldUnit = getMinecraftWorldUnit(jsonWorld, minecraftWorldUnitList);
                minecraftWorldUnit.setSyncType(jsonWorld.getSyncType());
            }
        }
        Collections.sort(minecraftWorldUnitList, new Comparator<MinecraftWorldUnit>() {
            @Override
            public int compare(MinecraftWorldUnit lhs, MinecraftWorldUnit rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });

        return minecraftWorldUnitList;
    }

    private List<JsonWorld> getJsonWorldsOrEmpty() {
        try {
            return jsonWorldManager.getJsonWorlds();
        } catch (MineSyncException e) {
            return Collections.emptyList();
        }
    }

    private MinecraftWorldUnit getMinecraftWorldUnit(JsonWorld jsonWorld, List<MinecraftWorldUnit> minecraftWorldUnitList) {
        for (MinecraftWorldUnit minecraftWorldUnit : minecraftWorldUnitList) {
            if (minecraftWorldUnit.getName().equals(jsonWorld.getName())) {
                return minecraftWorldUnit;
            }
        }
        return null;
    }

    private MinecraftWorldUnit getMinecraftWorldUnit(JsonWorld jsonWorld) {
        return new MinecraftWorldUnit(jsonWorld);
    }

    private List<MinecraftWorldUnit> getMinecraftWorldUnit(List<MinecraftWorld> minecraftDataWorldList, List<WorldEntity> worldEntityList) {
        final List<MinecraftWorldUnit> minecraftWorldUnits = new ArrayList<MinecraftWorldUnit>();
        for (MinecraftWorld minecraftWorld : minecraftDataWorldList) {
            WorldEntity worldEntity = getFilteredWorldEntity(worldEntityList, minecraftWorld.getName());
            minecraftWorldUnits.add(new MinecraftWorldUnit(minecraftWorld, worldEntity != null ? worldEntity.getSyncType() : SyncTypeEnum.AUTO));
        }
        return minecraftWorldUnits;
    }


    private WorldEntity getFilteredWorldEntity(List<WorldEntity> worldEntityList, String name) {
        for (WorldEntity worldEntity : worldEntityList) {
            if (name.equals(worldEntity.getName())) {
                return worldEntity;
            }
        }
        return null;
    }

    private boolean contains(List<MinecraftWorldUnit> minecraftWorldUnitList, JsonWorld jsonWorld) {
        for (MinecraftWorldUnit minecraftWorldUnit : minecraftWorldUnitList) {
            if (minecraftWorldUnit.getName().equals(jsonWorld.getName())) {
                return true;
            }
        }
        return false;
    }
}
