package com.example.salvadore.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.salvadore.R;
import com.example.salvadore.activities.MainActivity;
import com.example.salvadore.activities.MapActivity;
import com.example.salvadore.clouddb.CloudDBZoneWrapper;
import com.example.salvadore.clouddb.Contact;
import com.example.salvadore.clouddb.Help;
import com.example.salvadore.clouddb.TableUser;
import com.example.salvadore.utils.Constants;
import com.example.salvadore.utils.GlobalData;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;
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
import com.huawei.hms.maps.model.BitmapDescriptorFactory;
import com.huawei.hms.maps.model.CameraPosition;
import com.huawei.hms.maps.model.Circle;
import com.huawei.hms.maps.model.CircleOptions;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.Marker;
import com.huawei.hms.maps.model.MarkerOptions;
import com.huawei.hms.maps.util.LogM;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HelpDetailFragment extends BaseFragmentV2 implements View.OnClickListener, CloudDBZoneWrapper.UiCallBack {

    View rootView;

    @BindView(R.id.btn_showMap)
    Button btn_showMap;
    @BindView(R.id.btn_helpDetailCloseHelp)
    Button btn_helpDetailCloseHelp;
    @BindView(R.id.btn_helpDetailUpdateLocation)
    Button btn_helpDetailUpdateLocation;

    @BindView(R.id.textView_helpDetailName)
    TextView textView_helpDetailName;
    @BindView(R.id.textView_helpDetailState)
    TextView textView_helpDetailState;
    @BindView(R.id.textView_helpDetailDate)
    TextView textView_helpDetailDate;
    @BindView(R.id.textView_helpDetailDetails)
    TextView textView_helpDetailDetails;
    @BindView(R.id.textView_helpDetailAddress)
    TextView textView_helpDetailAddress;

    @BindView(R.id.linearLayout_AcceptRequest)
    LinearLayout linearLayout_AcceptRequest;
    @BindView(R.id.btn_helpDetailAcceptHelp)
    Button btn_helpDetailAcceptHelp;
    @BindView(R.id.textView_helpAccepted)
    TextView textView_helpAccepted;

    GlobalData globalData = GlobalData.getInstance();

    private MyHandler mHandler = new MyHandler();
    private CloudDBZoneWrapper mCloudDBZoneWrapper;

    int currentHelpID, HelpUserID;
    String currentUID = "", userAddress = "", currentUserName = "";
    int userID;
    double lat, lng;
    String HelpUserToken = "", HelpLat = "", HelpLng = "", HelpAddress = "", HelpState = "", HelpUserName = "", HelpDetail = "", HelpHelpers = "";
    String CDBOperation = "";

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;

    private static final class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            // dummy
        }
    }

    public HelpDetailFragment() {
        mCloudDBZoneWrapper = new CloudDBZoneWrapper();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_help_detail, container, false);
        ButterKnife.bind(this,rootView);

        currentHelpID = globalData.getHelpID();

        AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
        currentUID = user.getUid();
        Log.w(Constants.HELP_DETAIL_TAG, "GIVE DETAIL : AGConnectUser UID : " + currentUID);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(HelpDetailFragment.this.getActivity());
        getLastLocationAndAddress();

        btn_showMap.setOnClickListener(this);
        btn_helpDetailCloseHelp.setOnClickListener(this);
        btn_helpDetailUpdateLocation.setOnClickListener(this);
        btn_helpDetailAcceptHelp.setOnClickListener(this);

        showProgressDialog();

        mHandler.post(() -> {
            mCloudDBZoneWrapper.addCallBacks(HelpDetailFragment.this);
            mCloudDBZoneWrapper.createObjectType();
            mCloudDBZoneWrapper.openCloudDBZone();
            //mCloudDBZoneWrapper.getUserDetail();
            //mCloudDBZoneWrapper.getAllHep();
        });

        return rootView;
    }

    public void getLastLocationAndAddress() {
        try {
            Task<Location> lastLocation = mFusedLocationProviderClient.getLastLocation();
            lastLocation.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location == null) {
                        Log.i(Constants.HELP_DETAIL_TAG, "getLastLocation onSuccess location is null");
                        return;
                    }
                    Log.i(Constants.HELP_DETAIL_TAG,
                            "getLastLocation onSuccess location[Longitude,Latitude]:" + location.getLongitude() + ","
                                    + location.getLatitude());
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    Geocoder geo = new Geocoder(HelpDetailFragment.this.getContext(), Locale.getDefault());
                    try {
                        List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        userAddress = addresses.get(0).getAddressLine(0);
                        String cityName = addresses.get(0).getAdminArea();
                        Log.e(Constants.HELP_DETAIL_TAG, "CITY : " + cityName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mHandler.post(() -> {
                        mCloudDBZoneWrapper.getUserDetail();
                    });

                    return;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Log.e(Constants.HELP_DETAIL_TAG, "getLastLocation onFailure:" + e.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(Constants.HELP_DETAIL_TAG, "getLastLocation exception:" + e.getMessage());
        }
    }

    public void acceptHelp(){
        if(HelpHelpers.equals("None")){
            HelpHelpers = currentUserName;
        }else{
            HelpHelpers = HelpHelpers + ", " + currentUserName;
        }

        CDBOperation = "Accept";
        Help help = new Help();
        help.setId(currentHelpID);
        help.setUser_id(HelpUserID);
        help.setUser_token(HelpUserToken);
        help.setLat(HelpLat);
        help.setLng(HelpLng);
        help.setAddress(HelpAddress);
        help.setState(HelpState);
        help.setUser_name(HelpUserName);
        help.setDetail(HelpDetail);
        help.setHelpers(HelpHelpers);
        mHandler.post(() -> {
            mCloudDBZoneWrapper.insertHelp(help);
        });
    }

    public void closeHelp(){
        CDBOperation = "Close";
        Help help = new Help();
        help.setId(currentHelpID);
        help.setUser_id(HelpUserID);
        help.setUser_token(HelpUserToken);
        help.setLat(HelpLat);
        help.setLng(HelpLng);
        help.setAddress(HelpAddress);
        help.setState("Closed");
        help.setUser_name(HelpUserName);
        help.setDetail(HelpDetail);
        help.setHelpers(HelpHelpers);
        mHandler.post(() -> {
            mCloudDBZoneWrapper.insertHelp(help);
        });
    }

    public void updateHelpLocation(){
        CDBOperation = "Update";
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_showMap:
                Intent intent = new Intent(getContext(), MapActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_helpDetailCloseHelp:
                closeHelp();
                break;
            case R.id.btn_helpDetailUpdateLocation:
                updateHelpLocation();
                break;
            case R.id.btn_helpDetailAcceptHelp:
                acceptHelp();
                break;
            default:
                break;
        }
    }

    public void showAlert(String message){
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Salvadore");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity mainActivity = (MainActivity) getActivity();
                        mainActivity.setSelectedTab(Constants.TAB_HELP_DETAIL, Constants.TAB_HELP_LIST);
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onAddOrQuery(List<TableUser> userList) {
        for(int i = 0; i <= userList.size()-1; i++){
            if(userList.get(i).getAuth_id().equals(currentUID)){
                userID = userList.get(i).getUser_id();
                currentUserName = userList.get(i).getUser_name();
            }
        }
        mHandler.post(() -> {
            mCloudDBZoneWrapper.getAllHep();
        });
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
            if(helpList.get(i).getId().equals(currentHelpID)){
                if(helpList.get(i).getUser_id()==userID){
                    if(helpList.get(i).getState().equals("Closed")){
                        btn_helpDetailCloseHelp.setVisibility(View.GONE);
                    }else{
                        btn_helpDetailCloseHelp.setVisibility(View.VISIBLE);
                    }
                }else{
                    String[] HelperList = helpList.get(i).getHelpers().split(",");
                    for(int k=0; k<=HelpHelpers.length(); k++){
                        Log.e(Constants.HELP_DETAIL_TAG, "HELPERS : " + HelperList[k]);
                        if(HelperList[k].equals(currentUserName)){
                            linearLayout_AcceptRequest.setVisibility(View.GONE);
                            textView_helpAccepted.setVisibility(View.VISIBLE);
                        }else{
                            linearLayout_AcceptRequest.setVisibility(View.VISIBLE);
                        }
                    }

                    btn_helpDetailCloseHelp.setVisibility(View.GONE);
                }

                HelpUserID = helpList.get(i).getUser_id();
                HelpUserToken = helpList.get(i).getUser_token();
                HelpLat = helpList.get(i).getLat();
                HelpLng = helpList.get(i).getLng();
                HelpAddress = helpList.get(i).getAddress();
                HelpState = helpList.get(i).getState();
                HelpUserName = helpList.get(i).getUser_name();
                HelpDetail = helpList.get(i).getDetail();
                HelpHelpers = helpList.get(i).getHelpers();

                textView_helpDetailName.setText(helpList.get(i).getUser_name());
                textView_helpDetailState.setText(helpList.get(i).getState());
                textView_helpDetailDate.setText(helpList.get(i).getUser_token());
                textView_helpDetailDetails.setText(helpList.get(i).getDetail());
                textView_helpDetailAddress.setText(helpList.get(i).getAddress());

                globalData.setHelpLat(Double.parseDouble(helpList.get(i).getLat()));
                globalData.setHelpLng(Double.parseDouble(helpList.get(i).getLng()));
                globalData.setHelpAdres(helpList.get(i).getAddress());
                globalData.setHelpUserName(helpList.get(i).getUser_name());

                dismisProgressDialog();
            }
        }
    }

    @Override
    public void onContactsAddQuery(List<Contact> contactList) {

    }

    @Override
    public void isInsert(boolean inserted) {
        if(!inserted){
            showAlert("An Error Occurred");
        }else{
            if (CDBOperation.equals("Accept")) {
                showAlert("You Accepted This Help.");
            } else if (CDBOperation.equals("Close")) {
                showAlert("Closed Help Request.");
            } else if (CDBOperation.equals("Update")) {
                showAlert("Location Updated.");
            }
        }
    }
}