package minor.Project;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class User_Dashboard extends AppCompatActivity implements OnMapReadyCallback {

    private static final int FINE_PERMISSION_CODE = 1;
    private static final int LOCATION_UPDATE_INTERVAL = 5000; // 5 seconds

    private GoogleMap myMap;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private SearchView mapSearchView;
    private Marker driverMarker;
    private Polyline driverPath;
    private List<LatLng> pathPoints = new ArrayList<>();
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        mapSearchView = findViewById(R.id.mapSearch);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(User_Dashboard.this);

        // Search for a location
        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String location) {
                searchLocation(location);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_PERMISSION_CODE);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                if (myMap != null) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    myMap.addMarker(new MarkerOptions()
                            .position(userLocation)
                            .title("My Location"));
                    myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                }
            }
        });
    }


    private void searchLocation(String location) {
        Geocoder geocoder = new Geocoder(User_Dashboard.this);
        List<Address> addressList;
        try {
            addressList = geocoder.getFromLocationName(location, 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                myMap.addMarker(new MarkerOptions().position(latLng).title(location));
                myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show();
        }
    }


    private void fetchDriverLocation() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("DriverLocation");
        query.orderByDescending("timestamp"); // Get latest location
        query.getFirstInBackground((object, e) -> {
            if (e == null) {
                double latitude = object.getDouble("latitude");
                double longitude = object.getDouble("longitude");
                LatLng driverLocation = new LatLng(latitude, longitude);

                updateDriverMarker(driverLocation);
            } else {
                Log.e("User_Dashboard", "Failed to fetch driver location: " + e.getMessage());
            }
        });
    }


    private void updateDriverMarker(LatLng location) {
        if (driverMarker == null) {
            driverMarker = myMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title("Driver Location"));
        } else {
            driverMarker.setPosition(location);
        }


        pathPoints.add(location);


        if (driverPath != null) driverPath.remove();

        driverPath = myMap.addPolyline(new PolylineOptions()
                .addAll(pathPoints)
                .width(8)
                .color(Color.BLUE));

        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
    }


    private final Runnable fetchDriverLocationRunnable = new Runnable() {
        @Override
        public void run() {
            fetchDriverLocation();
            handler.postDelayed(this, LOCATION_UPDATE_INTERVAL);
        }
    };

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        myMap.getUiSettings().setZoomControlsEnabled(true);
        myMap.getUiSettings().setZoomGesturesEnabled(true);


        handler.post(fetchDriverLocationRunnable);

        if (currentLocation != null) {
            LatLng userLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            myMap.addMarker(new MarkerOptions().position(userLocation).title("My Location"));
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
        } else {
            Toast.makeText(this, "Failed to get current location", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(fetchDriverLocationRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(fetchDriverLocationRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(fetchDriverLocationRunnable);
        if (driverMarker != null) driverMarker.remove();
        if (driverPath != null) driverPath.remove();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Permission denied. Please enable location permission.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
