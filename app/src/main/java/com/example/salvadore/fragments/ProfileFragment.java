package com.example.salvadore.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.salvadore.R;
import com.example.salvadore.activities.RegisterActivity;
import com.example.salvadore.clouddb.CloudDBZoneWrapper;
import com.example.salvadore.clouddb.Contact;
import com.example.salvadore.clouddb.Help;
import com.example.salvadore.clouddb.TableUser;
import com.example.salvadore.utils.Constants;
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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends BaseFragmentV2 implements View.OnClickListener, CloudDBZoneWrapper.UiCallBack {

    @BindView(R.id.btn_editProfile)
    Button btn_editProfile;
    @BindView(R.id.btn_saveChanges)
    Button btn_saveChanges;

    @BindView(R.id.textView_centerName)
    TextView textView_centerName;

    @BindView(R.id.textView_mail)
    TextView textView_mail;
    @BindView(R.id.textView_phone)
    TextView textView_phone;
    @BindView(R.id.textView_age)
    TextView textView_age;
    @BindView(R.id.textView_gender)
    TextView textView_gender;
    @BindView(R.id.textView_country)
    TextView textView_country;
    @BindView(R.id.textView_city)
    TextView textView_city;

    @BindView(R.id.relativeLayout_profileGenderArea)
    RelativeLayout relativeLayout_profileGenderArea;
    @BindView(R.id.spinner_profileGender)
    Spinner spinner_profileGender;

    @BindView(R.id.editText_profileMail)
    TextView editText_profileMail;
    @BindView(R.id.editText_profilePhone)
    TextView editText_profilePhone;
    @BindView(R.id.editText_profileAge)
    TextView editText_profileAge;

    View rootView;
    String UserToken = "", UserGender = "", UserUID = "", currentUID = "";
    int UserID = 0;
    String selectedGender = "";

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;

    private MyHandler mHandler = new MyHandler();
    private CloudDBZoneWrapper mCloudDBZoneWrapper;

    private static final class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            // dummy
        }
    }

    public ProfileFragment() {
        mCloudDBZoneWrapper = new CloudDBZoneWrapper();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, rootView);

        btn_editProfile.setOnClickListener(this);
        btn_saveChanges.setOnClickListener(this);
        textView_mail.setOnClickListener(this);
        textView_phone.setOnClickListener(this);
        textView_age.setOnClickListener(this);
        textView_gender.setOnClickListener(this);

        setSpinner();

        AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
        currentUID = user.getUid();
        Log.w(Constants.PROFILE_TAG, "GIVE DETAIL : AGConnectUser UID : " + currentUID);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getLastLocationAndAddress();

        showProgressDialog();

        mHandler.post(() -> {
            mCloudDBZoneWrapper.addCallBacks(ProfileFragment.this);
            mCloudDBZoneWrapper.createObjectType();
            mCloudDBZoneWrapper.openCloudDBZone();
            mCloudDBZoneWrapper.getUserDetail();
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
                        Log.i(Constants.PROFILE_TAG, "getLastLocation onSuccess location is null");
                        return;
                    }
                    Geocoder geo = new Geocoder(getContext(), Locale.getDefault());
                    try {
                        List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        String cityName = addresses.get(0).getAdminArea();
                        String countryName = getContext().getResources().getConfiguration().locale.getDisplayCountry();
                        textView_country.setText(countryName);
                        textView_city.setText(cityName);
                        Log.e(Constants.PROFILE_TAG, "CITY : " + cityName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Log.e(Constants.PROFILE_TAG, "getLastLocation onFailure:" + e.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(Constants.PROFILE_TAG, "getLastLocation exception:" + e.getMessage());
        }
    }

    public void editProfile(TextView id, String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle(title);

        final EditText input = new EditText(this.getContext());
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setText(id.getText().toString());
        builder.setView(input);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                id.setText(input.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    public void onAddOrQuery(List<TableUser> userList) {
        for(int i = 0; i <= userList.size()-1; i++){
            if(userList.get(i).getAuth_id().equals(currentUID)){
                textView_mail.setText(userList.get(i).getUser_mail());
                textView_phone.setText(userList.get(i).getUser_phone());
                textView_age.setText(userList.get(i).getUser_age());
                textView_gender.setText(userList.get(i).getUser_gender());
                textView_centerName.setText(userList.get(i).getUser_name());

                editText_profileMail.setText(userList.get(i).getUser_mail());
                editText_profilePhone.setText(userList.get(i).getUser_phone());
                editText_profileAge.setText(userList.get(i).getUser_age());

                UserID = userList.get(i).getUser_id();
                UserToken = userList.get(i).getUser_token();
                UserUID = userList.get(i).getAuth_id();
                UserGender = userList.get(i).getUser_gender();

                if(UserGender.equals("Male")){
                    spinner_profileGender.setSelection(1);
                }else if(UserGender.equals("Female")){
                    spinner_profileGender.setSelection(2);
                }else if(UserGender.equals("Other")){
                    spinner_profileGender.setSelection(3);
                }else{
                    spinner_profileGender.setSelection(0);
                }

                dismisProgressDialog();
            }
        }
    }

    public void updateProfile(){
        TableUser user = new TableUser();
        user.setUser_id(UserID);
        user.setUser_token(UserToken);
        user.setUser_name(textView_centerName.getText().toString());
        user.setUser_mail(editText_profileMail.getText().toString());
        user.setUser_phone(editText_profilePhone.getText().toString());
        user.setUser_age(editText_profileAge.getText().toString());
        user.setUser_gender(selectedGender);
        user.setUser_country(textView_country.getText().toString());
        user.setUser_city(textView_city.getText().toString());
        user.setAuth_id(UserUID);
        mHandler.post(() -> {
            mCloudDBZoneWrapper.insertUser(user);
        });

        textView_mail.setText(editText_profileMail.getText().toString());
        textView_phone.setText(editText_profilePhone.getText().toString());
        textView_age.setText(editText_profileAge.getText().toString());
        textView_gender.setText(selectedGender);
    }

    public void changeVisibilities(String visibilityState){
        if(visibilityState.equals("VISIBLE")){

            btn_editProfile.setVisibility(View.GONE);
            btn_saveChanges.setVisibility(View.VISIBLE);

            relativeLayout_profileGenderArea.setVisibility(View.VISIBLE);
            editText_profileMail.setVisibility(View.VISIBLE);
            editText_profilePhone.setVisibility(View.VISIBLE);
            editText_profileAge.setVisibility(View.VISIBLE);
            textView_mail.setVisibility(View.GONE);
            textView_phone.setVisibility(View.GONE);
            textView_age.setVisibility(View.GONE);
            textView_gender.setVisibility(View.GONE);
            textView_country.setVisibility(View.GONE);
            textView_city.setVisibility(View.GONE);

        }else if(visibilityState.equals("GONE")){

            btn_editProfile.setVisibility(View.VISIBLE);
            btn_saveChanges.setVisibility(View.GONE);

            relativeLayout_profileGenderArea.setVisibility(View.GONE);
            editText_profileMail.setVisibility(View.GONE);
            editText_profilePhone.setVisibility(View.GONE);
            editText_profileAge.setVisibility(View.GONE);
            textView_mail.setVisibility(View.VISIBLE);
            textView_phone.setVisibility(View.VISIBLE);
            textView_age.setVisibility(View.VISIBLE);
            textView_gender.setVisibility(View.VISIBLE);
            textView_country.setVisibility(View.VISIBLE);
            textView_city.setVisibility(View.VISIBLE);
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
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(),R.layout.spinner_item,genderList){
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
        spinner_profileGender.setAdapter(spinnerArrayAdapter);
        spinner_profileGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGender = (String) parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_saveChanges:
                changeVisibilities("GONE");
                updateProfile();
                break;
            case R.id.btn_editProfile:
                changeVisibilities("VISIBLE");
                break;
            default:
                break;
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
