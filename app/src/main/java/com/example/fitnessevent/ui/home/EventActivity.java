package com.example.fitnessevent.ui.home;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitnessevent.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class EventActivity extends AppCompatActivity implements OnMapReadyCallback{
    private GoogleMap mMap;
    private LatLng location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        supportMapFragment.getMapAsync(this);



}
//    public double Location(double lat, double lng) {
//        return 0;
//    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        // map object: googlemap
        mMap = googleMap;

        //纬度
        double Lat = 42.3;
        //经度
        double Lng = -71.1;
        location = new LatLng(Lat,Lng);

        mMap.addMarker(new MarkerOptions().position(location).title("Marathon Sports"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,12));
        //click and display marker
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){

            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });
        //Zoom in/out
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        //Scroll the map
        mMap.getUiSettings().setScrollGesturesEnabled(true);








    }
}
