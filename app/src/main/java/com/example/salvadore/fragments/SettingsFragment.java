package com.example.salvadore.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.salvadore.R;
import com.example.salvadore.activities.LoginActivity;
import com.example.salvadore.activities.MainActivity;
import com.example.salvadore.utils.Constants;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.analytics.HiAnalyticsTools;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.linearLayout_myAccount)
    LinearLayout linearLayout_myAccount;
    @BindView(R.id.linearLayout_aboutApp)
    LinearLayout linearLayout_aboutApp;
    @BindView(R.id.linearLayout_privacyPolicy)
    LinearLayout linearLayout_privacyPolicy;
    @BindView(R.id.linearLayout_howToUse)
    LinearLayout linearLayout_howToUse;
    @BindView(R.id.linearLayout_checkWifi)
    LinearLayout linearLayout_checkWifi;
    @BindView(R.id.linearLayout_logOut)
    LinearLayout linearLayout_logOut;

    HiAnalyticsInstance instance;

    View rootView;

    private HuaweiIdAuthService mAuthManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, rootView);

        HiAnalyticsTools.enableLog();
        instance = HiAnalytics.getInstance(getActivity());
        instance.setAnalyticsEnabled(true);
        instance.regHmsSvcEvent();

        linearLayout_myAccount.setOnClickListener(this);
        linearLayout_aboutApp.setOnClickListener(this);
        linearLayout_privacyPolicy.setOnClickListener(this);
        linearLayout_howToUse.setOnClickListener(this);
        linearLayout_checkWifi.setOnClickListener(this);
        linearLayout_logOut.setOnClickListener(this);

        return rootView;
    }

    public void signOut() {
        Task<Void> signOutTask = mAuthManager.signOut();
        signOutTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(Constants.SETTINGS_TAG, "signOut Success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.i(Constants.SETTINGS_TAG, "signOut fail");
            }
        });
    }

    public void showLogOutDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Log Out");
        builder.setMessage("Are you sure to sign out?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendAnalysis();
                Intent intent = new Intent(getContext(),LoginActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void sendAnalysis(){
        Bundle bundle = new Bundle();
        bundle.putString("logoutState", "logout");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        bundle.putString("logoutTime",sdf.format(new Date()));

        instance.onEvent("LOGOUT", bundle);
        Log.i(Constants.LOGIN_TAG, "LOGOUT sendAnaysis");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearLayout_myAccount:
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.setSelectedTab(Constants.TAB_SETINGS, Constants.TAB_PROFILE);
                break;
            case R.id.linearLayout_logOut:
                showLogOutDialog();
                break;
            case R.id.linearLayout_privacyPolicy:
                MainActivity mainActivity2 = (MainActivity) getActivity();
                mainActivity2.setSelectedTab(Constants.TAB_SETINGS, Constants.TAB_PRIVACY_POLICY);
                break;
            case R.id.linearLayout_aboutApp:
                MainActivity mainActivity3 = (MainActivity) getActivity();
                mainActivity3.setSelectedTab(Constants.TAB_SETINGS, Constants.TAB_ABOUT_APP);
                break;
            case R.id.linearLayout_howToUse:
                MainActivity mainActivity4 = (MainActivity) getActivity();
                mainActivity4.setSelectedTab(Constants.TAB_SETINGS, Constants.TAB_HOW_TO_USE);
                break;
            case R.id.linearLayout_checkWifi:
                MainActivity mainActivity5 = (MainActivity) getActivity();
                mainActivity5.setSelectedTab(Constants.TAB_SETINGS, Constants.TAB_WIFI_STATUS);
                break;
            default:
                break;
        }
    }
}
