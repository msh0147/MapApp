package com.example.mapapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.mapapp.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, AutoPermissionsListener {

    private GoogleMap mMap;
    MarkerOptions myLocationMarker;

    int[] mapTypes = {GoogleMap.MAP_TYPE_SATELLITE, GoogleMap.MAP_TYPE_NORMAL, GoogleMap.MAP_TYPE_TERRAIN};
    int mapType = GoogleMap.MAP_TYPE_SATELLITE;
    double[] lats = {35.1379222};
    double[] lngs = {129.05562775};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        AutoPermissions.Companion.loadAllPermissions(this,101);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0,1,0,"위성지도");
        menu.add(0,2,0,"일반지도");
        menu.add(0,3,0,"지형지도");
        menu.add(0,4,0,"부산시청");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 1 :
                mapType = mapTypes[0];
                break;
            case 2 :
                mapType = mapTypes[1];
                break;
            case 3 :
                mapType = mapTypes[2];
                break;
            case 4 :
                showCurrentLocation(lats[0], lngs[0]);
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        startLocationService();
        showCurrentLocation(35.156,129.0595);


        // Add a marker in Sydney and move the camera
//        LatLng myPoint = new LatLng(35.156, 129.0595);
//
//        mMap.addMarker(new MarkerOptions().position(myPoint).title("Marker in myPoint"));
//
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPoint,15));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    private void startLocationService(){
        LocationManager manager=(LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        try {
            Location location = manager.getLastKnownLocation(
                    LocationManager.GPS_PROVIDER);
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                showCurrentLocation(latitude,longitude);
            }
            GPSListener listener=new GPSListener();
            long minTime=10000;
            float minDistance=0;
            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTime, minDistance, listener);
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(this, requestCode,
                permissions, this); //
    }

    @Override
    public void onDenied(int i, String[] strings) {
        Log.i("permissions denied : ",strings.length+"");//
    }

    @Override
    public void onGranted(int i, String[] strings) {
        Log.i("permissions granted : ",strings.length+"");//
    }

    class GPSListener implements LocationListener{

        @Override
        public void onLocationChanged(@NonNull Location location) {
            Double latitude=location.getLatitude();
            Double longitude=location.getLongitude();
            showCurrentLocation(latitude, longitude);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(@NonNull String provider) {
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
        }
    }

    private void showCurrentLocation(Double latitude, Double longitude){
        LatLng curPoint=new LatLng(latitude, longitude);
        mMap.setMapType(mapType);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint,13));
        showMyLocationMark(curPoint);
    }

    private void showMyLocationMark(LatLng curPoint){
        if (myLocationMarker == null) {
            myLocationMarker = new MarkerOptions();
            myLocationMarker.position(curPoint);
            myLocationMarker.title("● 내 위치\n");
            myLocationMarker.snippet("● GPS로 확인한 위치");
            myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation));
            mMap.addMarker(myLocationMarker);
        } else {
            myLocationMarker.position(curPoint);
            mMap.addMarker(myLocationMarker);
        }
    }
}