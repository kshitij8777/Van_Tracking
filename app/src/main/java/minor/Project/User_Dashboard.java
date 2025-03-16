package minor.Project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class User_Dashboard extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView etaTextView, distanceTextView;
    private GeoApiContext geoApiContext;
    private Handler handler = new Handler();
    private Runnable fetchDriverLocationRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize ETA and Distance TextViews
        etaTextView = findViewById(R.id.etaTextView);
        distanceTextView = findViewById(R.id.distanceTextView);


        geoApiContext = new GeoApiContext.Builder()
                .apiKey("AIzaSyDOkTAiAizujL3lhnM0XO51y7Rq6Ztydcg")
                .build();

        loadMap();


        startFetchingDriverLocation();


        setupBottomNavigation();
    }


    private void loadMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Enable zoom controls and gestures
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
            getCurrentLocation();

        } else {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("Your Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

                    // Fetch driver's location from backend
                    fetchDriverLocation(currentLocation);
                } else {
                    Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void fetchDriverLocation(LatLng userLocation) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("DriverLocation");
        query.orderByDescending("timestamp"); // Get the latest location
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects != null && !objects.isEmpty()) {
                    ParseObject driverLocation = objects.get(0);
                    double latitude = driverLocation.getDouble("latitude");
                    double longitude = driverLocation.getDouble("longitude");
                    LatLng driverLatLng = new LatLng(latitude, longitude);


                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Driver Location"));

                    // Draw a route between user and driver
                    drawRoute(userLocation, driverLatLng);

                    // Calculate ETA and distance
                    calculateETAAndDistance(userLocation, driverLatLng);
                } else {
                    Toast.makeText(User_Dashboard.this, "Driver location not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void drawRoute(LatLng origin, LatLng destination) {
        try {
            DirectionsResult result = DirectionsApi.newRequest(geoApiContext)
                    .mode(TravelMode.DRIVING)
                    .origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                    .destination(new com.google.maps.model.LatLng(destination.latitude, destination.longitude))
                    .await();

            if (result.routes != null && result.routes.length > 0) {
                PolylineOptions polylineOptions = new PolylineOptions();
                for (com.google.maps.model.LatLng point : result.routes[0].overviewPolyline.decodePath()) {
                    polylineOptions.add(new LatLng(point.lat, point.lng));
                }
                mMap.addPolyline(polylineOptions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Calculate ETA and distance
    private void calculateETAAndDistance(LatLng origin, LatLng destination) {
        try {
            DirectionsResult result = DirectionsApi.newRequest(geoApiContext)
                    .mode(TravelMode.DRIVING)
                    .origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                    .destination(new com.google.maps.model.LatLng(destination.latitude, destination.longitude))
                    .await();

            if (result.routes != null && result.routes.length > 0) {
                String eta = result.routes[0].legs[0].duration.humanReadable;
                String distance = result.routes[0].legs[0].distance.humanReadable;

                // Update ETA and Distance TextViews
                etaTextView.setText("ETA: " + eta);
                distanceTextView.setText("Distance: " + distance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Start fetching driver's location periodically
    private void startFetchingDriverLocation() {
        fetchDriverLocationRunnable = new Runnable() {
            @Override
            public void run() {
                if (mMap != null) {
                    getCurrentLocation();
                }
                handler.postDelayed(this, 10000);
            }
        };
        handler.post(fetchDriverLocationRunnable);
    }


    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                // Handle Home click
                Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.profile) {
                // Navigate to Profile activity
                Intent profileIntent = new Intent(User_Dashboard.this, User_Profile.class);
                startActivity(profileIntent);
                return true;
            } else if (itemId == R.id.Notification) {
                // Navigate to Notification activity
                Intent notificationIntent = new Intent(User_Dashboard.this, Notification.class);
                startActivity(notificationIntent);
                return true;
            } else if (itemId == R.id.settings) {
                // Navigate to Settings activity
                Intent settingsIntent = new Intent(User_Dashboard.this, Settings.class);
                startActivity(settingsIntent);
                return true;
            }

            return false;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(fetchDriverLocationRunnable);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}