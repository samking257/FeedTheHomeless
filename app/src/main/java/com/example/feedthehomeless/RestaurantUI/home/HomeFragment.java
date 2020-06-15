package com.example.feedthehomeless.RestaurantUI.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.feedthehomeless.R;
import com.example.feedthehomeless.ShelterInfoActivity;
import com.example.feedthehomeless.User;
import com.example.feedthehomeless.UserMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private GoogleApiClient APIClient;
    private LocationRequest locationRequest;
    private Location lastKnownLocation;
    private Marker currentLocation;
    private static final int requestUserLocationCode = 99;
    private FirebaseFirestore mDatabase;
    UserMapper userMapper = new UserMapper();
    Map<String, User> shelterData = new HashMap<>();
    Map<String, String> UIDname = new HashMap<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.restaurant_home, container, false);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.restaurantMap);
        supportMapFragment.getMapAsync(this);
        mDatabase = FirebaseFirestore.getInstance();
        getShelters();
        return root;
    }

    private void getShelters() {
        mDatabase.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Map<String, Object> docData;
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                docData = doc.getData();
                                String accountType = docData.get("accountType").toString();
                                String activated = docData.get("activated").toString();
                                if(accountType.equals("Shelter") && activated.equals("true")){
                                    User user = new User();
                                    shelterData.put(doc.getId(), userMapper.UserMapper(docData, user));
                                }
                            }
                            setShelterMarkers();
                        } else{
                            Log.d("MyTag", "Error getting documents", task.getException());
                        }
                    }
                });
    }

    private void setShelterMarkers() {
        for(Map.Entry<String, User> shelter : shelterData.entrySet()){
            User user = shelter.getValue();
            MarkerOptions markerOptions = new MarkerOptions();

            LatLng latLng = new LatLng(user.latLng.getLatitude(), user.latLng.getLongitude());
            markerOptions.position(latLng);
            markerOptions.title(user.companyName);
            int colour = 63;
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(colour));

            UIDname.put(user.companyName, shelter.getKey());
            mMap.addMarker(markerOptions);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            buildAPIClient();
            mMap.setMyLocationEnabled(true);
        }
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case requestUserLocationCode:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        if(APIClient == null){
                            buildAPIClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }else{
                    Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }return;
        }
    }

    protected synchronized void buildAPIClient(){
        APIClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        APIClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        lastKnownLocation = location;

        if(currentLocation != null){
            currentLocation.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        if(APIClient != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(APIClient, this);

        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(APIClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        String uid = UIDname.get(marker.getTitle());
        Intent intent = new Intent(getActivity(), ShelterInfoActivity.class);
        intent.putExtra("SHELTER_UID", uid);
        startActivity(intent);
    }
}