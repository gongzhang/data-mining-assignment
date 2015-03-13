/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.sei.dm.dianping.building;

import cn.edu.buaa.sei.dm.dianping.resolution.Location;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Qiang
 */
public class BuildingRawData {
    public Location location;
    public String icon;
    public String id;
    public String name;
    public double rating;
    public String reference;
    public String[] types;
    public String vicinity;

    @Override
    public String toString() {
        return "[" + name + "," + vicinity + "," + location.lat + "," + location.lng + "]";
    }
    
    public static BuildingRawData restore(JSONObject jo) {
        BuildingRawData build = new BuildingRawData();
        try {
            JSONObject loc = jo.getJSONObject("location");
            build.location = new Location(loc.getDouble("lat"), loc.getDouble("lng"));
            build.icon = jo.getString("icon");
            build.id = jo.getString("id");
            build.name = jo.getString("name");
            build.rating = jo.getDouble("rating");
            build.reference = jo.getString("reference");
            JSONArray ja = jo.getJSONArray("types");
            build.types = new String[ja.length()];
            for (int i = 0; i < ja.length(); i++) {
                build.types[i] = ja.getString(i);
            }
            build.vicinity = jo.getString("vicinity");
            return build;
        } catch (JSONException ex) {
            Logger.getLogger(BuildingRawData.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public JSONObject toJSONObject() {
        try {
            JSONObject jo = new JSONObject();
            JSONObject loc = new JSONObject();
            loc.put("lat", location.lat);
            loc.put("lng", location.lng);
            jo.put("location", loc);
            jo.put("icon", icon);
            jo.put("id", id);
            jo.put("name", name);
            jo.put("rating", rating);
            jo.put("reference", reference);
            JSONArray ja = new JSONArray();
            for (int i = 0; i < types.length; i++) {
                ja.put(types[i]);
            }
            jo.put("types", ja);
            jo.put("vicinity", vicinity);
            return jo;
        } catch (JSONException ex) {
            Logger.getLogger(BuildingRawData.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
