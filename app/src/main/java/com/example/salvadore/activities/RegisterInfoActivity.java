package com.example.salvadore.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.salvadore.R;
import com.example.salvadore.clouddb.CloudDBZoneWrapper;
import com.example.salvadore.clouddb.Contact;
import com.example.salvadore.clouddb.Help;
import com.example.salvadore.clouddb.TableUser;
import com.example.salvadore.clouddb.UserEditFields;
import com.example.salvadore.utils.Constants;
import com.example.salvadore.utils.GlobalData;
import com.example.salvadore.utils.MyPushService;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.analytics.HiAnalyticsTools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterInfoActivity extends BaseActivityV2 implements View.OnClickListener, CloudDBZoneWrapper.UiCallBack {

    @BindView(R.id.btn_createAccount)
    Button btn_createAccount;

    @BindView(R.id.editText_registerNameLastName)
    EditText editText_registerNameLastName;
    @BindView(R.id.editText_registerMail)
    EditText editText_registerMail;
    @BindView(R.id.editText_registerPhone)
    EditText editText_registerPhone;
    @BindView(R.id.editText_registerAge)
    EditText editText_registerAge;

    @BindView(R.id.spinner_registerGender)
    Spinner spinner_registerGender;

    private MyHandler mHandler = new MyHandler();
    private CloudDBZoneWrapper mCloudDBZoneWrapper;
    private boolean mInSearchMode = false;

    int turnedLastUserID = 0;

    String selectedGender = "";
    String AGC_UID = "";
    String PushToken = "";
    String UserToken = "";
    String ageNew = "";

    GlobalData globalData = GlobalData.getInstance();

    HiAnalyticsInstance instance;

    public RegisterInfoActivity() {
        mCloudDBZoneWrapper = new CloudDBZoneWrapper();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_info);

        ButterKnife.bind(this);

        btn_createAccount.setOnClickListener(this);

        UserToken = globalData.getUserToken();
        Log.w(Constants.REGISTER_INFO_TAG, "USER TOKEN : " + UserToken);
        if (UserToken == null) {
            getToken();
        }

        setSpinner();

        AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
        AGC_UID = user.getUid();
        Log.w(Constants.REGISTER_INFO_TAG, "REGISTER INFO : AGConnectUser UID : " + AGC_UID);

        HiAnalyticsTools.enableLog();
        instance = HiAnalytics.getInstance(this);
        instance.setAnalyticsEnabled(true);
        instance.regHmsSvcEvent();

        CloudDBZoneWrapper.initAGConnectCloudDB(this);

        mHandler.post(() -> {
            mCloudDBZoneWrapper.addCallBacks(RegisterInfoActivity.this);
            mCloudDBZoneWrapper.createObjectType();
            mCloudDBZoneWrapper.openCloudDBZone();
        });

        mHandler.post(() -> {
            mCloudDBZoneWrapper.getLastUserID();
            Log.w(Constants.REGISTER_INFO_TAG, "GoGetUserID: ");
        });
    }

    private void getToken() {
        Log.i(Constants.MAIN_TAG, "get token: begin");
        new Thread() {
            @Override
            public void run() {
                try {
                    String appId = AGConnectServicesConfig.fromContext(RegisterInfoActivity.this).getString("client/app_id");
                    PushToken = HmsInstanceId.getInstance(RegisterInfoActivity.this).getToken(appId, "HCM");
                    if(!TextUtils.isEmpty(PushToken)) {
                        Log.i(Constants.MAIN_TAG, "get token:" + PushToken);
                        UserToken = PushToken;
                        Log.i(Constants.MAIN_TAG, "get token 2:" + UserToken);
                    }
                } catch (Exception e) {
                    Log.i(Constants.MAIN_TAG,"getToken failed, " + e);

                }
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_createAccount:
                if(editText_registerNameLastName.getText().toString().trim().equals("") || editText_registerMail.getText().toString().trim().equals("")){
                    Log.i(Constants.REGISTER_INFO_TAG, "Resitration Error");
                    AlertDialog alertDialog = new AlertDialog.Builder(RegisterInfoActivity.this).create();
                    alertDialog.setTitle("Registration Error");
                    alertDialog.setMessage("Please fill name and mail area.");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //nothing
                                }
                            });
                    alertDialog.show();
                }else{
                    showProgressDialog();
                    Log.i(Constants.REGISTER_INFO_TAG, "GO MAIN ACTIVITY ");
                    sendAnalysis();
                    Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                    intent2.putExtra(UserEditFields.EDIT_MODE, UserEditFields.EditMode.ADD.name());
                    createNewUser(intent2);
                    startActivityForResult(intent2, 2);
                    Log.i(Constants.REGISTER_INFO_TAG, "CLICK CREATE ACCOUNT");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        createNewUser(data);
        if (resultCode == Activity.RESULT_CANCELED) {
            Log.i(Constants.REGISTER_INFO_TAG, "01");
            createNewUser(data);
            return;
        }
        createNewUser(data);
        if (requestCode == 2) {
            Log.i(Constants.REGISTER_INFO_TAG, "02");
            createNewUser(data);
        } else {
            Log.i(Constants.REGISTER_INFO_TAG, "03");
            createNewUser(data);
        }
    }

    private static final class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            // dummy
        }
    }

    public void setSpinner(){
        String[] genders = new String[]{
                "Select",
                "Male",
                "Female",
                "Other"
        };
        final List<String> genderList = new ArrayList<>(Arrays.asList(genders));
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item,genderList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0) { return false; }
                else{ return true; }
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){ tv.setTextColor(Color.GRAY); }
                else { tv.setTextColor(Color.BLACK); }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner_registerGender.setAdapter(spinnerArrayAdapter);

        spinner_registerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGender = (String) parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void createNewUser(Intent data){

        if (editText_registerAge.getText().toString().equals("")) {
            ageNew = "Enter";
        }else{
            ageNew = editText_registerAge.getText().toString();
        }
        TableUser user = new TableUser();
        user.setUser_id(turnedLastUserID + 1);
        user.setUser_token(UserToken);
        user.setUser_name(editText_registerNameLastName.getText().toString());
        user.setUser_mail(editText_registerMail.getText().toString());
        user.setUser_phone("Enter");
        user.setUser_age(ageNew);
        user.setUser_gender(selectedGender);
        user.setUser_country("Enter");
        user.setUser_city("Enter");
        user.setAuth_id(AGC_UID);
        mHandler.post(() -> {
            mCloudDBZoneWrapper.insertUser(user);
        });
        dismisProgressDialog();
    }

    public void sendAnalysis(){
        Bundle bundle = new Bundle();
        bundle.putString("registerState", "register");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        bundle.putString("registerTime",sdf.format(new Date()));

        instance.onEvent("REGISTER", bundle);
        Log.i(Constants.LOGIN_TAG, "LOGIN sendAnaysis");
    }

    @Override
    public void onAddOrQuery(List<TableUser> userList) {
        Log.i(Constants.REGISTER_INFO_TAG, "QUERY USER FUC");
    }

    @Override
    public void onSubscribe(List<TableUser> userList) {
        Log.i(Constants.REGISTER_INFO_TAG, "SUBSCRIBE USER FUC");
    }

    @Override
    public void onDelete(List<TableUser> userList) {
    }

    @Override
    public void updateUiOnError(String errorMessage) {
        // dummy
    }

    @Override
    public void isLoginSuccess(boolean state) {

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

    @Override
    public void onDestroy() {
        mHandler.post(() -> mCloudDBZoneWrapper.closeCloudDBZone());
        Log.i(Constants.REGISTER_INFO_TAG, "DESTORY USER FUC");
        super.onDestroy();
    }
}
