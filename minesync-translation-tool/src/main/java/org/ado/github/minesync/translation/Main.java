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

package org.ado.github.minesync.translation;

import org.ado.github.minesync.translation.drive.DriveClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class description here.
 *
 * @author andoni
 * @since 06.10.2014
 */
public class Main {

    public static void main(String[] args) {
        DriveClient driveClient = new DriveClient();
        ExportFile exportFile = new ExportFile();

        if (args.length < 2) {
            System.out.println("Missing parameters!");
            System.exit(1);
        }

        final File exportDirectory = new File(args[0]);
        if (!exportDirectory.exists()) {
            System.out.println("Export directory \"" + exportDirectory.getAbsolutePath() + "\" does not exists");
        }
        String version = args[1];
        if (version.endsWith("-SNAPSHOT")) {
            version = version.replace("-SNAPSHOT", "");
        }

        try {
            final InputStream translationFile = driveClient.getTranslationFile(version);
            exportFile.export(translationFile, exportDirectory);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
        System.out.println("Translation export finished successfully.");
//        System.exit(0);
    }
}
