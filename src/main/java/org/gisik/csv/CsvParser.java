package org.gisik.csv;

import org.gisik.EmptyFileException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CsvParser {
    static class NameInfo {
        String text;
        boolean realName;
        NameInfo(String text, int headerSize) {
            if(text.isEmpty()) {
                realName = false;
                this.text = String.format("column %d", headerSize+1);
            } else {
                this.text = text;
                realName = true;
            }
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private final List<String> lines;
    private List<NameInfo> header;
    private char separator;
    CsvParser(File file, char separator) throws IOException {
        lines = Files.readAllLines(file.toPath());
        if(lines.isEmpty()) {
            throw new EmptyFileException("Empty file");
        }
        setSeparator(separator); //TODO: check if separator length is 1
        System.out.println(header);
    }

    public String[] getColumnNames() {
        String[] ret = new String[header.size()];
        for(int i = 0; i < header.size(); i++) {
            ret[i] = header.get(i).text;
        }
        return ret;
    }

    public char getSeparator() {
        return separator;
    }

    public void setSeparator(char separator) {
        this.separator = separator;
        readHeader();
    }

    private void readHeader() {
        header = new ArrayList<>();
        int columnsNum = countColumns();
        String row = lines.get(0);
        boolean opened = false;
        String name = "";
        for(int i = 0; i < row.length(); i++) {
            char cur = row.charAt(i);
            if (cur != '"') {
                if(cur == separator && !opened) {
                    header.add(new NameInfo(name, header.size()));
                    name = "";
                } else {
                    name += cur;
                }
            } else {
                opened ^= true;
            }
        }
        header.add(new NameInfo(name, header.size()));

        int headerSize = header.size();
        if(headerSize < columnsNum) {
            for(int i=0; i<columnsNum-headerSize;i++) {
                header.add(new NameInfo("", header.size()));
            }
        }
    }

    private int countColumns() {
        int max = 1;
        boolean opened = false;
        for(String line : lines) {
            int columnCount = 1;
            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) == '"') {
                    opened ^= true;
                }
                if (line.charAt(i) == separator && !opened) {
                    columnCount++;
                }
            }
            max = Math.max(max, columnCount);
        }
        return max;
    }

    private int nameToColumnIndex(String colName) {
        int idx = 0;
        for(NameInfo h : header) {
            if (h.text.equals(colName)) {
                return idx;
            }
            idx++;
        }
        return -1; // shoudnt happen;
    }

    public List<Double> parseColumnByName(String colName, boolean firstRow) {
        List<Double> ret = new ArrayList<>();
        int idx = nameToColumnIndex(colName);
        boolean opened = false;
        boolean isFirst = true;
        for(String line : lines) {
            if(isFirst && !firstRow) {
                isFirst = false;
                continue;
            }
            String name = "";
            int curIdx = 0;
            for (int i = 0; i < line.length(); i++) {
                char cur = line.charAt(i);
                if (cur != '"') {
                    if (cur != separator) {
                        if(curIdx == idx) {
                            name +=  line.charAt(i);
                        }
                    } else if(!opened) {
                        if(curIdx == idx) {
                            try {
                                ret.add(Double.parseDouble(name.trim()));
                            } catch(NumberFormatException e) {
                                ret.add(null);
                            }
                            break;
                        }
                        curIdx++;
                    }
                } else {
                    opened ^= true;
                }
            }
            if(name.isEmpty()) {
                ret.add(null);
            }
        }
        return ret;
    }

    public String getFileContent() {
        String ret = "";
        for (String line : lines) {
            ret += line + "\n";
        }
        return ret;
    }

    static public char comboTextToChar(String txt) {
        if (Objects.equals(txt, "TAB")) {
            return '\t';
        } else if (Objects.equals(txt, "SPACE")) {
            return ' ';
        }
        return txt.charAt(0);
    }
}
