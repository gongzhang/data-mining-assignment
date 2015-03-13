/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.sei.dm.dianping.rawdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

/**
 *
 * @author gong
 */
public final class Utility {
    
    private Utility() {
    }
    
    /**
     * Fetch HTTP page as text from specified URL.
     * @param url
     * @param buffer
     * @return
     * @throws IOException 
     */
    public static StringBuilder fetchHTMLFromURL(URL url, StringBuilder buffer) throws IOException {
        URLConnection conn = url.openConnection();
        conn.setDoInput(true);
        conn.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 4.5; Window NT 5.1)");
        conn.connect();
        InputStreamReader isr = new InputStreamReader(conn.getInputStream(), Charset.forName("utf-8"));
        BufferedReader reader = new BufferedReader(isr);
        String line;
        if (buffer == null) buffer = new StringBuilder();
        else buffer.delete(0, buffer.length());
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
            buffer.append('\n');
        }
        reader.close();
        return buffer;
    }
    
    /**
     * Fetch HTTP page as text from specified URL.
     * @param url
     * @return
     * @throws IOException 
     */
    public static StringBuilder fetchHTMLFromURL(URL url) throws IOException {
        return fetchHTMLFromURL(url, null);
    }
    
    public static int getIDFromDianpingShopPageURL(URL url) {
        String s = url.toExternalForm();
        s = s.substring(s.lastIndexOf("/") + 1);
        return parseInt(s, -1);
    }
    
    public static int parseInt(String string, int def) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException ex) {
            return def;
        }
    }
    
}
