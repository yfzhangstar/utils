package com.digisky.ps.exe2lua;

import java.net.URLEncoder;
import java.util.regex.Pattern;

/**
 * Unit test for simple App.
 */
public class AppTest {
    public static void main(String[] args) {
        String s = "{\"21\",\"22\",\"23\",\"24\",\"25\",\"26\",\"27\",\"28\"}";
        Pattern p = Pattern.compile("\"");
        System.out.println(p.matcher(s).matches());
        System.out.println(p.matcher(s).replaceAll("\"\""));
        System.out.println(s);
        System.out.println(URLEncoder.encode("http://192.168.10.65/Game.of.Thrones.S07E01.Dragonstone.REPACK.1080p.AMZN.WEBRip.DDP5.1.x264-GoT[rartv]/Game.of.Thrones.S07E01.Dragonstone.REPACK.1080p.AMZN.WEBRip.DDP5.1.x264-GoT.mkv"));
    }
}
