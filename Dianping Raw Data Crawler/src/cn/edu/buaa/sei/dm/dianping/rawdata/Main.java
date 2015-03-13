/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.sei.dm.dianping.rawdata;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gong
 */
public class Main {

    public static void main(String[] args) {

        // crawler code:
//        try {
//            Crawler crawler = new Crawler();
//            URL url = new URL("http://www.dianping.com/shop/3185098");
//            ShopRawData data = crawler.crawlShopRawDataByURL(url, null);
//            System.out.println(data);
//
//            Database database = Database.getInstance();
////            database.put(data);
////            database.saveToDataFile();
//        } catch (IOException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (CrawlerException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }

        // read database code:
        Database database = Database.getInstance();
	int cnt = 0;
        for (ShopRawData data : database) {
//            System.out.println(data);
	    cnt++;
        }
	System.out.println(cnt + " records.");

	// crawl data
//	Crawler crawler = new Crawler();
//	crawler.crawlShopIDsByCatalogFile(0, 0, 1000);
    }

}
