package com.example.salvadore.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.salvadore.R;
import com.example.salvadore.utils.Constants;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.SupportMapFragment;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.LatLngBounds;
import com.huawei.hms.maps.model.Marker;
import com.huawei.hms.maps.model.MarkerOptions;
import com.huawei.hms.maps.model.Polyline;
import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.AddressDetail;
import com.huawei.hms.site.api.model.Coordinate;
import com.huawei.hms.site.api.model.LocationType;
import com.huawei.hms.site.api.model.NearbySearchRequest;
import com.huawei.hms.site.api.model.NearbySearchResponse;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;
import com.huawei.hms.site.api.model.TextSearchRequest;
import com.huawei.hms.site.api.model.TextSearchResponse;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapFragment extends BaseFragmentV2 implements View.OnClickListener, OnMapReadyCallback {

    View rootView;

    @BindView(R.id.mapview_mapviewdemo)
    MapView mMapView;

    @BindView(R.id.editText_search)
    EditText editText_search;

    @BindView(R.id.btn_search)
    Button btn_search;

    private HuaweiMap hMap;
    public LatLng latLng3;
    public LatLng LatLngNew;
    String MyAdres = "";

    double currentLat,currentLng;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;

    private static final String[] RUNTIME_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET
    };
    private static final int REQUEST_CODE = 100;

    //Site Kit
    private SearchService searchService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_map, container, false);

        ButterKnife.bind(this,rootView);
        if (!hasPermissions(getContext(), RUNTIME_PERMISSIONS)) {
            ActivityCompat.requestPermissions(getActivity(), RUNTIME_PERMISSIONS, REQUEST_CODE);
        }

        btn_search.setOnClickListener(this);

        searchService = SearchServiceFactory.create(getContext(), "xxx");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getLastLocationAndAddress();

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle("MapViewBundleKey");
        }
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);

        return rootView;
    }

    public void getNearbySites(double lat, double lng, String city, String language){
        NearbySearchRequest request = new NearbySearchRequest();
        Coordinate location = new Coordinate(lat, lng);
        request.setLocation(location);
        request.setQuery(city);
        request.setRadius(3000);
        request.setPoiType(LocationType.ADDRESS);
        request.setLanguage(language.toLowerCase());
        request.setPageIndex(2);
        request.setPageSize(20);
        SearchResultListener<NearbySearchResponse> resultListener = new SearchResultListener<NearbySearchResponse>() {
            @Override
            public void onSearchResult(NearbySearchResponse results) {
                List<Site> sites = results.getSites();
                if (results == null || results.getTotalCount() <= 0 || sites == null || sites.size() <= 0) {
                    return;
                }
                for (Site site : sites) {
                    Log.i("TAG SITE KIT ", "Sites : " + site.getName() + " " + site.getLocation().getLat() + " " + site.getAddress().getAdminArea());
                    hMap.addMarker(new MarkerOptions().position(new LatLng(site.getLocation().getLat(),site.getLocation().getLng())).title(site.getName()).snippet(site.getFormatAddress()));
                }
            }
            @Override
            public void onSearchError(SearchStatus status) {
                Log.i("TAG", "Error : " + status.getErrorCode() + " " + status.getErrorMessage());
            }
        };
        searchService.nearbySearch(request, resultListener);
    }

    public void search(double lat, double lng){
        TextSearchRequest textSearchRequest = new TextSearchRequest();
        Coordinate location = new Coordinate(currentLat, currentLng);
        textSearchRequest.setQuery(editText_search.getText().toString());
        textSearchRequest.setLocation(location);
        searchService.textSearch(textSearchRequest, new SearchResultListener<TextSearchResponse>() {
            @Override
            public void onSearchResult(TextSearchResponse textSearchResponse) {
                hMap.clear();
                StringBuilder response = new StringBuilder("\n");
                response.append("success\n");
                int count = 1;
                AddressDetail addressDetail;
                for (Site site :textSearchResponse.getSites()){
                    addressDetail = site.getAddress();
                    response.append(String.format(
                            "[%s]  name: %s, formatAddress: %s, country: %s, countryCode: %s \r\n",
                            "" + (count++),  site.getName(), site.getFormatAddress(),
                            (addressDetail == null ? "" : addressDetail.getCountry()),
                            (addressDetail == null ? "" : addressDetail.getCountryCode())));
                    hMap.addMarker(new MarkerOptions().position(new LatLng(site.getLocation().getLat(), site.getLocation().getLng())).title(site.getName()).snippet(site.getFormatAddress()));
                }
                Log.d("SEARCH RESULTS", "search result is : " + response);
            }

            @Override
            public void onSearchError(SearchStatus searchStatus) {
                Log.e("SEARCH RESULTS", "onSearchError is: " + searchStatus.getErrorCode());
            }
        });
    }

    public void getLastLocationAndAddress() {
        try {
            Task<Location> lastLocation = mFusedLocationProviderClient.getLastLocation();
            lastLocation.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location == null) {
                        Log.i(Constants.MAP_TAG, "getLastLocation onSuccess location is null");
                        return;
                    }
                    Log.i(Constants.MAP_TAG,"LatLng3 : " + location.getLongitude() + "," + location.getLatitude());
                    latLng3 = new LatLng(location.getLatitude(),location.getLongitude());
                    currentLat = location.getLatitude();
                    currentLng = location.getLongitude();
                    LatLngNew = new LatLng(currentLat,currentLng);

                    Geocoder geo = new Geocoder(getContext(), Locale.getDefault());
                    try {
                        List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        MyAdres = addresses.get(0).getAddressLine(0);
                        Log.e(Constants.MAP_TAG, "Go Get : :" + location.getLatitude() + " "  + location.getLongitude() + " " + addresses.get(0).getAdminArea() + " " + addresses.get(0).getCountryCode());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(),"Location error. Please check your network connection.",Toast.LENGTH_SHORT).show();
                    Log.e(Constants.MAP_TAG, "getLastLocation onFailure:" + e.getMessage());
                }
            });
        } catch (Exception e) {
            Toast.makeText(getContext(),"Location error. Please check your network connection.",Toast.LENGTH_SHORT).show();
            Log.e(Constants.MAP_TAG, "getLastLocation exception:" + e.getMessage());
        }
    }

    @Override
    public void onMapReady(HuaweiMap huaweiMap) {
        hMap = huaweiMap;
        hMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLngNew, 10));
        hMap.setMyLocationEnabled(true);
        hMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        search(currentLat,currentLng);
    }
}