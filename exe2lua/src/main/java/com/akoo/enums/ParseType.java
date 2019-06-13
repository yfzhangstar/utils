package com.akoo.enums;

/**
 * @author Aly on  2016-07-19.
 */
public enum ParseType {
    ALL("1") {
        @Override
        public boolean isParseFile(String[] fileNames, String fileName) {
            return true;
        }
    },
    EXCLUDES("2") {
        @Override
        public boolean isParseFile(String[] fileNames, String fileName) {
            if (null != fileNames) {
                for (String exclude : fileNames) {
                    if (fileName.startsWith(exclude)) {
                        return false;
                    }
                }
            }
            return true;
        }
    },
    INCLUDES("3") {
        @Override
        public boolean isParseFile(String[] fileNames, String fileName) {
            return !EXCLUDES.isParseFile(fileNames, fileName);
        }
    },;
    private String name;

    ParseType(String name) {
        this.name = name;
    }

    public static ParseType getType(String name) {
        for (ParseType parseType : ParseType.values()) {
            if (parseType.name.equals(name)) {
                return parseType;
            }
        }
        return ALL;
    }


    public abstract boolean isParseFile(String[] fileNames, String fileName);
}
