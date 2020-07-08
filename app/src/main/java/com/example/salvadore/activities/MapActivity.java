package com.example.salvadore.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.salvadore.R;
import com.example.salvadore.fragments.HomeFragment;
import com.example.salvadore.utils.Constants;
import com.example.salvadore.utils.GlobalData;
import com.example.salvadore.utils.map.NetworkRequestManager;
import com.huawei.agconnect.apms.APMS;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.maps.CameraUpdate;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.SupportMapFragment;
import com.huawei.hms.maps.model.BitmapDescriptorFactory;
import com.huawei.hms.maps.model.CameraPosition;
import com.huawei.hms.maps.model.Circle;
import com.huawei.hms.maps.model.CircleOptions;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.LatLngBounds;
import com.huawei.hms.maps.model.Marker;
import com.huawei.hms.maps.model.MarkerOptions;
import com.huawei.hms.maps.model.Polyline;
import com.huawei.hms.maps.model.PolylineOptions;
import com.huawei.hms.maps.util.LogM;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

public class MapActivity extends BaseActivityV2 implements OnMapReadyCallback {

    private SupportMapFragment mSupportMapFragment;
    private TextView textView_yourAddress, textView_helpAddress;
    private Button btn_goWithGoogleMap;
    private HuaweiMap hMap;
    private Marker mMarkerOrigin;
    private Marker mMarkerDestination;
    private List<Polyline> mPolylines = new ArrayList<>();
    private List<List<LatLng>> mPaths = new ArrayList<>();
    private LatLngBounds mLatLngBounds;

    GlobalData globalData = GlobalData.getInstance();
    double helpLat = globalData.getHelpLat();
    double helpLng = globalData.getHelpLng();
    String helpAdres = globalData.getHelpAdres();
    String helpUserName = globalData.getHelpUserName();

