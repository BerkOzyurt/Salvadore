package com.example.salvadore.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.salvadore.R;
import com.example.salvadore.clouddb.CloudDBZoneWrapper;
import com.example.salvadore.clouddb.Contact;
import com.example.salvadore.clouddb.Help;
import com.example.salvadore.clouddb.TableUser;
import com.example.salvadore.clouddb.UserEditFields;
import com.example.salvadore.fragments.HomeFragment;
import com.example.salvadore.utils.Constants;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.auth.HwIdAuthProvider;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.analytics.HiAnalyticsTools;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.support.api.entity.auth.Scope;
import com.huawei.hms.support.api.entity.hwid.HwIDConstant;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends BaseActivityV2 implements View.OnClickListener, CloudDBZoneWrapper.UiCallBack {

    @BindView(R.id.textView_login)
    TextView textView_login;
    @BindView(R.id.layout_registerWithHuawei)
    LinearLayout layout_registerWithHuawei;

    HiAnalyticsInstance instance;

    private static final int REQUEST_CODE_SIGNIN_HWID = 8888;
    private List<OnLoginEventCallBack> mLoginCallbacks = new ArrayList<>();
    private HuaweiIdAuthService mClient;
    private MyHandler mHandler = new MyHandler();
    private CloudDBZoneWrapper mCloudDBZoneWrapper;

    String AGC_UID = "";

    public RegisterActivity() {
        mCloudDBZoneWrapper = new CloudDBZoneWrapper();
    }

    private static final class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            // dummy
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_login);

        ButterKnife.bind(this);

        textView_login.setOnClickListener(this);
        layout_registerWithHuawei.setOnClickListener(this);

        HiAnalyticsTools.enableLog();
        instance = HiAnalytics.getInstance(this);
        instance.setAnalyticsEnabled(true);
        instance.regHmsSvcEvent();

        CloudDBZoneWrapper.initAGConnectCloudDB(this);

        mHandler.post(() -> {
            mCloudDBZoneWrapper.addCallBacks(RegisterActivity.this);
            mCloudDBZoneWrapper.createObjectType();
            mCloudDBZoneWrapper.openCloudDBZone();
        });
    }

    public void login() {
        logOut();
        HuaweiIdAuthParamsHelper huaweiIdAuthParamsHelper = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM);
        List<Scope> scopeList = new ArrayList<>();
        scopeList.add(new Scope(HwIDConstant.SCOPE.ACCOUNT_BASEPROFILE));
        huaweiIdAuthParamsHelper.setScopeList(scopeList);
        HuaweiIdAuthParams authParams = huaweiIdAuthParamsHelper.setAccessToken().createParams();

        mClient = HuaweiIdAuthManager.getService(this,authParams);
        this.startActivityForResult(mClient.getSignInIntent(), REQUEST_CODE_SIGNIN_HWID);
    }

    public void logOut() {
        AGConnectAuth auth = AGConnectAuth.getInstance();
        auth.signOut();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.w(Constants.REGISTER_TAG, "onActivityResult: " + requestCode + " resultCode = " + resultCode);
        if (requestCode == REQUEST_CODE_SIGNIN_HWID ) {
            Task<AuthHuaweiId> signInHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data);
            if (signInHuaweiIdTask.isSuccessful()){
                AuthHuaweiId huaweiAccount = signInHuaweiIdTask.getResult();
                String accessToken = huaweiAccount.getAccessToken();
                Log.w(Constants.REGISTER_TAG, "accessToken: " + accessToken);
                AGConnectAuthCredential credential = HwIdAuthProvider.credentialWithToken(accessToken);
                int provider_now = credential.getProvider();
                Log.w(Constants.REGISTER_TAG, "provider_now: " + provider_now);

                AGConnectAuth.getInstance().signIn(credential).addOnSuccessListener(new OnSuccessListener<SignInResult>() {
                    @Override
                    public void onSuccess(SignInResult signInResult) {
                        AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
                        Log.w(Constants.REGISTER_TAG, "AGConnectUser : " + user);
                        Log.w(Constants.REGISTER_TAG, "AGConnectUser UID : " + user.getUid());
                        AGC_UID = user.getUid();
                        Log.w(Constants.REGISTER_TAG, "AGConnectUser UID 2 : " + AGC_UID);
                        mHandler.post(() -> {
                            mCloudDBZoneWrapper.loginDB(huaweiAccount.getEmail());
                            Log.w(Constants.REGISTER_TAG, "GoLoginDB: ");
                        });
                        for (OnLoginEventCallBack loginEventCallBack : mLoginCallbacks) {
                            loginEventCallBack.onLogin(true, signInResult);
                        }
                    }
                }).addOnFailureListener(e -> {
                    dismisProgressDialog();
                    Toast.makeText(getApplicationContext(),"Sign In Failture",Toast.LENGTH_SHORT).show();
                    Log.w(Constants.REGISTER_TAG, "sign in for agc failed: " + e.getMessage());
                    for (OnLoginEventCallBack loginEventCallBack : mLoginCallbacks) {
                        loginEventCallBack.onLogOut(false); }
                });
            }
            else {
                dismisProgressDialog();
                Toast.makeText(getApplicationContext(),"Sign In Fail",Toast.LENGTH_SHORT).show();
                Log.e(Constants.REGISTER_TAG, "sign in failed : " + ((ApiException)signInHuaweiIdTask.getException()).getStatusCode());
            }
        }
    }

    @Override
    public void onAddOrQuery(List<TableUser> userList) {
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
        dismisProgressDialog();
        if(!state){
            Log.w(Constants.REGISTER_TAG, "isLoginSuccess: " + AGC_UID);
            Intent intent = new Intent(getApplicationContext(), RegisterInfoActivity.class);
            intent.putExtra("key",AGC_UID);
            startActivity(intent);
        }else{
            Log.w(Constants.REGISTER_TAG, "notLoginSuccess: ");
            AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
            alertDialog.setTitle("Login Error");
            alertDialog.setMessage("You have already an account. Please login.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Login",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);;
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //nothing
                        }
                    });
            alertDialog.show();
        }
    }

    @Override
    public void isLastID(int lastUserID) {

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

    public interface OnLoginEventCallBack {
        void onLogin(boolean showLoginUserInfo, SignInResult signInResult);
        void onLogOut(boolean showLoginUserInfo);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance.unRegHmsSvcEvent();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView_login:
                Log.i(Constants.REGISTER_TAG, "GO REGISTER ACTIVITY");
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.layout_registerWithHuawei:
                showProgressDialog();
                Log.i(Constants.REGISTER_TAG, "HUAWEI LOGIN ");
                login();
                break;
            default:
                break;
        }
    }
}
