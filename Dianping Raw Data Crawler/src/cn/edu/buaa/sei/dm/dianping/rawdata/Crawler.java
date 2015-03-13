/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.sei.dm.dianping.rawdata;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author gong
 */
public class Crawler {

    private final static Pattern PATTERN_MAIN_AREA = Pattern.compile(
            "<body.+?<div class=\"main page.+?<div class=\"section\">(.+?)" + // Group 1: main area, all the information comes from here
            "<div class=\"aside aside-right\">" +
            //".+?地图上参看该区域(.+?)>" + // Group 2: location (http address)
            "", Pattern.MULTILINE | Pattern.DOTALL);

    private final static Pattern PATTERN_BASIC_INFO = Pattern.compile(
            "(?><h1 class=\"shop-title\"[^>]*?>(.+?)</h1>)" + // Group 1: shop title
            "(?>.+?<div class=\"comment-rst\">.+?class=\"item-rank-rst irr-star([^\"]+?)\")" + // Group 2: main rating
            "(?>.+?<span itemprop=\"count\">(.+?)</span>)" + // Group 3: number of rating
            "(?>.+?<span class=\"Price\">.</span>(.+?)</dd>)" + // Group 4: avg. price
            "(?>.+?口味.+?<em class=\"progress-value\">(.+?)</em>)" +
            "(?>.+?环境.+?<em class=\"progress-value\">(.+?)</em>)" + // Group 5-7: rating details
            "(?>.+?服务.+?<em class=\"progress-value\">(.+?)</em>)" +
            "(?>.+?<span itemprop=\"street-address\">(.+?)</span>)" + // Group 8: address
            "(?>.+?<dt>标签:</dt>.*?<dd>(.+?)</dd>)" + // Group 9: tags area
            "(?>.+?<div class=\"block-title\">商户特色</div>(.+?)" + // Group 10: shop features
                "<div class=\"block-title\">详细信息</div>)" +
            "(?>(.+?)<div class=\"block raw-block)" + // Group 11: shop details
            "", Pattern.MULTILINE | Pattern.DOTALL);

    private final static Pattern PATTERN_DETAILS_AREA_1 = Pattern.compile(
            "(?>餐厅氛围.+?<dd>(.+?)</dd>)", Pattern.MULTILINE | Pattern.DOTALL);

    private final static Pattern PATTERN_DETAILS_AREA_2 = Pattern.compile(
            "(?>餐厅特色.+?<dd>(.+?)</dd>)", Pattern.MULTILINE | Pattern.DOTALL);

    private final static Pattern PATTERN_DETAILS_AREA_3 = Pattern.compile(
            "(?>营业时间.+?<dd (.+?)</dd>)", Pattern.MULTILINE | Pattern.DOTALL);

    private final static Pattern PATTERN_BASIC_TAGS = Pattern.compile(
            "<span>.+?<a href.+?onclick.+?\\);\">(.+?)</a>", // Group 1: basic tag
            Pattern.MULTILINE | Pattern.DOTALL);

    private final static Pattern PATTERN_FEATURES = Pattern.compile(
            "<a href=.+?>(.+?)</a>", // Group 1: feature
            Pattern.MULTILINE | Pattern.DOTALL);

    private final static Pattern PATTERN_DTAILS_TAG = Pattern.compile(
            "rel=\"tag\">(.+?)</a>" + // Group 1: detail tag
            ".+?\"count\">\\((.+?)\\)</em>", // Group 2: count tag
            Pattern.MULTILINE | Pattern.DOTALL);

    private final static Pattern PATTERN_DTAILS_TIME_TAG = Pattern.compile(
            "full-cont\">(.+?)</span>", // Group 1: time tag
            Pattern.MULTILINE | Pattern.DOTALL);

    public ShopRawData crawlShopRawDataByURL(URL url, StringBuilder buffer) throws CrawlerException {
        ShopRawData data = new ShopRawData();
        data.id = Utility.getIDFromDianpingShopPageURL(url);
        try {
            StringBuilder buf = Utility.fetchHTMLFromURL(url, buffer);
            Logger.getLogger(Crawler.class.getName()).log(Level.INFO, "Page downloaded: {0}", url);
            Matcher matcher = PATTERN_MAIN_AREA.matcher(buf);
            if (matcher.find()) {
                Matcher basicMatcher = PATTERN_BASIC_INFO.matcher(matcher.group(1));
                if (basicMatcher.find()) {
                    data.shopName = basicMatcher.group(1);
                    data.rating.overall = Utility.parseInt(basicMatcher.group(2), 0);
                    data.rating.count = Utility.parseInt(basicMatcher.group(3), 0);
                    data.price = Utility.parseInt(basicMatcher.group(4), 0);
                    data.rating.taste = Utility.parseInt(basicMatcher.group(5), 0);
                    data.rating.environment = Utility.parseInt(basicMatcher.group(6), 0);
                    data.rating.service = Utility.parseInt(basicMatcher.group(7), 0);
                    data.address = basicMatcher.group(8);

                    Matcher matcher2 = PATTERN_BASIC_TAGS.matcher(basicMatcher.group(9));
                    while (matcher2.find()) {
                        data.tags.add(matcher2.group(1));
                    }

                    Matcher matcher3 = PATTERN_FEATURES.matcher(basicMatcher.group(10));
                    while (matcher3.find()) {
                        ShopRawData.Feature f = createFeatureByString(matcher3.group(1));
                        if (f != null) data.features.add(f);
                    }

                    Matcher detailMatcher1 = PATTERN_DETAILS_AREA_1.matcher(basicMatcher.group(11));
		    Matcher detailMatcher2 = PATTERN_DETAILS_AREA_2.matcher(basicMatcher.group(11));
		    Matcher detailMatcher3 = PATTERN_DETAILS_AREA_3.matcher(basicMatcher.group(11));
                    if (detailMatcher1.find()) {
                        Matcher envMatcher = PATTERN_DTAILS_TAG.matcher(detailMatcher1.group(1));
                        while (envMatcher.find()) {
                            ShopRawData.Feature f = new ShopRawData.Feature(envMatcher.group(1), Utility.parseInt(envMatcher.group(2), 0));
                            if (f.count > 0) data.feelings.add(f);
                        }
                    }
		    if (detailMatcher2.find()) {
			Matcher featureMatcher = PATTERN_DTAILS_TAG.matcher(detailMatcher2.group(1));
                        while (featureMatcher.find()) {
                            ShopRawData.Feature f = new ShopRawData.Feature(featureMatcher.group(1), Utility.parseInt(featureMatcher.group(2), 0));
                            if (f.count > 0) data.characteristics.add(f);
                        }
		    }
		    if (detailMatcher3.find()) {
			Matcher timeMatcher = PATTERN_DTAILS_TIME_TAG.matcher(detailMatcher3.group(1));
                        if (timeMatcher.find()) {
                            data.openingHours = timeMatcher.group(1);
                        }
		    }
                } else {
                    throw new CrawlerException(url, "doesn't match: PATTERN_BASIC_INFO");
                }
            } else {
                throw new CrawlerException(url, "doesn't match: PATTERN_MAIN_AREA");
            }
        } catch (IOException ex) {
            throw new CrawlerException(url, ex);
        }
        return data;
    }

