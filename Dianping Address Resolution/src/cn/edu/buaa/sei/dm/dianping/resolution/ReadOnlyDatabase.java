/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.sei.dm.dianping.resolution;

import cn.edu.buaa.sei.dm.dianping.rawdata.ShopRawData;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gong
 */
public final class ReadOnlyDatabase implements Iterable<ShopRawData> {

    private static final int HASH_SIZE = 128;
    private static final ReadOnlyDatabase instance = new ReadOnlyDatabase();

    private final Map<Integer, Map<Integer, ShopRawData>> loadedMaps;

    public static ReadOnlyDatabase getInstance() {
	return instance;
    }

    private ReadOnlyDatabase() {
        loadedMaps = new HashMap<Integer, Map<Integer, ShopRawData>>();
    }

    private static int getStoreFileIndex(int id) {
        return id % HASH_SIZE;
    }

    private Map<Integer, ShopRawData> getMapByIndex(int index) {
        if (loadedMaps.containsKey(index)) {
            return loadedMaps.get(index);
        } else {
            FileInputStream fis;
            ObjectInputStream ois;
            try {
                File file = new File(String.format("../Dianping Raw Data Crawler/raw_data/%d.dat", index));
                if (file.exists()) {
                    fis = new FileInputStream(file);
                    ois = new ObjectInputStream(fis);
                    Map<Integer, ShopRawData> map = (Map<Integer, ShopRawData>) ois.readObject();
                    ois.close();
                    loadedMaps.put(index, map);
                    return map;
                } else {
                    Map<Integer, ShopRawData> map = new HashMap<Integer, ShopRawData>();
                    loadedMaps.put(index, map);
                    return map;
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(cn.edu.buaa.sei.dm.dianping.rawdata.Database.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException("bad serialization");
            } catch (IOException ex) {
                Logger.getLogger(cn.edu.buaa.sei.dm.dianping.rawdata.Database.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException("bad store file");
            }
        }
    }

    private Map<Integer, ShopRawData> getMap(int id) {
        int index = getStoreFileIndex(id);
        return getMapByIndex(index);
    }

    public boolean contains(int id) {
        Map<Integer, ShopRawData> map = getMap(id);
        return map.containsKey(id);
    }

    public ShopRawData get(int id) {
        Map<Integer, ShopRawData> map = getMap(id);
        return map.get(id);
    }

    @Override
    public Iterator<ShopRawData> iterator() {
        return new Iterator<ShopRawData>() {
            int index = -1;
            Iterator<ShopRawData> it;
            @Override
            public boolean hasNext() {
                if (it != null && it.hasNext()) return true;
                else {
                    for (index++; index < HASH_SIZE; index++) {
                        Map<Integer, ShopRawData> map = getMapByIndex(index);
                        it = map.values().iterator();
                        boolean hasnext = it.hasNext();
                        if (hasnext) return true;
                        else continue;
                    }
                    return false;
                }
            }

            @Override
            public ShopRawData next() {
                return it.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    public static interface Filter {
        boolean accept(ShopRawData data);
    }
    
    public Iterator<ShopRawData> iterator(final Filter filter) {
        return new Iterator<ShopRawData>() {
            Iterator<ShopRawData> it = iterator();
            ShopRawData next = null;
            @Override
            public boolean hasNext() {
                while (it.hasNext()) {
                    next = it.next();
                    if (filter == null || filter.accept(next)) return true;
                }
                next = null;
                return false;
            }

            @Override
            public ShopRawData next() {
                return next;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }
    
}
