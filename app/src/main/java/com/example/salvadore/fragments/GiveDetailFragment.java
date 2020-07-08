package com.example.salvadore.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.salvadore.R;
import com.example.salvadore.activities.MainActivity;
import com.example.salvadore.clouddb.CloudDBZoneWrapper;
import com.example.salvadore.clouddb.Contact;
import com.example.salvadore.clouddb.Help;
import com.example.salvadore.clouddb.TableUser;
import com.example.salvadore.models.NotificationMessage;
import com.example.salvadore.rest.ApiInterface;
import com.example.salvadore.rest.NetworkEngine;
import com.example.salvadore.rest.base.DBResponse;
import com.example.salvadore.rest.response.EmptyResponse;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GiveDetailFragment extends BaseFragmentV2 implements View.OnClickListener, CloudDBZoneWrapper.UiCallBack {

    View rootView;

    @BindView(R.id.btn_submit)
    Button btn_submit;

    @BindView(R.id.multiAutoCompleteTextView)
    MultiAutoCompleteTextView multiAutoCompleteTextView;

    double giveDetailLat, giveDetailLng;
    String giveDetailUserAddress = "", giveDetailUserToken = "", giveDetailUserName = "", currentUID = "", firstAccessToken = "";
    int giveDetailLastHelpID = 0, giveDetailUserID = 0;

    ArrayList<String> AllTokenList = new ArrayList<String>();

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;

    private MyHandler mHandler = new MyHandler();
    private CloudDBZoneWrapper mCloudDBZoneWrapper;

    GlobalData globalData = GlobalData.getInstance();

    private static final class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            // dummy
        }
    }

    public GiveDetailFragment() {
        mCloudDBZoneWrapper = new CloudDBZoneWrapper();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_give_detail, container, false);
        ButterKnife.bind(this,rootView);

        btn_submit.setOnClickListener(this);

        AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
        currentUID = user.getUid();
        Log.w(Constants.GIVE_DETAIL_TAG, "GIVE DETAIL : AGConnectUser UID : " + currentUID);

        firstAccessToken = globalData.getCreatedAccessToken();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(GiveDetailFragment.this.getActivity());
        getLastLocationAndAddress();

        mHandler.post(() -> {
            mCloudDBZoneWrapper.addCallBacks(GiveDetailFragment.this);
            mCloudDBZoneWrapper.createObjectType();
            mCloudDBZoneWrapper.openCloudDBZone();
            mCloudDBZoneWrapper.getUserDetail();
        });

        mHandler.post(() -> {
            mCloudDBZoneWrapper.getLastHelpID();
            Log.w(Constants.GIVE_DETAIL_TAG, "GoGetHelpID: ");
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
                        Log.i(Constants.GIVE_DETAIL_TAG, "getLastLocation onSuccess location is null");
                        return;
                    }
                    Log.i(Constants.GIVE_DETAIL_TAG,
                            "getLastLocation onSuccess location[Longitude,Latitude]:" + location.getLongitude() + ","
                                    + location.getLatitude());
                    giveDetailLat = location.getLatitude();
                    giveDetailLng = location.getLongitude();
                    Geocoder geo = new Geocoder(GiveDetailFragment.this.getContext(), Locale.getDefault());
                    try {
                        List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        giveDetailUserAddress = addresses.get(0).getAddressLine(0);
                        String cityName = addresses.get(0).getAdminArea();
                        Log.e(Constants.GIVE_DETAIL_TAG, "CITY : " + cityName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Log.e(Constants.GIVE_DETAIL_TAG, "getLastLocation onFailure:" + e.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(Constants.GIVE_DETAIL_TAG, "getLastLocation exception:" + e.getMessage());
        }
    }

    public void createNewHelp(){
        Help help = new Help();
        help.setId(giveDetailLastHelpID +1);
        help.setUser_id(giveDetailUserID);
        help.setUser_token(getCurrenDateTime());
        help.setLat(Double.toString(giveDetailLat));
        help.setLng(Double.toString(giveDetailLng));
        help.setAddress(giveDetailUserAddress);
        help.setState("Created");
        help.setUser_name(giveDetailUserName);
        help.setDetail(multiAutoCompleteTextView.getText().toString());
        help.setHelpers("None");
        mHandler.post(() -> {
            mCloudDBZoneWrapper.insertHelp(help);
        });

        for(int i = 0; i <= AllTokenList.size()-1; i++){
            sendNotification(AllTokenList.get(i));
        }
        showAlert();
    }

    public void sendNotification(String token){
        ApiInterface apiInterface = NetworkEngine.getInstance().getSalvadoreApi(getActivity());

        NotificationMessage notificationMessage = new NotificationMessage.Builder(
                "New Help Request!", giveDetailUserName + " Need Help! Go! ", token)
                .build();

        Call<DBResponse<EmptyResponse>> call = apiInterface.createNotification(
                "Bearer " + firstAccessToken,
                notificationMessage
        );
        call.enqueue(new Callback<DBResponse<EmptyResponse>>() {
            @Override
            public void onResponse(Call<DBResponse<EmptyResponse>> call, Response<DBResponse<EmptyResponse>> response) {
                DBResponse dbResponse = null;
            }

            @Override
            public void onFailure(Call<DBResponse<EmptyResponse>> call, Throwable t) {
                Log.w(Constants.HOME_TAG, "HATA" + notificationMessage.getMessage().getNotification().getTitle());
                Log.w(Constants.HOME_TAG, "HATA " +t.getLocalizedMessage());
                Log.w(Constants.HOME_TAG, "HATA" + t.getMessage());
            }
        });

    }

    public void showAlert(){
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Salvadore");
        alertDialog.setMessage("Your help request created.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity mainActivity = (MainActivity) getActivity();
                        mainActivity.setSelectedTab(Constants.TAB_GIVE_DETAIL, Constants.TAB_HOME);
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                createNewHelp();
                break;
            default:
                break;
        }
    }

    @Override
    public void onAddOrQuery(List<TableUser> userList) {
        for(int i = 0; i <= userList.size()-1; i++){
            if(userList.get(i).getAuth_id().equals(currentUID)){
                AllTokenList.add(userList.get(i).getUser_token());
                giveDetailUserID = userList.get(i).getUser_id();
                giveDetailUserToken = userList.get(i).getUser_token();
                giveDetailUserName = userList.get(i).getUser_name();
            }
        }
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
        giveDetailLastHelpID = lastUserID;
    }

    @Override
    public void onHelpAddQuery(List<Help> helpList) {

    }

    @Override
    public void onContactsAddQuery(List<Contact> contactList) {

    }

    @Override
    public void isInsert(boolean inserted) {

    }
}