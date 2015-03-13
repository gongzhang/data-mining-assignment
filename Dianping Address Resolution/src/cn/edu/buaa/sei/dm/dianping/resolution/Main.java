/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.sei.dm.dianping.resolution;

import cn.edu.buaa.sei.dm.dianping.rawdata.Database;
import cn.edu.buaa.sei.dm.dianping.rawdata.ShopRawData;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.print.DocFlavor;

/**
 *
 * @author gong
 */
public class Main {

    public static void main(String[] args) {
        Map<Integer, Location> locMap = loadLocationMap();
        Logger.getLogger(Main.class.getName()).log(Level.INFO, String.format("%d records.", locMap.size()));

        ReadOnlyDatabase database = ReadOnlyDatabase.getInstance();
        Iterator<ShopRawData> it = database.iterator();
        int cnt = 0;
        while (it.hasNext()) {
            ShopRawData data = it.next();
            if (!locMap.containsKey(data.id)) {
                // resolve addr
                int index = data.address.lastIndexOf("(");
                String addr;
                if (index != -1) {
                    addr = "中国北京市" + data.address.substring(0, index);
                } else {
                    addr = "中国北京市" + data.address;
                }
                try {
                    Location loc = resolveAddress(addr);
                    if (loc != null) {
                        locMap.put(data.id, loc);
                        Logger.getLogger(Main.class.getName()).log(Level.INFO, String.format("Address resolved - data[%d]: %s", data.id, data.address));
                        cnt++;
                        if (cnt == 16) {
                            Logger.getLogger(Main.class.getName()).log(Level.INFO, String.format("%d records.", locMap.size()));
                            saveLocationMap(locMap);
                            cnt = 0;
                        }
                    } else {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, String.format("cannot resolve address on data[%d]: %s", data.id, data.address));
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, String.format("cannot resolve address on data[%d]: %s", data.id, data.address), ex);
                }
            }
        }
        saveLocationMap(locMap);
    }

    private static Map<Integer, Location> loadLocationMap() {
        try {
            File file = new File("location.dat");
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Map<Integer, Location> rst = (Map<Integer, Location>) ois.readObject();
            ois.close();
            return rst;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static void saveLocationMap(Map<Integer, Location> map) {
        try {
            File file = new File("location.dat");
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(map);
            oos.close();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static Location resolveAddress(String addr) throws IOException {
        String encoded = URLEncoder.encode(addr, "utf-8");
        URL url = new URL("http://maps.googleapis.com/maps/api/geocode/xml?address=" + addr + "&sensor=false");
        URLConnection conn = url.openConnection();
        conn.connect();
        InputStreamReader isr = new InputStreamReader(conn.getInputStream());
        BufferedReader reader = new BufferedReader(isr);
        String line;
        StringBuilder buf = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            buf.append(line);
        }
        Pattern pattern = Pattern.compile("<lat>(.+?)</lat>\\s*<lng>(.+?)</lng>", Pattern.MULTILINE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(buf);
        if (matcher.find()) {
            Location loc = new Location();
            loc.lat = Double.parseDouble(matcher.group(1));
            loc.lng = Double.parseDouble(matcher.group(2));
            return loc;
        } else {
            return null;
        }
    }

}
