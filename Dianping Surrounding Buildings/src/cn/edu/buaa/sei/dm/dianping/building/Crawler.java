/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.sei.dm.dianping.building;

import cn.edu.buaa.sei.dm.dianping.preprocess.Utility;
import cn.edu.buaa.sei.dm.dianping.resolution.Location;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Qiang
 * @deprecated 
 */
public class Crawler {
    static String apiKey = "";
    static String apiKey2 = "";
    static double gridSize = 108;
    
    static public final int OK = 0;
    static public final int ZERO_RESULTS = 1;
    static public final int OVER_QUERY_LIMIT = 2;
    static public final int REQUEST_DENIED = 3;
    static public final int INVALID_REQUEST = 4;
    static public final int ERROR = 5;
    
    double[] radius = new double[] {
            0,
            gridSize * Math.sqrt(2) / 2,
            gridSize * 1.5,
            gridSize * 1.5 * Math.sqrt(2),
            gridSize * Math.sqrt(34) / 2
    };
    
    private int iCount = 0, jCount = 0, loadCount = 0;
    
    public void execute(int start, int limit, boolean overlay) {
        boolean stop = false;
        BuildingDatabase database = BuildingDatabase.getInstance();
        File file = new File("raw_data/building.dat");
        try {
            FileWriter fw = new FileWriter(file, !overlay);
            BufferedWriter bw = new BufferedWriter(fw);
            ShopDensity density = new ShopDensity();
            density.initialize(Utility.loadLocationMap());
            
            for (int i = 0; i < density.getSliceCount(); i++) {
                for (int j = 0; j < density.getSliceCount(); j++) {
                    
                    if (density.getDensity(i, j) != 0) {
                        if (loadCount < start) {
                            loadCount++;
                            continue;
                        }
                        
                        if (stop || loadCount - start >= limit) {
                            System.out.println("Stopped. @i=" + iCount + ",j=" + jCount + ",loaded=" + loadCount);
                            System.out.println("Database has saved " + database.size() + " buildings.");
                            bw.close();
                            return;
                        }

                        Location loc = density.getGridCenterLocation(i, j);
                        String json = downloadBuildingInfo(loc.lat, loc.lng, radius[density.getRadius(i, j)]);
                        ArrayList<BuildingRawData> builds = new ArrayList<BuildingRawData>();
                        int result = parseBuildingData(json, builds);
                        if (result == OK) {
                            for (BuildingRawData build : builds) {
                                if (!database.contains(build.id)) {
                                    bw.write(build.toJSONObject().toString());
                                    bw.newLine();
                                    database.add(build);
                                }
                            }
                            iCount = i;
                            jCount = j;
                            loadCount++;
                        } else if (result == ZERO_RESULTS) {
                            iCount = i;
                            jCount = j;
                            loadCount++;
                        } else {
                            String reason;
                            switch (result) {
                                case OVER_QUERY_LIMIT:
                                    reason = "OVER_QUERY_LIMIT";
                                    break;
                                case REQUEST_DENIED:
                                    reason = "REQUEST_DENIED";
                                    break;
                                case INVALID_REQUEST:
                                    reason = "INVALID_REQUEST";
                                    break;
                                default:
                                    reason = "UNKNOWN_ERROR";
                                    break;
                            }
                            System.out.println("Interuptted by " + reason + ".");
                            stop = true;
                        }
                    }
                    
                }
            }
            System.out.println("Finished.");
            System.out.println("Database has saved " + database.size() + " buildings.");
            bw.close();
        } catch (JSONException ex) {
            Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static String downloadBuildingInfo(double lat, double lng, double radius) throws IOException {
        String urlStr = "https://maps.googleapis.com/maps/api/place/search/json?location=" + lat + "," + lng + "&radius=" + radius + "&types=establishment&sensor=false&key=" + apiKey;
        System.out.println(urlStr);
        URL url = new URL(urlStr);
        URLConnection conn = url.openConnection();
        conn.connect();
        InputStreamReader isr = new InputStreamReader(conn.getInputStream());
        BufferedReader reader = new BufferedReader(isr);
        String line;
        StringBuilder buf = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            buf.append(line);
        }
        return buf.toString();
    }
    
    public static int parseBuildingData(String json, List<BuildingRawData> data) throws JSONException {
        JSONObject jo = new JSONObject(json);
        String status = jo.getString("status");
        
        if (status.equals("OK")) {
            JSONArray ja = jo.getJSONArray("results");
            System.out.println(ja.length());
            for (int i = 0; i < ja.length(); i++) {
                BuildingRawData build = new BuildingRawData();
                data.add(build);
                JSONObject bo = ja.getJSONObject(i);

                if (bo.has("geometry")) {
                    JSONObject loc = bo.getJSONObject("geometry").getJSONObject("location");
                    build.location = new Location(loc.getDouble("lat"), loc.getDouble("lng"));
                }
                if (bo.has("icon"))
                    build.icon = bo.getString("icon");
                if (bo.has("id"))
                    build.id = bo.getString("id");
                if (bo.has("name"))
                    build.name = bo.getString("name");
                if (bo.has("rating"))
                    build.rating = bo.getDouble("rating");
                if (bo.has("reference"))
                    build.reference = bo.getString("reference");
                if (bo.has("types")) {
                    JSONArray tyo = bo.getJSONArray("types");
                    build.types = new String[tyo.length()];
                    for (int j = 0; j < tyo.length(); j++) {
                        build.types[j] = tyo.getString(j);
                    }
                }
                if (bo.has("vicinity"))
                    build.vicinity = bo.getString("vicinity");
            }
            return OK;
        }
        if (status.equals("ZERO_RESULTS"))
            return ZERO_RESULTS;
        if (status.equals("OVER_QUERY_LIMIT"))
            return OVER_QUERY_LIMIT;
        if (status.equals("REQUEST_DENIED"))
            return REQUEST_DENIED;
        if (status.equals("INVALID_REQUEST"))
            return INVALID_REQUEST;
        return ERROR;
    }
}
