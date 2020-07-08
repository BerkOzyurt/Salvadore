package com.example.salvadore.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.salvadore.adapters.HelpListAdapter;
import com.example.salvadore.clouddb.CloudDBZoneWrapper;
import com.example.salvadore.clouddb.Contact;
import com.example.salvadore.clouddb.Help;
import com.example.salvadore.clouddb.TableUser;
import com.example.salvadore.listeners.HelpListItemClickListener;
import com.example.salvadore.listeners.PaginationScrollListener;
import com.example.salvadore.utils.Constants;
import com.example.salvadore.R;
import com.example.salvadore.activities.MainActivity;
import com.example.salvadore.utils.GlobalData;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.hms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HelpListFragment extends BaseFragmentV2 implements View.OnClickListener, CloudDBZoneWrapper.UiCallBack, HelpListItemClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.radioButton_allHelps)
    RadioButton radioButton_allHelps;
    @BindView(R.id.radioButton_myHelps)
    RadioButton radioButton_myHelps;

    ArrayList<Help> newHelpList = new ArrayList<Help>();
    ArrayList<Help> allHelpList = new ArrayList<Help>();

    View rootView;

    GlobalData globalData = GlobalData.getInstance();

    private MyHandler mHandler = new MyHandler();
    private CloudDBZoneWrapper mCloudDBZoneWrapper;

    String currentUID = "";
    int userID;

    HelpListAdapter helpListAdapter;
    private boolean isLastPage = false;
    private boolean isLoading = false;

    private static final class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            // dummy
        }
    }

    public HelpListFragment() {
        mCloudDBZoneWrapper = new CloudDBZoneWrapper();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_help_list, container, false);
        ButterKnife.bind(this,rootView);

        AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
        currentUID = user.getUid();
        Log.w(Constants.HELP_LIST_TAG, "GIVE DETAIL : AGConnectUser UID : " + currentUID);

        radioButton_allHelps.setChecked(true);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mHandler.post(() -> {
                    mCloudDBZoneWrapper.getAllHep();
                });
            }
        });

        showProgressDialog();

        mHandler.post(() -> {
            mCloudDBZoneWrapper.addCallBacks(HelpListFragment.this);
            mCloudDBZoneWrapper.createObjectType();
            mCloudDBZoneWrapper.openCloudDBZone();
            mCloudDBZoneWrapper.getAllHep();
            mCloudDBZoneWrapper.getUserDetail();
        });

        return rootView;

    }

    @Override
    public void onClick(View v) {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setSelectedTab(Constants.TAB_HELP_LIST, Constants.TAB_HELP_DETAIL);
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        if(radioButton_allHelps.isChecked()){
            allHelpList.clear();
            for(int j = 0; j<= helpList.size()-1; j++){
                if(!helpList.get(j).getState().equals("Closed")){
                    allHelpList.add(helpList.get(j));
                }
            }
            helpListAdapter = new HelpListAdapter(allHelpList,getActivity(),this);
        }else{
            newHelpList.clear();
            for(int i = 0; i <= helpList.size()-1; i++){
                if(helpList.get(i).getUser_id()==userID){
                    //helpList.remove(i);
                    Log.w("USERID ", "USER ID IN FOR : " + helpList.get(i).getUser_id());
                    newHelpList.add(helpList.get(i));
                }
            }
            helpListAdapter = new HelpListAdapter(newHelpList,getActivity(),this);
        }

        recyclerView.setAdapter(helpListAdapter);

        recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        dismisProgressDialog();
    }

    @Override
    public void onContactsAddQuery(List<Contact> contactList) {

    }

    @Override
    public void isInsert(boolean inserted) {

    }

    @Override
    public void onItemClicked(Help help, int position) {
        globalData.setHelpID(help.getId());
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setSelectedTab(Constants.TAB_HELP_LIST, Constants.TAB_HELP_DETAIL);
    }
}
