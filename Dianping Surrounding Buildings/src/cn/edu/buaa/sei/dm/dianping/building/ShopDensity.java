/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.sei.dm.dianping.building;

import cn.edu.buaa.sei.dm.dianping.resolution.Location;
import java.util.Map;

/**
 *
 * @author Qiang
 */
public class ShopDensity {

    public static int DEFAULT_SLICE_COUNT = 500;
    // x, y
    private int[][] densityMap;
    // x, y
    private int[][] radiusMap;
    private int sliceCount;
    private double maxLat = Utility.MAX_LAT;
    private double minLat = Utility.MIN_LAT;
    private double maxLng = Utility.MAX_LNG;
    private double minLng = Utility.MIN_LNG;

    public ShopDensity() {
        sliceCount = DEFAULT_SLICE_COUNT;
        densityMap = new int[DEFAULT_SLICE_COUNT][DEFAULT_SLICE_COUNT];
        radiusMap = new int[DEFAULT_SLICE_COUNT][DEFAULT_SLICE_COUNT];
    }

    public ShopDensity(int slice) {
        sliceCount = slice;
        densityMap = new int[sliceCount][sliceCount];
        radiusMap = new int[sliceCount][sliceCount];
    }

    //         max_lat
    // min_lng [ map ] max_lng
    //         min_lat
    public void setBound(double min_lat, double max_lat, double min_lng, double max_lng) {
        maxLat = max_lat;
        minLat = min_lat;
        maxLng = max_lng;
        minLng = min_lng;
    }

    public int getSliceCount() {
        return sliceCount;
    }

    @Deprecated
    public int getLngGridIndex(double lng) {
        return (int) ((lng - minLng) / (maxLng - minLng) * sliceCount);
    }

    @Deprecated
    public int getLatGridIndex(double lat) {
        return (int) ((maxLat - lat) / (maxLat - minLat) * sliceCount);
    }

    public int getRadius(Location loc) {
        int lat_grid = (int) ((maxLat - loc.lat) / (maxLat - minLat) * sliceCount);
        int lng_grid = (int) ((loc.lng - minLng) / (maxLng - minLng) * sliceCount);
        return radiusMap[lng_grid][lat_grid];
    }

    public Location getGridCenterLocation(int i, int j) {
        Location loc = new Location();
        loc.lat = maxLat - (maxLat - minLat) / sliceCount * (j + 0.5);
        loc.lng = minLng + (maxLng - minLng) / sliceCount * (i + 0.5);
        return loc;
    }

    public Location getGridCenterLocation(Location loc) {
        int lat_grid = (int) ((maxLat - loc.lat) / (maxLat - minLat) * sliceCount);
        int lng_grid = (int) ((loc.lng - minLng) / (maxLng - minLng) * sliceCount);
        return getGridCenterLocation(lng_grid, lat_grid);
    }

    public void initialize(Map<Integer, Location> map) {
        for (Location loc : map.values()) {
            if (loc.lat > maxLat || loc.lat < minLat) {
                continue;
            }
            if (loc.lng > maxLng || loc.lng < minLng) {
                continue;
            }
            int lat_grid = (int) ((maxLat - loc.lat) / (maxLat - minLat) * sliceCount);
            int lng_grid = (int) ((loc.lng - minLng) / (maxLng - minLng) * sliceCount);
            densityMap[lng_grid][lat_grid]++;
        }

        for (int i = 0; i < sliceCount; i++) {
            for (int j = 0; j < sliceCount; j++) {
                int rnd1;
                int rnd2;
                if (densityMap[i][j] > 0) {
                    // o  o  o
                    // o  i  o
                    // o  o  o
                    rnd1 = 8;
                    //    o  o  o   
                    // o  x  x  x  o
                    // o  x  i  x  o
                    // o  x  x  x  o
                    //    o  o  o   
                    rnd2 = 12;
                    for (int m = (i - 2 >= 0 ? i - 2 : 0); m <= (i + 2 <= sliceCount - 1 ? i + 2 : sliceCount - 1); m++) {
                        for (int n = (j - 2 >= 0 ? j - 2 : 0); n <= (j + 2 <= sliceCount - 1 ? j + 2 : sliceCount - 1); n++) {
                            // ignore corners
                            if ((m - i == 2 || i - m == 2) && (n - j == 2 || j - n == 2)) {
                                continue;
                            }
                            // ignore center
                            if (m == i && n == j) {
                                continue;
                            }
                            // round 1
                            if ((m - i < 2 || i - m < 2 || n - j < 2 || j - n < 2) && densityMap[m][n] != 0) {
                                rnd1--;
                            } // round 2
                            else if (densityMap[m][n] != 0) {
                                rnd2--;
                            }
                        }
                    }
                    if (rnd1 == 8 && rnd2 > 0) {
                        if (rnd2 >= 12) {
                            radiusMap[i][j] = 4;
                        } else {
                            radiusMap[i][j] = 3;
                        }
                    } else {
                        if (rnd1 >= 5) {
                            radiusMap[i][j] = 2;
                        } else {
                            radiusMap[i][j] = 1;
                        }
                    }
                }
            }
        }
    }

    public int getDensity(int i, int j) {
        return densityMap[i][j];
    }

    public int getRadius(int i, int j) {
        return radiusMap[i][j];
    }

    public int getMax() {
        int max = 0;
        for (int i = 0; i < sliceCount; i++) {
            for (int j = 0; j < sliceCount; j++) {
                if (densityMap[i][j] > max) {
                    max = densityMap[i][j];
                }
            }
        }
        return max;
    }

    public int getNotzeroCount() {
        int k = 0;
        for (int i = 0; i < sliceCount; i++) {
            for (int j = 0; j < sliceCount; j++) {
                if (densityMap[i][j] > 0) {
                    k++;
                }
            }
        }
        return k;
    }

    public int[] getRadiusCount() {
        int[] res = new int[4];
        for (int i = 0; i < sliceCount; i++) {
            for (int j = 0; j < sliceCount; j++) {
                switch (radiusMap[i][j]) {
                    case 4:
                        res[3]++;
                        break;
                    case 3:
                        res[2]++;
                        break;
                    case 2:
                        res[1]++;
                        break;
                    case 1:
                        res[0]++;
                        break;
                }
            }
        }
        return res;
    }
}
