package com.example.salvadore.clouddb;

import android.content.Context;
import android.nfc.NfcAdapter;
import android.util.Log;

import com.example.salvadore.utils.Constants;
import com.huawei.agconnect.cloud.database.AGConnectCloudDB;
import com.huawei.agconnect.cloud.database.CloudDBZone;
import com.huawei.agconnect.cloud.database.CloudDBZoneConfig;
import com.huawei.agconnect.cloud.database.CloudDBZoneObjectList;
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery;
import com.huawei.agconnect.cloud.database.CloudDBZoneSnapshot;
import com.huawei.agconnect.cloud.database.CloudDBZoneTask;
import com.huawei.agconnect.cloud.database.ListenerHandler;
import com.huawei.agconnect.cloud.database.OnFailureListener;
import com.huawei.agconnect.cloud.database.OnSnapshotListener;
import com.huawei.agconnect.cloud.database.OnSuccessListener;
import com.huawei.agconnect.cloud.database.exceptions.AGConnectCloudDBException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CloudDBZoneWrapper {

    private AGConnectCloudDB mCloudDB;
    private CloudDBZone mCloudDBZone;
    private CloudDBZoneConfig mConfig;
    private UiCallBack mUiCallBack;


    private ReadWriteLock mReadWriteLock = new ReentrantReadWriteLock();

    public interface UiCallBack {// Activity'de override edilecek.
        void onAddOrQuery(List<TableUser> userList);

        void onSubscribe(List<TableUser> userList);

        void onDelete(List<TableUser> bookInfoList);

        void updateUiOnError(String errorMessage);

        void isLoginSuccess(boolean state);

        void isLastID(int lastUserID);

        void onHelpAddQuery(List<Help> helpList);

        void onContactsAddQuery(List<Contact> contactList);

        void isInsert(boolean inserted);
    }

    public CloudDBZoneWrapper() {
        mCloudDB = AGConnectCloudDB.getInstance();
    }


    public static void initAGConnectCloudDB(Context context) {
        AGConnectCloudDB.initialize(context);
        Log.w(Constants.DB_ZONE_WRAPPER, "initAGConnectCloudDB" );
    }

    public void createObjectType() {
        try {
            mCloudDB.createObjectType(ObjectTypeInfoHelper.getObjectTypeInfo());
            Log.w(Constants.DB_ZONE_WRAPPER, "createObjectTypeSuccess " );
        } catch (AGConnectCloudDBException e) {
            Log.w(Constants.DB_ZONE_WRAPPER, "createObjectType: " + e.getMessage());
        }
    }
    public void openCloudDBZone() {
        mConfig = new CloudDBZoneConfig("xxx",
                CloudDBZoneConfig.CloudDBZoneSyncProperty.CLOUDDBZONE_CLOUD_CACHE,
                CloudDBZoneConfig.CloudDBZoneAccessProperty.CLOUDDBZONE_PUBLIC);
        mConfig.setPersistenceEnabled(true);
        Log.w(Constants.DB_ZONE_WRAPPER, "openCloudDBZoneSuccess " );
        try {
            mCloudDBZone = mCloudDB.openCloudDBZone(mConfig, true);
        } catch (AGConnectCloudDBException e) {
            Log.w(Constants.DB_ZONE_WRAPPER, "openCloudDBZone: " + e.getMessage());
        }
    }

    public void closeCloudDBZone() {
        try {
            mCloudDB.closeCloudDBZone(mCloudDBZone);
            Log.w(Constants.DB_ZONE_WRAPPER, "closeCloudDBZoneSuccess " );
        } catch (AGConnectCloudDBException e) {
            Log.w(Constants.DB_ZONE_WRAPPER, "closeCloudDBZone: " + e.getMessage());
        }
    }
    public void deleteCloudDBZone() {
        try {
            mCloudDB.deleteCloudDBZone(mConfig.getCloudDBZoneName());
            Log.w(Constants.DB_ZONE_WRAPPER, "deleteCloudDBZoneSuccess " );
        } catch (AGConnectCloudDBException e) {
            Log.w(Constants.DB_ZONE_WRAPPER, "deleteCloudDBZone: " + e.getMessage());
        }
    }

    public void addCallBacks(UiCallBack uiCallBack) {
        mUiCallBack = uiCallBack;
    }

    public void insertUser(TableUser user) {
        if (mCloudDBZone == null) {
            Log.w(Constants.DB_ZONE_WRAPPER, "INSERT USER : CloudDBZone is null, try re-open it");
            return;
        }
        CloudDBZoneTask<Integer> upsertTask = mCloudDBZone.executeUpsert(user);
        if (mUiCallBack == null) {
            return;
        }
        upsertTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer cloudDBZoneResult) {
                Log.w(Constants.DB_ZONE_WRAPPER, "INSERT USER : upsert " + cloudDBZoneResult + " records");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                mUiCallBack.updateUiOnError("INSERT USER : Insert user info failed");
            }
        });
    }

    public void loginDB(String uid) {
        if (mCloudDBZone == null) {
            Log.w(Constants.DB_ZONE_WRAPPER, "LOGIN DB : CloudDBZone is null, try re-open it");
            return;
        }
        CloudDBZoneTask<CloudDBZoneSnapshot<TableUser>> queryTask = mCloudDBZone.executeQuery(
                CloudDBZoneQuery.where(TableUser.class),
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);
        queryTask.addOnSuccessListener(new OnSuccessListener<CloudDBZoneSnapshot<TableUser>>() {
            @Override
            public void onSuccess(CloudDBZoneSnapshot<TableUser> snapshot) {
                loginResult(snapshot, uid);
                Log.w(Constants.DB_ZONE_WRAPPER, "LOGIN DB : GoResults: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (mUiCallBack != null) {
                    mUiCallBack.updateUiOnError("LOGIN DB : Query user list from cloud failed");
                }
            }
        });
    }
    private boolean loginResult(CloudDBZoneSnapshot<TableUser> snapshot, String UID) {
        boolean turn = false;
        CloudDBZoneObjectList<TableUser> userInfoCursor = snapshot.getSnapshotObjects();
        List<TableUser> userInfoList = new ArrayList<>();
        try {
            while (userInfoCursor.hasNext()) {
                TableUser userInfo = userInfoCursor.next();
                userInfoList.add(userInfo);
                Log.w(Constants.DB_ZONE_WRAPPER, "LOGIN RESULT : TurnedUID : " + userInfo.getAuth_id());
                Log.w(Constants.DB_ZONE_WRAPPER, "LOGIN RESULT : UID : " + UID);
                for(int i = 0; i <= userInfoList.size()-1; i++){
                    if(userInfoList.get(i).getAuth_id().equals(UID)){
                        turn = true;
                        Log.w(Constants.DB_ZONE_WRAPPER, "LOGIN RESULT : TURN1 : " + turn);
                    }
                }
            }
        } catch (AGConnectCloudDBException e) {
            Log.w(Constants.DB_ZONE_WRAPPER, "LOGIN RESULT : CatchprocessQueryResult: " + e.getMessage());
            turn = false;
            Log.w(Constants.DB_ZONE_WRAPPER, "LOGIN RESULT : TURN3 : " + turn);
        }
        snapshot.release();
        if (mUiCallBack != null) {
            mUiCallBack.onAddOrQuery(userInfoList);
            mUiCallBack.isLoginSuccess(turn);
        }
        Log.w(Constants.DB_ZONE_WRAPPER, "LOGIN RESULT : TURN4 : " + turn);
        return turn;
    }

    public void getLastUserID() {
        if (mCloudDBZone == null) {
            Log.w(Constants.DB_ZONE_WRAPPER, "GET LAST USER ID : CloudDBZone is null, try re-open it");
            return;
        }
        CloudDBZoneTask<CloudDBZoneSnapshot<TableUser>> queryTask = mCloudDBZone.executeQuery(
                CloudDBZoneQuery.where(TableUser.class),
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);
        queryTask.addOnSuccessListener(new OnSuccessListener<CloudDBZoneSnapshot<TableUser>>() {
            @Override
            public void onSuccess(CloudDBZoneSnapshot<TableUser> snapshot) {
                userIDResult(snapshot);
                Log.w(Constants.DB_ZONE_WRAPPER, "GET LAST USER ID : GoResults: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (mUiCallBack != null) {
                    mUiCallBack.updateUiOnError("GET LAST USER ID : Query user list from cloud failed");
                }
            }
        });
    }

    private void userIDResult(CloudDBZoneSnapshot<TableUser> snapshot) {
        int lastID = 0;
        CloudDBZoneObjectList<TableUser> userInfoCursor = snapshot.getSnapshotObjects();
        List<TableUser> userInfoList = new ArrayList<>();
        try {
            while (userInfoCursor.hasNext()) {
                TableUser userInfo = userInfoCursor.next();
                userInfoList.add(userInfo);
                lastID = userInfo.getUser_id();
                Log.w(Constants.DB_ZONE_WRAPPER, "USER ID RESULT : processQueryResult: " +lastID);

            }
        } catch (AGConnectCloudDBException e) {
            Log.w(Constants.DB_ZONE_WRAPPER, "USER ID RESULT : processQueryResult: " + e.getMessage());
        }
        snapshot.release();
        if (mUiCallBack != null) {
            mUiCallBack.isLastID(lastID);
        }
    }

    public void getUserDetail() {
        if (mCloudDBZone == null) {
            Log.w(Constants.DB_ZONE_WRAPPER, "GET USER DETAIL : CloudDBZone is null, try re-open it");
            return;
        }
        CloudDBZoneTask<CloudDBZoneSnapshot<TableUser>> queryTask = mCloudDBZone.executeQuery(
                CloudDBZoneQuery.where(TableUser.class),
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);
        queryTask.addOnSuccessListener(new OnSuccessListener<CloudDBZoneSnapshot<TableUser>>() {
            @Override
            public void onSuccess(CloudDBZoneSnapshot<TableUser> snapshot) {
                userDetailResult(snapshot);
                Log.w(Constants.DB_ZONE_WRAPPER, "GET USER DETAIL : GoResults: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (mUiCallBack != null) {
                    mUiCallBack.updateUiOnError("GET USER DETAIL : Query user list from cloud failed");
                }
            }
        });
    }

    private void userDetailResult(CloudDBZoneSnapshot<TableUser> snapshot) {
            CloudDBZoneObjectList<TableUser> userInfoCursor = snapshot.getSnapshotObjects();
        List<TableUser> userInfoList = new ArrayList<>();
        try {
            while (userInfoCursor.hasNext()) {
                TableUser userInfo = userInfoCursor.next();
                userInfoList.add(userInfo);
                Log.w(Constants.DB_ZONE_WRAPPER, "USER DETAIL RESULT : processQueryResult: " + userInfo.getUser_city());

            }
        } catch (AGConnectCloudDBException e) {
            Log.w(Constants.DB_ZONE_WRAPPER, "USER DETAIL RESULT : processQueryResult: " + e.getMessage());
        }
        snapshot.release();
        if (mUiCallBack != null) {
            mUiCallBack.onAddOrQuery(userInfoList);
        }
    }

    public void getLastHelpID() {
        if (mCloudDBZone == null) {
            Log.w(Constants.DB_ZONE_WRAPPER, "GET LAST HELP ID : CloudDBZone is null, try re-open it");
            return;
        }
        CloudDBZoneTask<CloudDBZoneSnapshot<Help>> queryTask = mCloudDBZone.executeQuery(
                CloudDBZoneQuery.where(Help.class),
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);
        queryTask.addOnSuccessListener(new OnSuccessListener<CloudDBZoneSnapshot<Help>>() {
            @Override
            public void onSuccess(CloudDBZoneSnapshot<Help> snapshot) {
                helpIDResult(snapshot);
                Log.w(Constants.DB_ZONE_WRAPPER, "GET LAST HELP ID : GoResults: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (mUiCallBack != null) {
                    mUiCallBack.updateUiOnError("GET LAST HELP ID : Query help list from cloud failed");
                }
            }
        });
    }
    private void helpIDResult(CloudDBZoneSnapshot<Help> snapshot) {
        int lastHelpID = 0;
        CloudDBZoneObjectList<Help> helpInfoCursor = snapshot.getSnapshotObjects();
        List<Help> helpInfoList = new ArrayList<>();
        try {
            while (helpInfoCursor.hasNext()) {
                Help helpInfo = helpInfoCursor.next();
                helpInfoList.add(helpInfo);
                lastHelpID = helpInfo.getId();
                Log.w(Constants.DB_ZONE_WRAPPER, "HELP ID RESULT : processQueryResult: " +lastHelpID);
            }
        } catch (AGConnectCloudDBException e) {
            Log.w(Constants.DB_ZONE_WRAPPER, "HELP ID RESULT : processQueryResult: " + e.getMessage());
        }
        snapshot.release();
        if (mUiCallBack != null) {
            mUiCallBack.isLastID(lastHelpID);
        }
    }

    public void insertHelp(Help help) {
        if (mCloudDBZone == null) {
            Log.w(Constants.DB_ZONE_WRAPPER, "INSERT HELP : CloudDBZone is null, try re-open it");
            mUiCallBack.isInsert(false);
            return;
        }
        CloudDBZoneTask<Integer> upsertTask = mCloudDBZone.executeUpsert(help);
        if (mUiCallBack == null) {
            return;
        }
        upsertTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer cloudDBZoneResult) {
                Log.w(Constants.DB_ZONE_WRAPPER, "INSERT HELP : upsert " + cloudDBZoneResult + " records" + help.getId());
                mUiCallBack.isInsert(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                mUiCallBack.updateUiOnError("INSERT HELP : Insert help info failed");
                mUiCallBack.isInsert(false);
            }
        });
    }

    public void getAllHep() {
        if (mCloudDBZone == null) {
            Log.w(Constants.DB_ZONE_WRAPPER, "GET USER DETAIL : CloudDBZone is null, try re-open it");
            return;
        }
        CloudDBZoneTask<CloudDBZoneSnapshot<Help>> queryTask = mCloudDBZone.executeQuery(
                CloudDBZoneQuery.where(Help.class),
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);
        queryTask.addOnSuccessListener(new OnSuccessListener<CloudDBZoneSnapshot<Help>>() {
            @Override
            public void onSuccess(CloudDBZoneSnapshot<Help> snapshot) {
                allHelpResult(snapshot);
                Log.w(Constants.DB_ZONE_WRAPPER, "GET USER DETAIL : GoResults: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (mUiCallBack != null) {
                    mUiCallBack.updateUiOnError("GET USER DETAIL : Query user list from cloud failed");
                }
            }
        });
    }

    private void allHelpResult(CloudDBZoneSnapshot<Help> snapshot) {
        CloudDBZoneObjectList<Help> userInfoCursor = snapshot.getSnapshotObjects();
        List<Help> helpInfoList = new ArrayList<>();
        try {
            while (userInfoCursor.hasNext()) {
                Help helpInfo = userInfoCursor.next();
                helpInfoList.add(helpInfo);
                Log.w(Constants.DB_ZONE_WRAPPER, "USER DETAIL RESULT : processQueryResult: " + helpInfo.getAddress());
            }
        } catch (AGConnectCloudDBException e) {
            Log.w(Constants.DB_ZONE_WRAPPER, "USER DETAIL RESULT : processQueryResult: " + e.getMessage());
        }
        snapshot.release();
        if (mUiCallBack != null) {
            mUiCallBack.onHelpAddQuery(helpInfoList);
        }
    }

    public void insertContact(Contact contact) {
        if (mCloudDBZone == null) {
            Log.w(Constants.DB_ZONE_WRAPPER, "INSERT CONTACT : CloudDBZone is null, try re-open it");
            mUiCallBack.isInsert(false);
            return;
        }
        CloudDBZoneTask<Integer> upsertTask = mCloudDBZone.executeUpsert(contact);
        if (mUiCallBack == null) {
            return;
        }
        upsertTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer cloudDBZoneResult) {
                Log.w(Constants.DB_ZONE_WRAPPER, "INSERT USER : upsert " + cloudDBZoneResult + " records");
                mUiCallBack.isInsert(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                mUiCallBack.updateUiOnError("INSERT CONTACT : Insert contact info failed");
                mUiCallBack.isInsert(false);
            }
        });
    }

    public void getAllContacts() {
        if (mCloudDBZone == null) {
            Log.w(Constants.DB_ZONE_WRAPPER, "GET CONTACTS : CloudDBZone is null, try re-open it");
            return;
        }
        CloudDBZoneTask<CloudDBZoneSnapshot<Contact>> queryTask = mCloudDBZone.executeQuery(
                CloudDBZoneQuery.where(Contact.class),
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);
        queryTask.addOnSuccessListener(new OnSuccessListener<CloudDBZoneSnapshot<Contact>>() {
            @Override
            public void onSuccess(CloudDBZoneSnapshot<Contact> snapshot) {
                allContactsResult(snapshot);
                Log.w(Constants.DB_ZONE_WRAPPER, "GET CONTACTS DETAIL : GoResults: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (mUiCallBack != null) {
                    mUiCallBack.updateUiOnError("GET CONTACTS DETAIL : Query user list from cloud failed");
                }
            }
        });
    }

    private void allContactsResult(CloudDBZoneSnapshot<Contact> snapshot) {
        CloudDBZoneObjectList<Contact> contactInfoCursor = snapshot.getSnapshotObjects();
        List<Contact> contactList = new ArrayList<>();
        try {
            while (contactInfoCursor.hasNext()) {
                Contact contactInfo = contactInfoCursor.next();
                contactList.add(contactInfo);
                Log.w(Constants.DB_ZONE_WRAPPER, "CONTACTS DETAIL RESULT : processQueryResult: " + contactInfo.getUser_id());
            }
        } catch (AGConnectCloudDBException e) {
            Log.w(Constants.DB_ZONE_WRAPPER, "CONTACTS DETAIL RESULT : processQueryResult: " + e.getMessage());
        }
        mUiCallBack.onContactsAddQuery(contactList);
        snapshot.release();
    }

    public void getLastContactID() {
        if (mCloudDBZone == null) {
            Log.w(Constants.DB_ZONE_WRAPPER, "GET LAST CONTACT ID : CloudDBZone is null, try re-open it");
            return;
        }
        CloudDBZoneTask<CloudDBZoneSnapshot<Contact>> queryTask = mCloudDBZone.executeQuery(
                CloudDBZoneQuery.where(Contact.class),
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);
        queryTask.addOnSuccessListener(new OnSuccessListener<CloudDBZoneSnapshot<Contact>>() {
            @Override
            public void onSuccess(CloudDBZoneSnapshot<Contact> snapshot) {
                contactIDResult(snapshot);
                Log.w(Constants.DB_ZONE_WRAPPER, "GET LAST CONTACT ID : GoResults: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (mUiCallBack != null) {
                    mUiCallBack.updateUiOnError("GET LAST CONTACT ID : Query user list from cloud failed");
                }
            }
        });
    }

    private void contactIDResult(CloudDBZoneSnapshot<Contact> snapshot) {
        int lastContactID = 0;
        CloudDBZoneObjectList<Contact> contactInfoCursor = snapshot.getSnapshotObjects();
        List<Contact> contactInfoList = new ArrayList<>();
        try {
            while (contactInfoCursor.hasNext()) {
                Contact contactInfo = contactInfoCursor.next();
                contactInfoList.add(contactInfo);
                lastContactID = contactInfo.getId();
                Log.w(Constants.DB_ZONE_WRAPPER, "CONTACT ID RESULT : processQueryResult: " +lastContactID);
            }
        } catch (AGConnectCloudDBException e) {
            Log.w(Constants.DB_ZONE_WRAPPER, "CONTACT ID RESULT : processQueryResult: " + e.getMessage());
        }
        snapshot.release();
        if (mUiCallBack != null) {
            mUiCallBack.isLastID(lastContactID);
        }
    }

    public void getAllUsers() {
        if (mCloudDBZone == null) {
            Log.w(Constants.DB_ZONE_WRAPPER, "CloudDBZone is null, try re-open it");
            return;
        }
        CloudDBZoneTask<CloudDBZoneSnapshot<TableUser>> queryTask = mCloudDBZone.executeQuery(
                CloudDBZoneQuery.where(TableUser.class),
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);
        queryTask.addOnSuccessListener(new OnSuccessListener<CloudDBZoneSnapshot<TableUser>>() {
            @Override
            public void onSuccess(CloudDBZoneSnapshot<TableUser> snapshot) {
                processQueryResult(snapshot);
                Log.w(Constants.DB_ZONE_WRAPPER, "GoResults: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (mUiCallBack != null) {
                    mUiCallBack.updateUiOnError("Query user list from cloud failed");
                }
            }
        });
    }
    private void processQueryResult(CloudDBZoneSnapshot<TableUser> snapshot) {
        CloudDBZoneObjectList<TableUser> userInfoCursor = snapshot.getSnapshotObjects();
        List<TableUser> userInfoList = new ArrayList<>();
        try {
            while (userInfoCursor.hasNext()) {
                TableUser userInfo = userInfoCursor.next();
                userInfoList.add(userInfo);
                Log.w(Constants.DB_ZONE_WRAPPER, "processQueryResult: " + userInfo.getUser_city());

            }
        } catch (AGConnectCloudDBException e) {
            Log.w(Constants.DB_ZONE_WRAPPER, "processQueryResult: " + e.getMessage());
        }
        snapshot.release();
        if (mUiCallBack != null) {
            mUiCallBack.onAddOrQuery(userInfoList);
        }
    }
}
