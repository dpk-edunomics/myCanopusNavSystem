package com.canopus.MapDataParser;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {
    public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

        List<List<HashMap<String, String>>> mRoutes = new ArrayList<List<HashMap<String, String>>>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {
            jRoutes = jObject.getJSONArray("routes");

            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List list = decodePoly(polyline);

                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                            hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
                            path.add(hm);
                        }
                    }
                    mRoutes.add(path);
                }
            }
        } catch (JSONException e) {

            e.printStackTrace();

        } catch (Exception e) { }

        return mRoutes;
    }

    private List decodePoly(String encoded) {

        List mList = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int mInt, mShift = 0, mResult = 0;
            do {
                mInt = encoded.charAt(index++) - 63;
                mResult |= (mInt & 0x1f) << mShift;
                mShift += 5;
            } while (mInt >= 0x20);
            int dlat = ((mResult & 1) != 0 ? ~(mResult >> 1) : (mResult >> 1));
            lat += dlat;

            mShift = 0;
            mResult = 0;
            do {
                mInt = encoded.charAt(index++) - 63;
                mResult |= (mInt & 0x1f) << mShift;
                mShift += 5;
            } while (mInt >= 0x20);
            int mLng = ((mResult & 1) != 0 ? ~(mResult >> 1) : (mResult >> 1));
            lng += mLng;

            LatLng mLatLng = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            mList.add(mLatLng);
        }

        return mList;
    }
}