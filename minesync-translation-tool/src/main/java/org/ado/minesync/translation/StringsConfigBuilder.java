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

package org.ado.minesync.translation;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.commons.lang.StringEscapeUtils.escapeJavaScript;

/**
 * Class description here.
 *
 * @author andoni
 * @since 07.10.2014
 */
public class StringsConfigBuilder {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static final String HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<!-- DO NOT EDIT MANUALLY. Content will get override by the Translation document. Exported on %s -->\n" +
            "<resources>";
    private static final String FOOTER = "\n</resources>";
    private static final String STRING = "<string name=\"%s\">%s</string>";
    private static final String GROUP = "<!--%s-->";
    private static final String TAB = "    ";

    private StringBuilder stringBuilder;
    private String path;

    public StringsConfigBuilder(String path) {
        stringBuilder = new StringBuilder();
        this.path = "/" + path;

        stringBuilder.append(String.format(HEADER, DATE_FORMAT.format(new Date())));
    }

    public void addGroup(String name) {
        stringBuilder.append("\n\n").append(TAB)
                .append(String.format(GROUP, name));
    }

    public void addCode(String name, String value, boolean skipEscape) {
        stringBuilder.append("\n").append(TAB)
                .append(String.format(STRING, name, skipEscape ? value : escapeJava(value)));
    }

    public void build(File exportDirectory) throws IOException {
        stringBuilder.append(FOOTER);
        FileUtils.write(new File(exportDirectory, path), stringBuilder.toString());
    }

    private String escapeJava(String value) {
        return escapeJavaScript(value.replace("\\n", "EOL")).replace("EOL", "\\n");
    }
}