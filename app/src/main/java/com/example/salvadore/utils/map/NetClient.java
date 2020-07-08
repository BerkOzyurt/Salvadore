package com.example.salvadore.utils.map;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.huawei.hms.maps.model.LatLng;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetClient {
    private static final String TAG = "NetClient";

    private static NetClient netClient;

    private static OkHttpClient client;

    private String mDefaultKey = "xxx";

    private String mWalkingRoutePlanningURL = "https://mapapi.cloud.huawei.com/mapApi/v1/routeService/walking";

    private String mBicyclingRoutePlanningURL = "https://mapapi.cloud.huawei.com/mapApi/v1/routeService/bicycling";

    private String mDrivingRoutePlanningURL = "https://mapapi.cloud.huawei.com/mapApi/v1/routeService/driving";

    private static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private NetClient() {
        client = initOkHttpClient();
    }

    public OkHttpClient initOkHttpClient() {
        if (client == null) {
            client = new OkHttpClient.Builder().readTimeout(10000, TimeUnit.MILLISECONDS)// Set the read timeout.
                    .connectTimeout(10000, TimeUnit.MILLISECONDS)// Set the connect timeout.
                    .build();
        }
        return client;
    }

    public static NetClient getNetClient() {
        if (netClient == null) {
            netClient = new NetClient();
        }
        return netClient;
    }

    public Response getWalkingRoutePlanningResult(LatLng latLng1, LatLng latLng2, boolean needEncode) {
        String key = mDefaultKey;
        if (needEncode) {
            try {
                key = URLEncoder.encode(mDefaultKey, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        String url = mWalkingRoutePlanningURL + "?key=" + key;

        Response response = null;
        JSONObject origin = new JSONObject();
        JSONObject destination = new JSONObject();
        JSONObject json = new JSONObject();
        try {
            origin.put("lat", latLng1.latitude);
            origin.put("lng", latLng1.longitude);

            destination.put("lat", latLng2.latitude);
            destination.put("lng", latLng2.longitude);

            json.put("origin", origin);
            json.put("destination", destination);

            RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
            Request request = new Request.Builder().url(url).post(requestBody).build();
            response = getNetClient().initOkHttpClient().newCall(request).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public Response getBicyclingRoutePlanningResult(LatLng latLng1, LatLng latLng2, boolean needEncode) {
        String key = mDefaultKey;
        if (needEncode) {
            try {
                key = URLEncoder.encode(mDefaultKey, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        String url = mBicyclingRoutePlanningURL + "?key=" + key;

        Response response = null;
        JSONObject origin = new JSONObject();
        JSONObject destination = new JSONObject();
        JSONObject json = new JSONObject();
        try {
            origin.put("lat", latLng1.latitude);
            origin.put("lng", latLng1.longitude);

            destination.put("lat", latLng2.latitude);
            destination.put("lng", latLng2.longitude);

            json.put("origin", origin);
            json.put("destination", destination);

            RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
            Request request = new Request.Builder().url(url).post(requestBody).build();
            response = getNetClient().initOkHttpClient().newCall(request).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public Response getDrivingRoutePlanningResult(LatLng latLng1, LatLng latLng2, boolean needEncode) {
        String key = mDefaultKey;
        if (needEncode) {
            try {
                key = URLEncoder.encode(mDefaultKey, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        String url = mDrivingRoutePlanningURL + "?key=" + key;

        Response response = null;
        JSONObject origin = new JSONObject();
        JSONObject destination = new JSONObject();
        JSONObject json = new JSONObject();
        try {
            origin.put("lat", latLng1.latitude);
            origin.put("lng", latLng1.longitude);

            destination.put("lat", latLng2.latitude);
            destination.put("lng", latLng2.longitude);

            json.put("origin", origin);
            json.put("destination", destination);

            RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
            Request request = new Request.Builder().url(url).post(requestBody).build();
            response = getNetClient().initOkHttpClient().newCall(request).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
