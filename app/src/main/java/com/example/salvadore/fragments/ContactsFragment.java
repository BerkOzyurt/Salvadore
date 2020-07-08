package com.example.salvadore.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.salvadore.R;
import com.example.salvadore.activities.MainActivity;
import com.example.salvadore.clouddb.CloudDBZoneWrapper;
import com.example.salvadore.clouddb.Contact;
import com.example.salvadore.clouddb.Help;
import com.example.salvadore.clouddb.TableUser;
import com.example.salvadore.utils.Constants;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactsFragment extends Fragment implements View.OnClickListener, CloudDBZoneWrapper.UiCallBack {

    View rootView;

    @BindView(R.id.linearLayout_editContact1)
    LinearLayout linearLayout_editContact1;
    @BindView(R.id.linearLayout_editContact2)
    LinearLayout linearLayout_editContact2;
    @BindView(R.id.linearLayout_editContact3)
    LinearLayout linearLayout_editContact3;
    @BindView(R.id.linearLayout_deleteContact1)
    LinearLayout linearLayout_deleteContact1;
    @BindView(R.id.linearLayout_deleteContact2)
    LinearLayout linearLayout_deleteContact2;
    @BindView(R.id.linearLayout_deleteContact3)
    LinearLayout linearLayout_deleteContact3;

    @BindView(R.id.textView_contact1Name)
    TextView textView_contact1Name;
    @BindView(R.id.textView_contact2Name)
    TextView textView_contact2Name;
    @BindView(R.id.textView_contact3Name)
    TextView textView_contact3Name;
    @BindView(R.id.textView_contact1Phone)
    TextView textView_contact1Phone;
    @BindView(R.id.textView_contact2Phone)
    TextView textView_contact2Phone;
    @BindView(R.id.textView_contact3Phone)
    TextView textView_contact3Phone;
    @BindView(R.id.textView_contact1Mail)
    TextView textView_contact1Mail;
    @BindView(R.id.textView_contact2Mail)
    TextView textView_contact2Mail;
    @BindView(R.id.textView_contact3Mail)
    TextView textView_contact3Mail;

    @BindView(R.id.linearLayout_editContact)
    LinearLayout linearLayout_editContact;

    @BindView(R.id.editText_contactName)
    EditText editText_contactName;
    @BindView(R.id.editText_contactPhone)
    EditText editText_contactPhone;
    @BindView(R.id.editText_contactMail)
    EditText editText_contactMail;


    @BindView(R.id.btn_saveContact)
    Button btn_saveContact;
    @BindView(R.id.btn_cancelContact)
    Button btn_cancelContact;

    private MyHandler mHandler = new MyHandler();
    private CloudDBZoneWrapper mCloudDBZoneWrapper;

    String currentUID = "";
    int currentUserID, ID1,ID2,ID3, currentContactID, LastContactID;

    List<Contact> myContacts = new ArrayList<Contact>();

    private static final class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            // dummy
        }
    }

    public ContactsFragment() {
        mCloudDBZoneWrapper = new CloudDBZoneWrapper();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_contacts, container, false);
        ButterKnife.bind(this,rootView);

        linearLayout_editContact1.setOnClickListener(this);
        linearLayout_editContact2.setOnClickListener(this);
        linearLayout_editContact3.setOnClickListener(this);
        linearLayout_deleteContact1.setOnClickListener(this);
        linearLayout_deleteContact2.setOnClickListener(this);
        linearLayout_deleteContact3.setOnClickListener(this);
        btn_saveContact.setOnClickListener(this);
        btn_cancelContact.setOnClickListener(this);

        AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
        currentUID = user.getUid();
        Log.w(Constants.PROFILE_TAG, "GIVE DETAIL : AGConnectUser UID : " + currentUID);

        mHandler.post(() -> {
            mCloudDBZoneWrapper.addCallBacks(ContactsFragment.this);
            mCloudDBZoneWrapper.createObjectType();
            mCloudDBZoneWrapper.openCloudDBZone();
            mCloudDBZoneWrapper.getUserDetail();
        });

        return rootView;
    }


    public void setContactLayouts(int selectedID, String contactName, String contactPhone, String contactMail, int contactNumber){
        Log.i("TAG ", "setContactsLayout 1: " + contactName + " " + contactPhone + " " + contactNumber);
        if(contactNumber == 1){
            ID1 = selectedID;
            textView_contact1Name.setText(contactName);
            textView_contact1Phone.setText(contactPhone);
            textView_contact1Mail.setText(contactMail);
            Log.i("TAG ", "setContactsLayout 2: " + contactName + " " + contactPhone + " " + contactNumber);
        }else if(contactNumber == 2 ){
            ID2 = selectedID;
            textView_contact2Name.setText(contactName);
            textView_contact2Phone.setText(contactPhone);
            textView_contact2Mail.setText(contactMail);
            Log.i("TAG ", "" +
                    "" +
                    " 3: " + contactName + " " + contactPhone + " " + contactNumber);
        }else if(contactNumber == 3){
            ID3 = selectedID;
            textView_contact3Name.setText(contactName);
            textView_contact3Phone.setText(contactPhone);
            textView_contact3Mail.setText(contactMail);
            Log.i("TAG ", "setContactsLayout 4: " + contactName + " " + contactPhone + " " + contactNumber);
        }
    }

    public void setContactsLayout(){
        int listSize = myContacts.size() -1;
        for(int i = 0; i<= listSize; i++){
            if (i == 0) {
                ID1 = myContacts.get(i).getId();
                textView_contact1Name.setText(myContacts.get(i).getContact_name());
                textView_contact1Phone.setText(myContacts.get(i).getContact_phone());
                textView_contact1Mail.setText(myContacts.get(i).getContact_mail());
                Log.i("TAG ", "setContactsLayout 2: " + ID1 + " " + myContacts.get(i).getContact_name() + " " + myContacts.get(i).getContact_phone() + " " + myContacts.get(i).getContact_mail());
            }else if(i == 1){
                ID2 = myContacts.get(i).getId();
                textView_contact2Name.setText(myContacts.get(i).getContact_name());
                textView_contact2Phone.setText(myContacts.get(i).getContact_phone());
                textView_contact2Mail.setText(myContacts.get(i).getContact_mail());
                Log.i("TAG ", "setContactsLayout 3: " + ID2 + " " + myContacts.get(i).getContact_name() + " " + myContacts.get(i).getContact_phone() + " " + myContacts.get(i).getContact_mail());
            }else if(i == 2){
                ID3 = myContacts.get(i).getId();
                textView_contact3Name.setText(myContacts.get(i).getContact_name());
                textView_contact3Phone.setText(myContacts.get(i).getContact_phone());
                textView_contact3Mail.setText(myContacts.get(i).getContact_mail());
                Log.i("TAG ", "setContactsLayout 4: " + ID3 + " " + myContacts.get(i).getContact_name() + " " + myContacts.get(i).getContact_phone() + " " + myContacts.get(i).getContact_mail());
            }
        }
    }

    @Override
    public void onAddOrQuery(List<TableUser> userList) {
        Log.i("TAG ", "CurrentUID : " + currentUID);
        for(int i = 0; i <= userList.size()-1; i++) {
            if (userList.get(i).getAuth_id().equals(currentUID)) {
                currentUserID = userList.get(i).getUser_id();

                Log.i("TAG ", "CurrentUserID : " + currentUserID);
            }
        }
        mHandler.post(() -> {
            mCloudDBZoneWrapper.getAllContacts();
            Log.i("TAG ", "GoGetAllContacts : " );
        });

    }

    public void editContact(int ContactID, String ContactName, String ContactMail, String ContactPhone){
        linearLayout_editContact.setVisibility(View.VISIBLE);
        editText_contactName.setText(ContactName);
        editText_contactMail.setText(ContactMail);
        editText_contactPhone.setText(ContactPhone);

        if(ContactID != 0){
            currentContactID = ContactID;
        }else{
            currentContactID = LastContactID + 1;
        }
    }

    public void saveChanges(){
        Contact contact = new Contact();
        contact.setId(currentContactID);
        contact.setUser_id(Integer.toString(currentUserID));
        contact.setContact_name(editText_contactName.getText().toString());
        contact.setContact_mail(editText_contactMail.getText().toString());
        contact.setContact_phone(editText_contactPhone.getText().toString());
        mHandler.post(() -> {
            mCloudDBZoneWrapper.insertContact(contact);
            linearLayout_editContact.setVisibility(View.GONE);
        });

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setSelectedTab(-1, Constants.TAB_HOME);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearLayout_editContact1:
                editContact(ID1, textView_contact1Name.getText().toString(),textView_contact1Mail.getText().toString(), textView_contact1Phone.getText().toString());
                break;
            case R.id.linearLayout_editContact2:
                editContact(ID2, textView_contact2Name.getText().toString(),textView_contact2Mail.getText().toString(), textView_contact2Phone.getText().toString());
                break;
            case R.id.linearLayout_editContact3:
                editContact(ID3, textView_contact3Name.getText().toString(),textView_contact3Mail.getText().toString(), textView_contact3Phone.getText().toString());
                break;
            case R.id.btn_saveContact:
                saveChanges();
                break;
            case R.id.btn_cancelContact:
                linearLayout_editContact.setVisibility(View.GONE);
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
        LastContactID = lastUserID;
        Log.w(Constants.REGISTER_INFO_TAG, "Turned UserID: " + LastContactID);
    }

    @Override
    public void onHelpAddQuery(List<Help> helpList) {

    }

    @Override
    public void onContactsAddQuery(List<Contact> contactList) {
        Log.i("TAG ", "OnContactsAddQuery : ");
        for(int i = 0; i <= contactList.size()-1; i++){
            if(contactList.get(i).getUser_id().equals(Integer.toString(currentUserID))){
                myContacts.add(contactList.get(i));
                Log.i("TAG ", "TURNED VALUE : " + i + contactList.get(i).getContact_name());
            }
        }
        setContactsLayout();
        mHandler.post(() -> {
            mCloudDBZoneWrapper.getLastContactID();
        });
    }

    @Override
    public void isInsert(boolean inserted) {

    }

}