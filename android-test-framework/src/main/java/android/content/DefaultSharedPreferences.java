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

package android.content;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Class description here.
 *
 * @author andoni
 * @since 15.03.2014
 */
public class DefaultSharedPreferences implements SharedPreferences {

    private Map<String, Object> map;

    public DefaultSharedPreferences() {
        this.map = new HashMap<String, Object>();
    }

    public void put(String key, Object value) {
        this.map.put(key, value);
    }

    @Override
    public Map<String, ?> getAll() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getString(String key, String defValue) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getInt(String key, int defValue) {
        System.out.println("getInt key[" + key + "] defValue[" + defValue + "].");
        return Integer.valueOf(getValue(key));
    }

    @Override
    public long getLong(String key, long defValue) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public float getFloat(String key, float defValue) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean contains(String key) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Editor edit() {
        return new DefaultEditor();
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private String getValue(String key) {
        try {
            return String.valueOf(this.map.get(key));
        } catch (Exception e) {
            throw new RuntimeException("Entry not found for key \"" + key + "\".");
        }
    }

    class DefaultEditor implements Editor {

        @Override
        public Editor putString(String key, String value) {
            map.put(key, value);
            return this;
        }

        @Override
        public Editor putStringSet(String key, Set<String> values) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Editor putInt(String key, int value) {
            map.put(key, value);
            return this;
        }

        @Override
        public Editor putLong(String key, long value) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Editor putFloat(String key, float value) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Editor putBoolean(String key, boolean value) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Editor remove(String key) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Editor clear() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean commit() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void apply() {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
