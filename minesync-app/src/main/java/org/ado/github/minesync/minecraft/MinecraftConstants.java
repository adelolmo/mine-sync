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

package org.ado.github.minesync.minecraft;

import android.os.Environment;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: andoni
 * Date: 21/09/13
 * Time: 12:43
 * To change this template use File | Settings | File Templates.
 */
public class MinecraftConstants {

    public static final String MINECRAFT_DATA_HOME_DIR = "games/com.mojang";
    public static final String MINECRAFT_CONFIG_DIR = "minecraftpe";
    public static final String MINECRAFT_WORLDS_DIR = "minecraftWorlds";

    public static final File MINECRAFT_HOME = new File(Environment.getExternalStorageDirectory(), MINECRAFT_DATA_HOME_DIR);
    public static final File MINECRAFT_CONFIG = new File(MINECRAFT_DATA_HOME_DIR, MINECRAFT_CONFIG_DIR);
    public static final File MINECRAFT_WORLDS = new File(MINECRAFT_HOME, MINECRAFT_WORLDS_DIR);

}
