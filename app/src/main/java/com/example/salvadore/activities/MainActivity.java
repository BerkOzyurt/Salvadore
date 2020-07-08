package com.example.salvadore.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.salvadore.utils.LocationBackgroundService;
import com.example.salvadore.fragments.AboutAppFragment;
import com.example.salvadore.fragments.ContactsFragment;
import com.example.salvadore.fragments.GiveDetailFragment;
import com.example.salvadore.fragments.HowToUseFragment;
import com.example.salvadore.fragments.PremiumFragment;
import com.example.salvadore.fragments.PrivacyPolicyFragment;
import com.example.salvadore.fragments.WifiStatusFragment;
import com.example.salvadore.utils.Constants;
import com.example.salvadore.utils.MyHelper;
import com.example.salvadore.R;
import com.example.salvadore.fragments.HelpDetailFragment;
import com.example.salvadore.fragments.HelpListFragment;
import com.example.salvadore.fragments.HomeFragment;
import com.example.salvadore.fragments.MapFragment;
import com.example.salvadore.fragments.ProfileFragment;
import com.example.salvadore.fragments.SettingsFragment;
import com.example.salvadore.models.ScreenStack;
import com.huawei.agconnect.apms.APMS;
import com.huawei.agconnect.appmessaging.AGConnectAppMessaging;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.aaid.entity.AAIDResult;
import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.analytics.HiAnalyticsTools;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationServices;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.mainBottomNeedListIcon)
    ImageView mainBottomNeedListIcon;
    @BindView(R.id.mainBottomContactsIcon)
    ImageView mainBottomContactsIcon;
    @BindView(R.id.mainBottomMapIcon)
    ImageView mainBottomMapIcon;
    @BindView(R.id.mainBottomSettingsIcon)
    ImageView mainBottomSettingsIcon;

    @BindView(R.id.mainBottomNeedList)
    LinearLayout mainBottomNeedList;
    @BindView(R.id.mainBottomContacts)
    LinearLayout mainBottomContacts;
    @BindView(R.id.mainBottomMap)
    LinearLayout mainBottomMap;
    @BindView(R.id.mainBottomSettings)
    LinearLayout mainBottomSettings;
    @BindView(R.id.mainBottomCenterBtn)
    LinearLayout mainBottomCenterBtn;

    @BindView(R.id.btn_back)
    ImageView btn_back;

    Fragment selectedFragment = null;
    private int selectedTab = -1;

    String pushtoken = "";

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;

    //AppMessaging
    private  String TAG = "MainActivity";
    private AGConnectAppMessaging appMessaging;
    private HiAnalyticsInstance instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        APMS.getInstance().enableCollection(true);

        setSelectedTab(-1,Constants.TAB_HOME);

        checkPermissions();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mainBottomContacts.setOnClickListener(this);
        mainBottomNeedList.setOnClickListener(this);
        mainBottomMap.setOnClickListener(this);
        mainBottomSettings.setOnClickListener(this);
        mainBottomCenterBtn.setOnClickListener(this);
        btn_back.setOnClickListener(this);

        startService(new Intent(MainActivity.this, LocationBackgroundService.class));
    }

    public void getAaid(){
        HmsInstanceId inst = HmsInstanceId.getInstance(this);
        Task<AAIDResult> idResult =  inst.getAAID();
        idResult.addOnSuccessListener(new OnSuccessListener<AAIDResult>() {
            @Override
            public void onSuccess(AAIDResult aaidResult) {
                String aaid = aaidResult.getId();
                Log.d(TAG, "getAAID success:" + aaid );
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "getAAID failure:" + e);
            }
        });
        HiAnalyticsTools.enableLog();
        instance = HiAnalytics.getInstance(this);
        instance.setAnalyticsEnabled(true);
        instance.regHmsSvcEvent();
        inAppMessaging();
    }

    private void inAppMessaging() {
        appMessaging = AGConnectAppMessaging.getInstance();
        appMessaging.setFetchMessageEnable(true);
        appMessaging.setDisplayEnable(true);
        appMessaging.setForceFetch();
    }


    public void getLastLocationAndAddress() {
        try {
            Task<Location> lastLocation = mFusedLocationProviderClient.getLastLocation();
            lastLocation.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location == null) {
                        Log.i(Constants.MAIN_TAG, "getLastLocation onSuccess location is null");
                        return;
                    }
                    Log.i(Constants.MAIN_TAG,"getLastLocation onSuccess location[Longitude,Latitude]:" + location.getLongitude() + ","+ location.getLatitude());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Log.e(Constants.MAIN_TAG, "getLastLocation onFailure:" + e.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(Constants.MAIN_TAG, "getLastLocation exception:" + e.getMessage());
        }
    }

    public void setSelectedTab(int currentScreenId, int nextScreenId){
        if(selectedTab == nextScreenId){
            return;
        }
        refreshTabIcons();
        selectedTab = nextScreenId;
        Fragment fragment = null;

        ArrayList<ScreenStack> arrayList = MyHelper.getScreenStackData(getApplicationContext());

        switch (nextScreenId){
            case Constants.TAB_HOME:
                btn_back.setVisibility(View.INVISIBLE);
                fragment = new HomeFragment();
                break;
            case Constants.TAB_HELP_LIST:
                fragment = new HelpListFragment();
                mainBottomNeedListIcon.setImageResource(R.drawable.list_selected);
                break;
            case Constants.TAB_PROFILE:
                fragment = new ProfileFragment();
                mainBottomSettingsIcon.setImageResource(R.drawable.settings_selected);
                break;
            case Constants.TAB_CONTACTS:
                fragment = new ContactsFragment();
                mainBottomContactsIcon.setImageResource(R.drawable.contact_selected);
                break;
            case Constants.TAB_SETINGS:
                fragment = new SettingsFragment();
                mainBottomSettingsIcon.setImageResource(R.drawable.settings_selected);
                break;
            case Constants.TAB_MAP:
                fragment = new MapFragment();
                mainBottomMapIcon.setImageResource(R.drawable.map_selected);
                break;
            case Constants.TAB_HELP_DETAIL:
                fragment = new HelpDetailFragment();
                mainBottomNeedListIcon.setImageResource(R.drawable.list_selected);
                break;
            case Constants.TAB_GIVE_DETAIL:
                fragment = new GiveDetailFragment();
                break;
            case Constants.TAB_PREMIUM:
                fragment = new PremiumFragment();
                mainBottomSettingsIcon.setImageResource(R.drawable.settings_selected);
                break;
            case Constants.TAB_PRIVACY_POLICY:
                fragment = new PrivacyPolicyFragment();
                mainBottomSettingsIcon.setImageResource(R.drawable.settings_selected);
                break;
            case Constants.TAB_ABOUT_APP:
                fragment = new AboutAppFragment();
                mainBottomSettingsIcon.setImageResource(R.drawable.settings_selected);
                break;
            case Constants.TAB_HOW_TO_USE:
                fragment = new HowToUseFragment();
                mainBottomSettingsIcon.setImageResource(R.drawable.settings_selected);
                break;
            case Constants.TAB_WIFI_STATUS:
                fragment = new WifiStatusFragment();
                mainBottomSettingsIcon.setImageResource(R.drawable.settings_selected);
                break;
            default:
                break;
        }

        if(fragment != null){
            setSelectedFragment(fragment);
            if(currentScreenId != -1){
                arrayList = addScreenToStack(arrayList,currentScreenId);
            }else{
                arrayList = addScreenToStack(arrayList,Constants.TAB_HOME);
            }
            MyHelper.setScreenStackData(getApplicationContext(),arrayList);
        }
    }

    private void setSelectedFragment(Fragment fragment){
        selectedFragment = fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();
    }

    private ArrayList<ScreenStack> addScreenToStack(ArrayList<ScreenStack> arrayList, int screenId){
        boolean isAdded = false;
        Iterator<ScreenStack> iterator = arrayList.iterator();
        while (iterator.hasNext()){
            ScreenStack screenStack = iterator.next();
            if(screenStack.getScreenId() == screenId){
                isAdded = true;
                break;
            }
        }
        if(!isAdded){
            ScreenStack screenStack = new ScreenStack();
            screenStack.setScreenId(screenId);
            arrayList.add(screenStack);
        }
        return arrayList;
    }

    private void refreshTabIcons(){
        btn_back.setVisibility(View.VISIBLE);

        mainBottomContactsIcon.setImageResource(R.drawable.contact_unselected);
        mainBottomNeedListIcon.setImageResource(R.drawable.list_unselected);
        mainBottomMapIcon.setImageResource(R.drawable.map_unselected);
        mainBottomSettingsIcon.setImageResource(R.drawable.settings_unselected);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mainBottomContacts:
                Log.i(Constants.MAIN_TAG, "GO CONTACTS");
                setSelectedTab(-1,Constants.TAB_CONTACTS);
                break;
            case R.id.mainBottomNeedList:
                Log.i(Constants.MAIN_TAG, "GO HELP LIST");
                setSelectedTab(-1,Constants.TAB_HELP_LIST);
                break;
            case R.id.mainBottomMap:
                getLastLocationAndAddress();
                Log.i(Constants.MAIN_TAG, "GO MAP");
                setSelectedTab(-1,Constants.TAB_MAP);
                break;
            case R.id.mainBottomSettings:
                Log.i(Constants.MAIN_TAG, "GO SETINGS");
                setSelectedTab(-1,Constants.TAB_SETINGS);
                break;
            case R.id.mainBottomCenterBtn:
                Log.i(Constants.MAIN_TAG, "GO HOME");
                getAaid();
                setSelectedTab(-1,Constants.TAB_HOME);
                break;
            case R.id.btn_back:
                onBackPressed();
            default:
                break;
        }
    }

    private void backPressOperations(){
        ArrayList<ScreenStack> arrayList = MyHelper.getScreenStackData(getApplicationContext());
        if(arrayList.size() == 0){
            super.onBackPressed();
            return;
        }

        ScreenStack screenStack = arrayList.get(arrayList.size() - 1);
        setSelectedTab(-1, screenStack.getScreenId());
        arrayList.remove(arrayList.size() - 1);
        MyHelper.setScreenStackData(getApplicationContext(), arrayList);
    }

    @Override
    public void onBackPressed(){
        if(selectedFragment instanceof HomeFragment){
            //super.onBackPressed();
        }
        else{
            backPressOperations();
        }
    }

    public void checkPermissions(){
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            Log.i(Constants.MAIN_TAG, "sdk < 28 Q");
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] strings =
                        {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                ActivityCompat.requestPermissions(this, strings, 1);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    "android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED) {
                String[] strings = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        "android.permission.ACCESS_BACKGROUND_LOCATION"};
                ActivityCompat.requestPermissions(this, strings, 2);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.i(Constants.MAIN_TAG, "onRequestPermissionsResult: apply LOCATION PERMISSION successful");
            } else {
                Log.i(Constants.MAIN_TAG, "onRequestPermissionsResult: apply LOCATION PERMISSSION  failed");
            }
        }

        if (requestCode == 2) {
            if (grantResults.length > 2 && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.i(Constants.MAIN_TAG, "onRequestPermissionsResult: apply ACCESS_BACKGROUND_LOCATION successful");
            } else {
                Log.i(Constants.MAIN_TAG, "onRequestPermissionsResult: apply ACCESS_BACKGROUND_LOCATION  failed");
            }
        }
    }
}
