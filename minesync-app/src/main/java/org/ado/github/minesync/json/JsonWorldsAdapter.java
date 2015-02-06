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

package org.ado.github.minesync.json;

import org.ado.github.minesync.db.SyncTypeEnum;
import org.ado.github.minesync.db.WorldEntity;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to transform <code>WorldEntity</code> into JSON array and vice versa.
 *
 * @author andoni
 * @since 1.2.0
 */
public class JsonWorldsAdapter {

    public JSONObject getWorldListToJson(List<WorldEntity> worldEntityList) {
        JSONObject jsonObject = new JSONObject();

        JSONArray jsonArray = new JSONArray();
        for (WorldEntity worldEntity : worldEntityList) {
            Map map = new HashMap<String, String>();
            map.put("name", worldEntity.getName());
            map.put("syncType", worldEntity.getSyncType().getSyncType());
            jsonArray.add(new JSONObject(map));
        }
        jsonObject.put("worlds", jsonArray);
        return jsonObject;
    }

    public List<JsonWorld> getWorldList(File jsonFile) {
        ArrayList<JsonWorld> jsonWorlds = new ArrayList<JsonWorld>();
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(FileUtils.readFileToString(jsonFile));
            JSONArray worldsArray = (JSONArray) jsonObject.get("worlds");
            for (Object o : worldsArray) {
                JSONObject world = (JSONObject) o;
                JsonWorld jsonWorld = new JsonWorld();
                jsonWorld.setName((String) world.get("name"));
                jsonWorld.setSyncType(SyncTypeEnum.find(((Long) world.get("syncType")).intValue()));
                jsonWorlds.add(jsonWorld);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return jsonWorlds;
    }
}
