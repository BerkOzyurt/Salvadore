package com.example.salvadore.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.salvadore.clouddb.CloudDBZoneWrapper;
import com.example.salvadore.clouddb.Contact;
import com.example.salvadore.clouddb.Help;
import com.example.salvadore.clouddb.TableUser;
import com.example.salvadore.utils.Constants;
import com.example.salvadore.R;
import com.example.salvadore.utils.GlobalData;
import com.huawei.agconnect.apms.APMS;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.auth.HwIdAuthProvider;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.analytics.HiAnalyticsTools;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.api.entity.auth.Scope;
import com.huawei.hms.support.api.entity.hwid.HwIDConstant;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivityV2 implements View.OnClickListener, CloudDBZoneWrapper.UiCallBack{

    private HuaweiIdAuthService mAuthManager;
    private HuaweiIdAuthParams mAuthParam;

    @BindView(R.id.layout_signInWithHuawei)
    LinearLayout layout_signInWithHuawei;

    HiAnalyticsInstance instance;

    private static final int REQUEST_CODE_SIGNIN_HWID = 8888;

    private List<OnLoginEventCallBack> mLoginCallbacks = new ArrayList<>();

    private HuaweiIdAuthService mClient;

    private MyHandler mHandler = new MyHandler();
    private CloudDBZoneWrapper mCloudDBZoneWrapper;

    String goActivity = "";
    String AGC_UID = "";
    String pushtoken = "";
    String UserToken = "";
    String DisplayName = "";

    int turnedLastUserID = 0;

    GlobalData globalData = GlobalData.getInstance();

    public LoginActivity() {
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
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        APMS.getInstance().enableCollection(true);

        UserToken = globalData.getUserToken();
        Log.w(Constants.LOGIN_TAG, "USER TOKEN : " + UserToken);
        if (UserToken == null) {
            getToken();
        }

        layout_signInWithHuawei.setOnClickListener(this);

        HiAnalyticsTools.enableLog();
        instance = HiAnalytics.getInstance(this);
        instance.setAnalyticsEnabled(true);
        instance.regHmsSvcEvent();

        CloudDBZoneWrapper.initAGConnectCloudDB(this);

        mHandler.post(() -> {
            mCloudDBZoneWrapper.addCallBacks(LoginActivity.this);
            mCloudDBZoneWrapper.createObjectType();
            mCloudDBZoneWrapper.openCloudDBZone();
        });

        mHandler.post(() -> {
            mCloudDBZoneWrapper.getLastUserID();
            Log.w(Constants.REGISTER_INFO_TAG, "GoGetUserID: ");
        });
    }

    private void getToken() {
        Log.i(Constants.LOGIN_TAG, "get token: begin");
        new Thread() {
            @Override
            public void run() {
                try {
                    String appId = AGConnectServicesConfig.fromContext(LoginActivity.this).getString("client/app_id");
                    pushtoken = HmsInstanceId.getInstance(LoginActivity.this).getToken(appId, "HCM");
                    if(!TextUtils.isEmpty(pushtoken)) {
                        Log.i(Constants.LOGIN_TAG, "get token:" + pushtoken);
                        UserToken = pushtoken;
                    }
                } catch (Exception e) {
                    Log.i(Constants.MAIN_TAG,"getToken failed, " + e);

                }
            }
        }.start();
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
        Log.w(Constants.LOGIN_TAG, "onActivityResult: " + requestCode + " resultCode = " + resultCode);
        if (requestCode == REQUEST_CODE_SIGNIN_HWID ) {
            Task<AuthHuaweiId> signInHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data);
            if (signInHuaweiIdTask.isSuccessful()){
                AuthHuaweiId huaweiAccount = signInHuaweiIdTask.getResult();
                String accessToken = huaweiAccount.getAccessToken();
                Log.w(Constants.LOGIN_TAG, "accessToken: " + accessToken);
                globalData.setCreatedAccessToken(accessToken);
                AGConnectAuthCredential credential = HwIdAuthProvider.credentialWithToken(accessToken);
                int provider_now = credential.getProvider();
                Log.w(Constants.LOGIN_TAG, "provider_now: " + provider_now);
                AGConnectAuth.getInstance().signIn(credential).addOnSuccessListener(new OnSuccessListener<SignInResult>() {
                    @Override
                    public void onSuccess(SignInResult signInResult) {
                        AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
                        Log.w(Constants.LOGIN_TAG, "AGConnectUser : " + user);
                        Log.w(Constants.LOGIN_TAG, "AGConnectUser UID : " + user.getUid());
                        Log.w(Constants.LOGIN_TAG, "AGConnectUser DisplayName : " + user.getDisplayName());
                        AGC_UID = user.getUid();
                        DisplayName = user.getDisplayName();
                        Log.w(Constants.LOGIN_TAG, "AGConnectUser UID 2 : " + AGC_UID);
                        mHandler.post(() -> {
                            mCloudDBZoneWrapper.loginDB(user.getUid());
                            Log.w(Constants.LOGIN_TAG, "GoLoginDB: ");
                        });
                        for (OnLoginEventCallBack loginEventCallBack : mLoginCallbacks) {
                            loginEventCallBack.onLogin(true, signInResult);
                        }
                    }
                }).addOnFailureListener(e -> {
                    dismisProgressDialog();
                    Toast.makeText(getApplicationContext(),"Sign In Failture",Toast.LENGTH_SHORT).show();
                    Log.w(Constants.LOGIN_TAG, "sign in for agc failed: " + e.getMessage());
                    for (OnLoginEventCallBack loginEventCallBack : mLoginCallbacks) {
                        loginEventCallBack.onLogOut(false); }
                });
            }
            else {
                dismisProgressDialog();
                Toast.makeText(getApplicationContext(),"Sign In Failed",Toast.LENGTH_SHORT).show();
                Log.e(Constants.LOGIN_TAG, "sign in failed : " + ((ApiException)signInHuaweiIdTask.getException()).getStatusCode());
            }
        }
    }

    public void addLoginCallBack(OnLoginEventCallBack loginEventCallBack) {
        if (!mLoginCallbacks.contains(loginEventCallBack)) {
            mLoginCallbacks.add(loginEventCallBack);
        }
    }

    public void createNewUser(){
        if(UserToken.equals("") || UserToken == null){
            UserToken = "Waiting";
        }
        Log.w(Constants.LOGIN_TAG, "createNewUser: ");
        TableUser user = new TableUser();
        user.setUser_id(turnedLastUserID + 1);
        user.setUser_token(UserToken);
        user.setUser_name(DisplayName);
        user.setUser_mail("Enter");
        user.setUser_phone("Enter");
        user.setUser_age("Enter");
        user.setUser_gender("Select");
        user.setUser_country("Enter");
        user.setUser_city("Enter");
        user.setAuth_id(AGC_UID);
        mHandler.post(() -> {
            mCloudDBZoneWrapper.insertUser(user);
        });
        dismisProgressDialog();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
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
        if(goActivity.equals("MAIN")){
            if(state){
                Log.w(Constants.LOGIN_TAG, "isLoginSuccess: ");
                sendAnalysis();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }else{
                //REGISTER ON HERE
                createNewUser();
            }
        }else if(goActivity.equals("REGISTER")){

            Log.w(Constants.LOGIN_TAG, "isLoginSuccess: " + AGC_UID);
            Intent intent = new Intent(getApplicationContext(), RegisterInfoActivity.class);
            intent.putExtra("key",AGC_UID);
            startActivity(intent);
        }
    }

    @Override
    public void isLastID(int lastUserID) {
        turnedLastUserID = lastUserID;
        Log.w(Constants.REGISTER_INFO_TAG, "Turned UserID: " + turnedLastUserID);
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

    public void sendAnalysis(){
        Bundle bundle = new Bundle();
        bundle.putString("loginState", "login");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        bundle.putString("loginTime",sdf.format(new Date()));

        instance.onEvent("LOGIN", bundle);
        Log.i(Constants.LOGIN_TAG, "LOGIN sendAnaysis");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_signInWithHuawei:
                showProgressDialog();
                goActivity = "MAIN";
                Log.i(Constants.LOGIN_TAG, "HUAWEI LOGIN ");
                login();
                break;
            default:
                break;
        }
    }
}