package com.example.salvadore.utils;

import android.Manifest;
import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.salvadore.clouddb.CloudDBZoneWrapper;
import com.example.salvadore.clouddb.Contact;
import com.example.salvadore.clouddb.Help;
import com.example.salvadore.clouddb.TableUser;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LocationBackgroundService extends Service implements CloudDBZoneWrapper.UiCallBack {

    public static final String BROADCAST_ACTION = "BroadcastLocationFastium";
    private static final int TWO_MINUTES = 1000 * 60;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;
    public int mIntervalLocation;

    Intent intent;
    int counter = 0;


    String currentUID = "";
    int userID;
    double lat, lng;

    int currentHelpID, HelpUserID;
    String HelpUserToken = "", HelpLat = "", HelpLng = "", HelpAddress = "", HelpState = "", HelpUserName = "", HelpDetail = "", HelpHelpers = "", userAddress = "";

    private MyHandler mHandler = new MyHandler();
    private CloudDBZoneWrapper mCloudDBZoneWrapper;

    private static final class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            // dummy
        }
    }

    public LocationBackgroundService() {
        mCloudDBZoneWrapper = new CloudDBZoneWrapper();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);

        AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
        currentUID = user.getUid();
        Log.w(Constants.HELP_LIST_TAG, "GIVE DETAIL : AGConnectUser UID : " + currentUID);

        mHandler.post(() -> {
            mCloudDBZoneWrapper.addCallBacks(LocationBackgroundService.this);
            mCloudDBZoneWrapper.createObjectType();
            mCloudDBZoneWrapper.openCloudDBZone();
            mCloudDBZoneWrapper.getUserDetail();
        });
    }

    @Override
    public void onStart(Intent intent, int startId) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TWO_MINUTES, 0, listener);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TWO_MINUTES, 0, listener);

    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }
    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    //@Override
    //public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        //super.onDestroy();
        //Log.v("STOP_SERVICE", "DONE");
        //locationManager.removeUpdates(listener);
    //}

    public Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {

                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }

    public String getCurrenDateTime(){
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    @Override
    public void onAddOrQuery(List<TableUser> userList) {
        for(int i = 0; i <= userList.size()-1; i++){
            if(userList.get(i).getAuth_id().equals(currentUID)){
                userID = userList.get(i).getUser_id();
            }
        }
        Log.w("USERID ", "USER ID 1 : " + userID);
    }

    @Override
    public void onSubscribe(List<TableUser> userList) {

    }

    @Override
    public void onDelete(List<TableUser> bookInfoList) {

    }

    @Override
    public void updateUiOnError(String errorMessage) {

    }

    @Override
    public void isLoginSuccess(boolean state) {

    }

    @Override
    public void isLastID(int lastUserID) {

    }

    @Override
    public void onHelpAddQuery(List<Help> helpList) {
        for(int i = 0; i <= helpList.size()-1; i++){
            if(helpList.get(i).getUser_id()==userID){
                if(helpList.get(i).getState().equals("Created") || helpList.get(i).getState().equals("Location Updated")){
                    currentHelpID = helpList.get(i).getId();
                    HelpUserID = helpList.get(i).getUser_id();
                    HelpUserToken = helpList.get(i).getUser_token();
                    HelpLat = helpList.get(i).getLat();
                    HelpLng = helpList.get(i).getLng();
                    HelpAddress = helpList.get(i).getAddress();
                    HelpState = helpList.get(i).getState();
                    HelpUserName = helpList.get(i).getUser_name();
                    HelpDetail = helpList.get(i).getDetail();
                    HelpHelpers = helpList.get(i).getHelpers();
                    UpdateHelpLocation();
                    Log.w("USERID ", "USER ID IN FOR : " + helpList.get(i).getId());
                }

            }
        }
    }

    @Override
    public void onContactsAddQuery(List<Contact> contactList) {

    }

    @Override
    public void isInsert(boolean inserted) {

    }

    public class MyLocationListener implements LocationListener
    {

        public void onLocationChanged(final Location loc)
        {
            Log.i("**", "Location changed" + loc.getLatitude() + " " + loc.getLongitude());
            if(isBetterLocation(loc, previousBestLocation)) {
                loc.getLatitude();
                loc.getLongitude();
                intent.putExtra("Latitude", String.valueOf(loc.getLatitude()));
                intent.putExtra("Longitude", String.valueOf(loc.getLongitude()));
                intent.putExtra("Provider", loc.getProvider());
                sendBroadcast(intent);

                lat = loc.getLatitude();
                lng = loc.getLongitude();

                Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geo.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                    userAddress = addresses.get(0).getAddressLine(0);
                    String cityName = addresses.get(0).getAdminArea();
                    Log.e(Constants.HELP_DETAIL_TAG, "CITY : " + cityName);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                checkHelpList();


                //Log.d("LOCATION", "LON:" + String.valueOf(loc.getLongitude()) + " LAT:" + String.valueOf(loc.getLatitude()));
                //Toast.makeText( getApplicationContext(), "LON:" + String.valueOf(loc.getLongitude()) + " LAT" + String.valueOf(loc.getLatitude()), Toast.LENGTH_SHORT ).show();
            }
        }

        public void onProviderDisabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "Gps Deshabilitado", Toast.LENGTH_SHORT ).show();
        }


        public void onProviderEnabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "Gps Habilitado", Toast.LENGTH_SHORT).show();
        }

        public void onStatusChanged(String provider, int status, Bundle extras)
        {

        }
    }

    public void checkHelpList(){
        mHandler.post(() -> {
            Log.w("CHECK HELP LIST ", "CHECK HELP LIST: ");
            mCloudDBZoneWrapper.getAllHep();
        });
    }

    public void UpdateHelpLocation(){
        Log.w("UPDATE HELP LOCATION ", "UPDATE HELP LOCATION ");
        Help help = new Help();
        help.setId(currentHelpID);
        help.setUser_id(HelpUserID);
        help.setUser_token(getCurrenDateTime());
        help.setLat(Double.toString(lat));
        help.setLng(Double.toString(lng));
        help.setAddress(userAddress);
        help.setState("Location Updated");
        help.setUser_name(HelpUserName);
        help.setDetail(HelpDetail);
        help.setHelpers(HelpHelpers);
        mHandler.post(() -> {
            mCloudDBZoneWrapper.insertHelp(help);
        });
    }
}
