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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiAvailability;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.support.api.entity.core.CommonCode;
import com.huawei.hms.support.api.entity.safetydetect.MaliciousAppsData;
import com.huawei.hms.support.api.entity.safetydetect.MaliciousAppsListResp;
import com.huawei.hms.support.api.entity.safetydetect.UserDetectResponse;
import com.huawei.hms.support.api.entity.safetydetect.WifiDetectResponse;
import com.huawei.hms.support.api.safetydetect.SafetyDetect;
import com.huawei.hms.support.api.safetydetect.SafetyDetectClient;
import com.huawei.hms.support.api.safetydetect.SafetyDetectStatusCodes;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WifiStatusFragment extends BaseFragmentV2 implements View.OnClickListener {

    View rootView;

    @BindView(R.id.btn_getWifiResult)
    Button btn_getWifiResult;

    @BindView(R.id.textView_wifiResult)
    TextView textView_wifiResult;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_wifi_detect, container, false);
        ButterKnife.bind(this,rootView);

        btn_getWifiResult.setOnClickListener(this);

        if (HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(getContext()) == ConnectionResult.SUCCESS){
            Log.e(Constants.WIFI_STATUS_TAG, "BAŞARILI");
        } else {
            Log.e(Constants.WIFI_STATUS_TAG, "HATA");
        }

        return rootView;
    }

    private void getWifiDetectStatus() {
        Log.i(Constants.WIFI_STATUS_TAG, "Start to getWifiDetectStatus!");
        SafetyDetectClient wifidetectClient = SafetyDetect.getClient(getActivity());
        Task task = wifidetectClient.getWifiDetectStatus();
        task.addOnSuccessListener(new OnSuccessListener<WifiDetectResponse>() {
            @Override
            public void onSuccess(WifiDetectResponse wifiDetectResponse) {
                int wifiDetectStatus = wifiDetectResponse.getWifiDetectStatus();
                Log.i(Constants.WIFI_STATUS_TAG, "\n-1 ：获取WiFi状态失败\n" + "0 ：未连接WIFI\n" + "1 ：当前连接的WIFI安全\n" + "2 ：当前连接的WIFI不安全.");
                Log.i(Constants.WIFI_STATUS_TAG, "wifiDetectStatus is: " + wifiDetectStatus);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    Log.e(Constants.WIFI_STATUS_TAG,
                            "Error: " + apiException.getStatusCode() + ":"
                                    + SafetyDetectStatusCodes.getStatusCodeString(apiException.getStatusCode()) + ": "
                                    + apiException.getStatusMessage());
                } else {
                    Log.e(Constants.WIFI_STATUS_TAG, "ERROR! " + e.getMessage());
                }
            }
        });
    }

    public void getAppStatus(){
        SafetyDetectClient appsCheckClient = SafetyDetect.getClient(getActivity());
        Task task = appsCheckClient.getMaliciousAppsList();
        task.addOnSuccessListener(new OnSuccessListener<MaliciousAppsListResp>() {
            @Override
            public void onSuccess(MaliciousAppsListResp maliciousAppsListResp) {
                List<MaliciousAppsData> appsDataList = maliciousAppsListResp.getMaliciousAppsList();
                if(maliciousAppsListResp.getRtnCode() == CommonCode.OK) {
                    if (appsDataList.isEmpty()) {
                        Log.i(Constants.WIFI_STATUS_TAG, "There are no known potentially malicious apps installed.");
                        textView_wifiResult.setText("This App İs Safe");
                    } else {
                        Log.i(Constants.WIFI_STATUS_TAG, "Potentially malicious apps are installed!");
                        for (MaliciousAppsData maliciousApp : appsDataList) {
                            Log.i(Constants.WIFI_STATUS_TAG, "Information about a malicious app:");
                            Log.i(Constants.WIFI_STATUS_TAG, "APK: " + maliciousApp.getApkPackageName());
                            Log.i(Constants.WIFI_STATUS_TAG, "SHA-256: " + maliciousApp.getApkSha256());
                            Log.i(Constants.WIFI_STATUS_TAG, "Category: " + maliciousApp.getApkCategory());
                        }
                    }
                }else{
                    Log.e(Constants.WIFI_STATUS_TAG,"getMaliciousAppsList fialed: "+maliciousAppsListResp.getErrorReason());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    Log.e(Constants.WIFI_STATUS_TAG, "Error: " +  SafetyDetectStatusCodes.getStatusCodeString(apiException.getStatusCode()) + ": " + apiException.getStatusMessage());
                } else {
                    Log.e(Constants.WIFI_STATUS_TAG, "ERROR: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_getWifiResult:
                getAppStatus();
                break;
            default:
                break;
        }
    }
}