    private static ShopRawData.Feature createFeatureByString(String str) {
        int index = str.lastIndexOf("(");
        if (index != -1) {
            String count_string = str.substring(index + 1);
            count_string = count_string.substring(0, count_string.length() - 1);
            ShopRawData.Feature f = new ShopRawData.Feature(str.substring(0, index), Utility.parseInt(count_string, 0));
            if (f.count > 0) return f;
            else return null;
        } else {
            return null;
        }
    }

    private final static Pattern PATTERN_CATALOG_SHOP_ID = Pattern.compile(
            "<li class=\"shopname\">.+?<a href=\"/shop/(.+?)\"" + // Group 1: shop id
            "", Pattern.MULTILINE | Pattern.DOTALL);

    public List<Integer> crawlShopIDsOnCatalogPage(URL url, StringBuilder buffer) throws CrawlerException {
	try {
	    ArrayList<Integer> list = new ArrayList<Integer>();
	    StringBuilder buf = Utility.fetchHTMLFromURL(url, buffer);
	    Logger.getLogger(Crawler.class.getName()).log(Level.INFO, "Page downloaded: {0}", url);
	    Matcher matcher = PATTERN_CATALOG_SHOP_ID.matcher(buf);
	    while (matcher.find()) {
		String str = matcher.group(1);
		int index = str.indexOf("?");
		if (index != -1) str = str.substring(0, index);
		try {
		    list.add(Integer.parseInt(str));
		} catch (NumberFormatException ex) {
		    throw new CrawlerException(url, ex);
		}
	    }
	    return list;
	} catch (IOException ex) {
	    throw new CrawlerException(url, ex);
	}
    }

    private final static Pattern PATTERN_CATALOG_LINE = Pattern.compile(
            "http://(.+)\\((.+)\\)" +
            "", Pattern.DOTALL);

    public void crawlShopIDsByCatalogFile(final int start_line_number, final int start_page_number, final int sleep_time) {
	StringBuilder buf = new StringBuilder();
	Database database = Database.getInstance();
	try {
	    File file = new File("raw_data/catalog.txt");
	    FileReader fr = new FileReader(file);
	    BufferedReader reader = new BufferedReader(fr);
	    String line = null;
	    int line_number = 1;
	    while ((line = reader.readLine()) != null) {
		if (line_number >= start_line_number) {
		    Matcher m = PATTERN_CATALOG_LINE.matcher(line);
		    if (m.find()) {
			try {
			    final int page_cnt = Integer.parseInt(m.group(2));
			    for (int i = (line_number == start_line_number ? start_page_number : 1); i <= page_cnt; i++) {
				URL url;
				if (i == 1) url = new URL(String.format("http://%s", m.group(1)));
				else url = new URL(String.format("http://%sp%d", m.group(1), i));

				try {
				    List<Integer> list = crawlShopIDsOnCatalogPage(url, buf);
				    for (int shop_index : list) {
					if (!database.contains(shop_index)) {
					    try {
						ShopRawData data = crawlShopRawDataByURL(new URL("http://www.dianping.com/shop/" + shop_index), null);
						database.put(data);
						try {
						    Thread.sleep(sleep_time);
						} catch (InterruptedException ex) {
						    Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
						}
					    } catch (CrawlerException ex) {
						Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
					    }
					}
				    }
				} catch (CrawlerException ex) {
				    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
				}
				database.saveToDataFile();
				Logger.getLogger(Crawler.class.getName()).log(Level.INFO, String.format("Catalog line %d page %d done.", line_number, i));
			    }
			} catch (NumberFormatException ex) {
			    Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
			}
		    }
		}
		line_number++;
	    }
	    Logger.getLogger(Crawler.class.getName()).log(Level.INFO, "Job done.");
	    reader.close();
	} catch (FileNotFoundException ex) {
	    Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
	} catch (IOException ex) {
	    Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

}
