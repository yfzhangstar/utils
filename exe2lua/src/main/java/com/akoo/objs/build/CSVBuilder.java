package com.akoo.objs.build;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.akoo.objs.HeadItem;
import com.akoo.objs.LineObj;
import com.akoo.objs.TypedHeadInfo;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * aly @ 16-11-12.
 */
public class CSVBuilder implements ExportBuilder {
    private Pattern p = Pattern.compile("\"");

    @Override
    public void buildHeadLine(StringBuilder builder, String saveFileName, TypedHeadInfo info) {
        for (HeadItem item : info) {
            builder.append('\"').append(item.cellVal).append('\"').append(',');
        }
        builder.append("\r\n");
    }

    @Override
    public void buildLine(StringBuilder builder, TypedHeadInfo headInfo, String id, LineObj line, boolean buildOne, Map<HeadItem, String> replaceVal) {
        for (HeadItem item : headInfo) {
//                String funType = head.getKey();
            String cellVal = line.getValue(item);
            Matcher matcher = p.matcher(cellVal);

            if (matcher.find()) {
                cellVal = matcher.replaceAll("\"\"");
                builder.append("\"").append(cellVal).append("\",");
            } else if (cellVal.contains(",")) {
                builder.append("\"").append(cellVal).append("\",");
            } else {
                builder.append(cellVal).append(",");
            }
        }
        builder.append("\r\n");
    }

    @Override
    public String getSaveFileName(Sheet sheet, String fileName) {
        return fileName;
    }

    @Override
    public void buildEnd(StringBuilder builder, String fileName, TypedHeadInfo headInfo) {

    }

    @Override
    public HeadItem getIDHeadItem(TypedHeadInfo headInfo) {
        return null;
    }

}

