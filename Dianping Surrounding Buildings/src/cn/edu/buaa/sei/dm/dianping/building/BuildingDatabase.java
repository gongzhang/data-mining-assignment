/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.sei.dm.dianping.building;

import cn.edu.buaa.sei.dm.dianping.rawdata.Database;
import cn.edu.buaa.sei.dm.dianping.resolution.Location;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Qiang
 */
public class BuildingDatabase {

    private Map<String, BuildingRawData> buildMap = new HashMap<String, BuildingRawData>();
    private Object[][] gridMap;
    private int sliceCount = 200;

    private static BuildingDatabase instance;
    
    public static BuildingDatabase getInstance() {
        if (instance == null) {
            instance = new BuildingDatabase();
            instance.load();
        }
        return instance;
    }
    
    private BuildingDatabase() {
        gridMap = new Object[sliceCount][sliceCount];
    }

    public Map<String, BuildingRawData> getMap() {
        return buildMap;
    }

    public void add(BuildingRawData build) {
        if (build.id == null) {
            System.out.println("No ID : " + build.name);
        } else if (contains(build.id)) {
            System.out.println("Recorded : " + build.id + "," + build.name);
        } else {
            buildMap.put(build.id, build);
            putBuildInMap(build);
        }
    }

    private void putBuildInMap(BuildingRawData build) {
        Location loc = build.location;
        int lat_grid = getLatGridIndex(loc.lat);
        int lng_grid = getLngGridIndex(loc.lng);
        ArrayList<BuildingRawData> list;
        if (gridMap[lng_grid][lat_grid] == null) {
            list = new ArrayList<BuildingRawData>();
            gridMap[lng_grid][lat_grid] = list;
        } else {
            list = (ArrayList<BuildingRawData>) gridMap[lng_grid][lat_grid];
        }
        list.add(build);
    }

    public BuildingRawData get(String id) {
        return buildMap.get(id);
    }

    public boolean contains(String id) {
        return buildMap.containsKey(id);
    }

    public int size() {
        return buildMap.size();
    }

    private int getLngGridIndex(double lng) {
        return (int) ((lng - Utility.MIN_LNG) / (Utility.MAX_LNG - Utility.MIN_LNG) * sliceCount);
    }

    private int getLatGridIndex(double lat) {
        return (int) ((Utility.MAX_LAT - lat) / (Utility.MAX_LAT - Utility.MIN_LAT) * sliceCount);
    }

    private static double getDistance(Location loc1, Location loc2) {
        double dlat = loc2.lat - loc1.lat;
        double dlng = loc2.lng - loc1.lng;
        return Math.sqrt(dlng * dlng + dlat * dlat);
    }

    public List<BuildingRawData> getBuildings(Location center, double radius) {
        ArrayList<BuildingRawData> res = new ArrayList<BuildingRawData>();

        int startI = getLngGridIndex(center.lng - Utility.distanceToLng(radius));
        int endI = getLngGridIndex(center.lng + Utility.distanceToLng(radius));
        int startJ = getLatGridIndex(center.lat + Utility.distanceToLat(radius));
        int endJ = getLatGridIndex(center.lat - Utility.distanceToLat(radius));
        if (startI >= sliceCount || endI < 0 || startJ >= sliceCount || endJ < 0) {
            return res;
        }
        if (startI < 0) {
            startI = 0;
        }
        if (endI >= sliceCount) {
            endI = sliceCount - 1;
        }
        if (startJ < 0) {
            startJ = 0;
        }
        if (endJ >= sliceCount) {
            endJ = sliceCount - 1;
        }
        List<BuildingRawData> list;
        for (int i = startI; i <= endI; i++) {
            for (int j = startJ; j <= endJ; j++) {
                if (gridMap[i][j] == null) {
                    continue;
                }
                list = (List<BuildingRawData>) gridMap[i][j];
                for (BuildingRawData build : list) {
                    if (getDistance(center, build.location) <= radius) {
                        res.add(build);
                    }
                }
            }
        }
        return res;
    }

    private void load() {
        File file = new File("../Dianping Surrounding Buildings/raw_data/building.dat");
        if (!file.exists()) {
            return;
        }
        if (file.length() == 0) {
            return;
        }
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                JSONObject jo = new JSONObject(line);
                BuildingRawData build = BuildingRawData.restore(jo);
                //System.out.println(build);
                add(build);
            }
        } catch (JSONException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