    private LatLng latLng2 = new LatLng(helpLat, helpLng);
    public LatLng latLng3;
    String MyAdres = "";

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    renderRoute(mPaths, mLatLngBounds);
                    break;
                case 1:
                    Bundle bundle = msg.getData();
                    String errorMsg = bundle.getString("errorMsg");
                    Toast.makeText(MapActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
    private static final String[] RUNTIME_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET
    };
    private static final int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        if (!hasPermissions(getApplicationContext(), RUNTIME_PERMISSIONS)) {
            ActivityCompat.requestPermissions(MapActivity.this, RUNTIME_PERMISSIONS, REQUEST_CODE);
        }

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        APMS.getInstance().enableCollection(true);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mapfragment_routeplanningdemo);
        if (fragment instanceof SupportMapFragment) {
            mSupportMapFragment = (SupportMapFragment) fragment;
            mSupportMapFragment.getMapAsync(this);
        }
        textView_yourAddress = findViewById(R.id.textView_yourAddress);
        textView_helpAddress = findViewById(R.id.textView_helpAddress);
        textView_helpAddress.setText(helpAdres);
        btn_goWithGoogleMap = findViewById(R.id.btn_goWithGoogleMap);
        btn_goWithGoogleMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps();
            }
        });
    }

    public void openGoogleMaps(){
        String uri = "http://maps.google.com/maps?q=loc:" + helpLat + "," + helpLng + " (" + "Help!" + ")";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        try{
            startActivity(intent);
        }catch (ActivityNotFoundException ex){
            try{
                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(unrestrictedIntent);
            }catch (ActivityNotFoundException innerEx){
                Toast.makeText(MapActivity.this,"Please install a maps application", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void getLastLocationAndAddress() {
        showProgressDialog();
        try {
            Task<Location> lastLocation = mFusedLocationProviderClient.getLastLocation();
            lastLocation.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location == null) {
                        Log.i(Constants.MAP_TAG, "getLastLocation onSuccess location is null");
                        return;
                    }
                    Log.i(Constants.MAP_TAG,
                            "LatLng3 : " + location.getLongitude() + ","
                                    + location.getLatitude());
                    latLng3 = new LatLng(location.getLatitude(),location.getLongitude());

                    Geocoder geo = new Geocoder(MapActivity.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        MyAdres = addresses.get(0).getAddressLine(0);
                        textView_yourAddress.setText(MyAdres);
                        addDestinationMarker(latLng3);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    dismisProgressDialog();
                    Toast.makeText(getApplicationContext(),"Location error. Please check your network connection.",Toast.LENGTH_SHORT).show();
                    Log.e(Constants.MAP_TAG, "getLastLocation onFailure:" + e.getMessage());
                }
            });
        } catch (Exception e) {
            dismisProgressDialog();
            Toast.makeText(getApplicationContext(),"Location error. Please check your network connection.",Toast.LENGTH_SHORT).show();
            Log.e(Constants.MAP_TAG, "getLastLocation exception:" + e.getMessage());
        }
    }
    @Override
    public void onMapReady(HuaweiMap huaweiMap) {
        hMap = huaweiMap;
        hMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng2, 13));
        hMap.setMyLocationEnabled(true);
        getLastLocationAndAddress();
        addOriginMarker(latLng2);

    }

    public void getWalkingRouteResult(View view) {
        removePolylines();
        NetworkRequestManager.getWalkingRoutePlanningResult(latLng3, latLng2,
                new NetworkRequestManager.OnNetworkListener() {
                    @Override
                    public void requestSuccess(String result) {
                        generateRoute(result);
                    }

                    @Override
                    public void requestFail(String errorMsg) {
                        Message msg = Message.obtain();
                        Bundle bundle = new Bundle();
                        bundle.putString("errorMsg", errorMsg);
                        msg.what = 1;
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                    }
                });
    }

    public void getBicyclingRouteResult(View view) {
        removePolylines();
        NetworkRequestManager.getBicyclingRoutePlanningResult(latLng3, latLng2,
                new NetworkRequestManager.OnNetworkListener() {
                    @Override
                    public void requestSuccess(String result) {
                        generateRoute(result);
                    }

                    @Override
                    public void requestFail(String errorMsg) {
                        Message msg = Message.obtain();
                        Bundle bundle = new Bundle();
                        bundle.putString("errorMsg", errorMsg);
                        Log.d("sfj", errorMsg);
                        msg.what = 1;
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                    }
                });
    }

    public void getDrivingRouteResult(View view) {
        removePolylines();
        NetworkRequestManager.getDrivingRoutePlanningResult(latLng3, latLng2,
                new NetworkRequestManager.OnNetworkListener() {
                    @Override
                    public void requestSuccess(String result) {
                        generateRoute(result);
                    }

                    @Override
                    public void requestFail(String errorMsg) {
                        Message msg = Message.obtain();
                        Bundle bundle = new Bundle();
                        bundle.putString("errorMsg", errorMsg);
                        msg.what = 1;
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                    }
                });
    }

    @SuppressLint("LongLogTag")
    private void generateRoute(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray routes = jsonObject.optJSONArray("routes");
            if (null == routes || routes.length() == 0) {
                return;
            }
            JSONObject route = routes.getJSONObject(0);

            JSONObject bounds = route.optJSONObject("bounds");
            if (null != bounds && bounds.has("southwest") && bounds.has("northeast")) {
                JSONObject southwest = bounds.optJSONObject("southwest");
                JSONObject northeast = bounds.optJSONObject("northeast");
                LatLng sw = new LatLng(southwest.optDouble("lat"), southwest.optDouble("lng"));
                LatLng ne = new LatLng(northeast.optDouble("lat"), northeast.optDouble("lng"));
                mLatLngBounds = new LatLngBounds(sw, ne);
            }

            // get paths
            JSONArray paths = route.optJSONArray("paths");
            for (int i = 0; i < paths.length(); i++) {
                JSONObject path = paths.optJSONObject(i);
                List<LatLng> mPath = new ArrayList<>();

                JSONArray steps = path.optJSONArray("steps");
                for (int j = 0; j < steps.length(); j++) {
                    JSONObject step = steps.optJSONObject(j);

                    JSONArray polyline = step.optJSONArray("polyline");
                    for (int k = 0; k < polyline.length(); k++) {
                        if (j > 0 && k == 0) {
                            continue;
                        }
                        JSONObject line = polyline.getJSONObject(k);
                        double lat = line.optDouble("lat");
                        double lng = line.optDouble("lng");
                        LatLng latLng = new LatLng(lat, lng);
                        mPath.add(latLng);
                    }
                }
                mPaths.add(i, mPath);
            }
            mHandler.sendEmptyMessage(0);

        } catch (JSONException e) {
            Log.e(Constants.MAP_TAG, "JSONException" + e.toString());
        }
    }

    private void renderRoute(List<List<LatLng>> paths, LatLngBounds latLngBounds) {
        if (null == paths || paths.size() <= 0 || paths.get(0).size() <= 0) {
            return;
        }

        for (int i = 0; i < paths.size(); i++) {
            List<LatLng> path = paths.get(i);
            PolylineOptions options = new PolylineOptions().color(Color.BLUE).width(5);
            for (LatLng latLng : path) {
                options.add(latLng);
            }

            Polyline polyline = hMap.addPolyline(options);
            mPolylines.add(i, polyline);
        }

        addOriginMarker(paths.get(0).get(0));
        addDestinationMarker(paths.get(0).get(paths.get(0).size() - 1));

        if (null != latLngBounds) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, 5);
            hMap.moveCamera(cameraUpdate);
        } else {
            hMap.moveCamera(CameraUpdateFactory.newLatLngZoom(paths.get(0).get(0), 13));
        }

    }
    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void addOriginMarker(LatLng latLng) {
        if (null != mMarkerOrigin) {
            mMarkerOrigin.remove();
        }
        mMarkerOrigin = hMap.addMarker(new MarkerOptions().position(latLng)
                .anchor(0.5f, 0.9f)
                .title("Help!")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet(helpUserName));
    }

    private void addDestinationMarker(LatLng latLng) {
        if (null != mMarkerDestination) {
            mMarkerDestination.remove();
        }
        mMarkerDestination = hMap.addMarker(
                new MarkerOptions().position(latLng).anchor(0.5f, 0.9f).title("Me").snippet(MyAdres));
        dismisProgressDialog();
    }

    private void removePolylines() {
        for (Polyline polyline : mPolylines) {
            polyline.remove();
        }

        mPolylines.clear();
        mPaths.clear();
        mLatLngBounds = null;
    }
}