/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.sei.dm.dianping.rawdata;

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
public final class Database implements Iterable<ShopRawData> {

    private static final int HASH_SIZE = 128;
    private static final Database instance = new Database();

    private final Map<Integer, Map<Integer, ShopRawData>> loadedMaps;

    public static Database getInstance() {
	return instance;
    }

    private Database() {
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
                File file = new File(String.format("raw_data/%d.dat", index));
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
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException("bad serialization");
            } catch (IOException ex) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException("bad store file");
            }
        }
    }

    private Map<Integer, ShopRawData> getMap(int id) {
        int index = getStoreFileIndex(id);
        return getMapByIndex(index);
    }

    public synchronized boolean contains(int id) {
        Map<Integer, ShopRawData> map = getMap(id);
        return map.containsKey(id);
    }

    public synchronized ShopRawData get(int id) {
        Map<Integer, ShopRawData> map = getMap(id);
        return map.get(id);
    }

    public synchronized void put(ShopRawData data) {
        Map<Integer, ShopRawData> map = getMap(data.id);
        map.put(data.id, data);
    }

    public synchronized void remove(int id) {
        Map<Integer, ShopRawData> map = getMap(id);
        map.remove(id);
    }

    public synchronized void saveToDataFile() {
        for (Map.Entry<Integer, Map<Integer, ShopRawData>> entry : loadedMaps.entrySet()) {
            File file = new File(String.format("raw_data/%d.dat", entry.getKey()));
            ObjectOutputStream oos;
            try {
                FileOutputStream fos = new FileOutputStream(file);
                oos = new ObjectOutputStream(fos);
                oos.writeObject(entry.getValue());
                oos.close();
            } catch (IOException ex) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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

}
