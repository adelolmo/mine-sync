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

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Class description here.
 *
 * @author andoni
 * @since 06.10.2014
 */
public class ExportFile {

    private static final int CODE_COL = 0;

    public void export(InputStream translations, File exportDirectory) throws IOException {

        Map<String, StringsConfigBuilder> map = null;
        XSSFWorkbook workbook = new XSSFWorkbook(translations);
        XSSFSheet sheet = workbook.getSheet("strings.xml");
        boolean init = false;

        for (Row cells : sheet) {

            if (!init) {
                map = createConfigurationBuilders(cells);
                init = true;

            } else {
                String code = cells.getCell(CODE_COL).getStringCellValue();

                if (StringUtils.isNotBlank(code)) {
                    int i = 1;
                    for (String lang : map.keySet()) {
                        Cell cell = cells.getCell(i++);
                        if (containsCode(cells)) {

                            if (isNotEmpty(cell)) {
                                addCode(map, sheet, code, cell);
                            }

                        } else {
                            map.get(lang).addGroup(code);
                        }
                    }
                }
            }
        }
        buildStringFiles(exportDirectory, map);
    }

    private Map<String, StringsConfigBuilder> createConfigurationBuilders(Row cells) {
        Map<String, StringsConfigBuilder> map = new HashMap<String, StringsConfigBuilder>();
        for (Cell cell : cells) {
            if (cell != null
                    && !"Code".equals(cell.getStringCellValue())
                    && StringUtils.isNotBlank(cell.getStringCellValue())) {
                map.put(cell.getStringCellValue(), new StringsConfigBuilder(cell.getStringCellValue() + "/strings.xml"));
            }
        }
        return map;
    }

    private boolean containsCode(Row cells) {
        return StringUtils.isNotBlank(cells.getCell(1).getStringCellValue());
    }

    private String getCodeEntryName(XSSFSheet sheet, Cell cell) {
        return sheet.getRow(CODE_COL).getCell(cell.getColumnIndex()).getStringCellValue();
    }

    private boolean isNotEmpty(Cell cell) {
        return cell != null
                && StringUtils.isNotBlank(cell.getStringCellValue());
    }

    private void addCode(Map<String, StringsConfigBuilder> map, XSSFSheet sheet, String code, Cell cell) {
        String header = getCodeEntryName(sheet, cell);
        StringsConfigBuilder stringsConfigBuilder = map.get(header);
        stringsConfigBuilder.addCode(code, cell.getStringCellValue(), isCellFormatted(cell));
    }

    private boolean isCellFormatted(Cell cell) {
        return cell.getCellStyle().getFillBackgroundColorColor() != null;
    }

    private void buildStringFiles(File exportDirectory, Map<String, StringsConfigBuilder> map) throws IOException {
        for (StringsConfigBuilder stringsConfigBuilder : map.values()) {
            stringsConfigBuilder.build(exportDirectory);
        }
    }
}
