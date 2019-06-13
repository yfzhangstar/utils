package com.akoo.objs;

public class HeadItem {
    public int ix;
    public String type;
    public String filedName;
    public String cellVal;

    HeadItem() {
    }

    @Override
    public String toString() {
        return "HeadItem{" +
                "ix=" + ix +
                ", type='" + type + '\'' +
                ", filedName='" + filedName + '\'' +
                ", cellVal='" + cellVal + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HeadItem headItem = (HeadItem) o;

        if (!type.equals(headItem.type)) return false;
        return filedName.equals(headItem.filedName);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + filedName.hashCode();
        return result;
    }
}