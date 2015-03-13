/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.sei.dm.dianping.rawdata;

import java.net.URL;

/**
 *
 * @author gong
 */
public class CrawlerException extends Exception {
    
    private final URL url;
    
    public CrawlerException(URL url, String msg) {
        super(msg);
        this.url = url;
    }
    
    public CrawlerException(URL url, Throwable t) {
        super(t);
        this.url = url;
    }
    
    public CrawlerException(URL url, String msg, Throwable t) {
        super(msg, t);
        this.url = url;
    }

    public URL getURL() {
        return url;
    }
    
}
