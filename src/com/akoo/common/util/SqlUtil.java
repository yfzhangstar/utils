package com.akoo.common.util;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;

/**
 * @author Aly on  2016-10-25.
 */
public class SqlUtil {
    public static String getJavaType(ResultSetMetaData rsmd, String columnName) throws SQLException {
        int count = rsmd.getColumnCount();
        for (int i = 1; i <= count; i++) {
            String key = rsmd.getColumnName(i);
            if (!key.equals(columnName))
                continue;
            return getJavaType(rsmd, i);
        }
        return "";
    }

    public static String getJavaType(ResultSetMetaData rsmd, int i) throws SQLException {
        int count = rsmd.getColumnCount();
        if (i > count)
            return "";
        int columnType = rsmd.getColumnType(i);
        switch (columnType) {
            case java.sql.Types.TIMESTAMP:
                return java.util.Date.class.getName();
            case java.sql.Types.TIME:
                return Time.class.getName();
            case java.sql.Types.VARBINARY:
            case java.sql.Types.SQLXML:
            case java.sql.Types.ROWID:
            case java.sql.Types.OTHER:
            case java.sql.Types.NCLOB:
            case java.sql.Types.LONGVARBINARY:
            case java.sql.Types.JAVA_OBJECT:
            case java.sql.Types.NUMERIC:
            case java.sql.Types.DECIMAL:
            case java.sql.Types.DATE:
            case java.sql.Types.CLOB:
            case java.sql.Types.BLOB:
            case java.sql.Types.BINARY:
            case java.sql.Types.ARRAY:
                return byte[].class.getSimpleName();
            case java.sql.Types.TINYINT:
                return Byte.class.getSimpleName();
            case java.sql.Types.SMALLINT:
                return Short.class.getSimpleName();
            case java.sql.Types.REAL:
            case java.sql.Types.INTEGER:
            case java.sql.Types.FLOAT:
            case java.sql.Types.DOUBLE:
                return Integer.class.getSimpleName();
            case java.sql.Types.BIGINT:
                return Long.class.getSimpleName();
            case java.sql.Types.BOOLEAN:
            case java.sql.Types.BIT:
                return Boolean.class.getSimpleName();
            case java.sql.Types.VARCHAR:
            case java.sql.Types.NVARCHAR:
            case java.sql.Types.NCHAR:
            case java.sql.Types.LONGNVARCHAR:
            case java.sql.Types.LONGVARCHAR:
                return String.class.getSimpleName();
            case java.sql.Types.CHAR: {
                return String.class.getSimpleName();
//			return columnName.equals("id")?UUID.class.getSimpleName():String.class.getSimpleName();
            }
            default:
                break;
        }
        return "";
    }

    public static String getJavaTypeDefaultValue(ResultSetMetaData rsmd, int i) throws SQLException {
        int count = rsmd.getColumnCount();
        if (i > count)
            return "";
        int columnType = rsmd.getColumnType(i);
        switch (columnType) {
            case java.sql.Types.VARBINARY:
            case java.sql.Types.TIMESTAMP:
            case java.sql.Types.TIME:
            case java.sql.Types.SQLXML:
            case java.sql.Types.ROWID:
            case java.sql.Types.OTHER:
            case java.sql.Types.NCLOB:
            case java.sql.Types.LONGVARBINARY:
            case java.sql.Types.JAVA_OBJECT:
            case java.sql.Types.NUMERIC:
            case java.sql.Types.DECIMAL:
            case java.sql.Types.DATE:
            case java.sql.Types.CLOB:
            case java.sql.Types.BLOB:
            case java.sql.Types.BINARY:
            case java.sql.Types.ARRAY:
                return "null";
            case java.sql.Types.TINYINT:
            case java.sql.Types.SMALLINT:
            case java.sql.Types.REAL:
            case java.sql.Types.INTEGER:
            case java.sql.Types.FLOAT:
            case java.sql.Types.DOUBLE:
                return "0";
            case java.sql.Types.BIGINT:
                return "0L";
            case java.sql.Types.BOOLEAN:
            case java.sql.Types.BIT:
                return "false";
            case java.sql.Types.VARCHAR:
            case java.sql.Types.NVARCHAR:
            case java.sql.Types.NCHAR:
            case java.sql.Types.LONGNVARCHAR:
            case java.sql.Types.LONGVARCHAR:
                return "\"\"";
            case java.sql.Types.CHAR:
                return "\"\"";
//			return columnName.equals("id")?null: "\"\"";
            default:
                break;
        }
        return "";
    }
}
