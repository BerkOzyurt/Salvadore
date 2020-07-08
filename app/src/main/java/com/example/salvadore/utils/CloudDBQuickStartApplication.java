package com.example.salvadore.utils;

        import android.app.Application;

        import com.example.salvadore.clouddb.CloudDBZoneWrapper;

public class CloudDBQuickStartApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CloudDBZoneWrapper.initAGConnectCloudDB(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
