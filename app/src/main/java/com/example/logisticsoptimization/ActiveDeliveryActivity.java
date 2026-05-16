package com.example.logisticsoptimization;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import com.google.android.gms.location.LocationRequest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ActiveDeliveryActivity extends BaseActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private String pickupAddress;
    private String deliveryAddress;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Marker currentLocationMarker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_active_delivery);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        pickupAddress = getIntent().getStringExtra("pickupAddress");
        deliveryAddress = getIntent().getStringExtra("deliveryAddress");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)
                .setFastestInterval(3000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    updateCurrentLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        showRoute();
    }
    private boolean firstLocationUpdate = true;

    private void updateCurrentLocation(LatLng latLng) {

        if (currentLocationMarker == null) {
            // primul marker - pozitia curenta
            currentLocationMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("You are here")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            if (firstLocationUpdate) {
                firstLocationUpdate = false;
                geocodeAddress(pickupAddress, pickupLatLng -> {
                    drawRoute(latLng, pickupLatLng, 0xFF00FF00);
                });
            }

        } else {
            // actualizeaza pozitia
            currentLocationMarker.setPosition(latLng);
        }

        // muta camera sa urmareasca soferul

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

    private void showRoute() {


        geocodeAddress(pickupAddress, pickupLatLng -> {
            Log.d("MAP", "Pickup geocoded: " + pickupLatLng);

            geocodeAddress(deliveryAddress, deliveryLatLng -> {
                Log.d("MAP", "Delivery geocoded: " + deliveryLatLng);



                mMap.addMarker(new MarkerOptions()
                        .position(pickupLatLng)
                        .title("Pickup: " + pickupAddress));

                mMap.addMarker(new MarkerOptions()
                        .position(deliveryLatLng)
                        .title("Delivery: " + deliveryAddress));

                LatLngBounds bounds = new LatLngBounds.Builder()
                        .include(pickupLatLng)
                        .include(deliveryLatLng)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));



                //pickup → delivery
                drawRoute(pickupLatLng, deliveryLatLng, 0xFF0066FF);


            });
        });

    }

    private void drawRoute(LatLng origin, LatLng destination, int color) {
        String url = "https://maps.googleapis.com/maps/api/directions/json?"
                + "origin=" + origin.latitude + "," + origin.longitude
                + "&destination=" + destination.latitude + "," + destination.longitude
                + "&key=AIzaSyAFs2HB2OdG2X7-HbZNjkQ3HXbFrGm7IAE";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONArray routes = response.getJSONArray("routes");
                        if (routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0);

                            // distanta si durata
                            JSONObject leg = route.getJSONArray("legs").getJSONObject(0);
                            String distance = leg.getJSONObject("distance").getString("text");
                            String duration = leg.getJSONObject("duration").getString("text");

                            Log.d("ROUTE", "Distanta: " + distance + ", Durata: " + duration);

                            // desenezi polyline pe harta
                            String encodedPolyline = route.getJSONObject("overview_polyline").getString("points");
                            List<LatLng> points = PolyUtil.decode(encodedPolyline);
                            mMap.addPolyline(new PolylineOptions()
                                    .addAll(points)
                                    .width(8)
                                    .color(color));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Eroare la calculul rutei!", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }


private void geocodeAddress(String address, OnGeocodedListener listener) {
    String url = "https://maps.googleapis.com/maps/api/geocode/json?address="
            + Uri.encode(address)
            + "&key=AIzaSyAFs2HB2OdG2X7-HbZNjkQ3HXbFrGm7IAE";

    Log.d("MAP", "Geocoding URL: " + url);

    JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            response -> {
                Log.d("MAP", "Geocoding response: " + response.toString());
                try {
                    JSONArray results = response.getJSONArray("results");
                    if (results.length() > 0) {
                        JSONObject location = results.getJSONObject(0)
                                .getJSONObject("geometry")
                                .getJSONObject("location");
                        double lat = location.getDouble("lat");
                        double lng = location.getDouble("lng");
                        listener.onGeocoded(new LatLng(lat, lng));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            },
            error -> {
                Log.e("MAP", "Geocoding error: " + error.toString());
                Toast.makeText(this, "Eroare geocoding!", Toast.LENGTH_SHORT).show();
            }
    );

    Volley.newRequestQueue(this).add(request);
}

    public interface OnGeocodedListener {
        void onGeocoded(LatLng latLng);
    }
}
