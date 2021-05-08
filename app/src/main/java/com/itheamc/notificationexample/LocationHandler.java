package com.itheamc.notificationexample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationHandler {
    private static final String TAG = "LocationHandler";
    private static LocationHandler instance;
    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;

    // Constructor
    private LocationHandler(Context context) {
        this.context = context;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    // Instance
    public static LocationHandler getInstance(Context context) {
        if (instance == null) {
            instance = new LocationHandler(context);
        }

        return instance;
    }

    // Getting Current Location
    // Method -1
    @SuppressLint("MissingPermission")
    public void getLastLocation(TextView textView) {
        Log.d(TAG, "getCurrentLocation: This method is called");
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Log.d(TAG, "onSuccess: Location Accurracy is " + location.getAccuracy() + location.getProvider());
                            Log.d(TAG, "onSuccess: " + calcDistance(location.getLatitude(),
                                    location.getLongitude(), 27.8166961, 82.5179287));
                            textView.setText(String.valueOf(location.getLatitude()));
                        }
                    }
                });
    }

    /**
     * Creating function to request network update incase
     * getCurrentLocation() function unable to get user location
     */
    // Method -2
    @SuppressLint("MissingPermission")
    public void requestUserLocation(TextView textView) {
        LocationRequest locationRequest = LocationRequest.create()
                .setWaitForAccurateLocation(true)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(2000)
                .setFastestInterval(1000)
                .setNumUpdates(1);

        // Location Callback
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location gotLocation = locationResult.getLastLocation();
                Log.d(TAG, "onSuccess: Location Accurracy is " + gotLocation.getAccuracy() + "\n" + gotLocation.getProvider());
                Log.d(TAG, "onSuccess: " + calcDistance(gotLocation.getLatitude(),
                        gotLocation.getLongitude(), 27.8166961, 82.5179287));
                textView.setText(String.valueOf(gotLocation.getLatitude()));

            }
        };

        // Finally requesting to get user location
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }


    // Function to get location
    // Method -3
    @SuppressLint("MissingPermission")
    public void getCurrentLocation(TextView textView) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.d(TAG, "onLocationChanged: " + location.getAccuracy() + "   --" + location.getLatitude() + "   --" + location.getLongitude());
                textView.setText(String.valueOf(location.getLatitude()));
                Log.d(TAG, "onSuccess: " + calcDistance(location.getLatitude(),
                        location.getLongitude(), 27.8166961, 82.5179287));
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 20F, locationListener, Looper.getMainLooper());
    }



    // Creating method to calculate distance between two places
    private double calcDistance(double latitude1, double longitude1, double latitude2,
                                double longitude2) {
        double distance = 0;
        double latDistance = 0;
        double longDistance = 0;
        final double earthRadius = 6371;    // 6371 is for KM, Use 3956 for Miles

        // Now calculating distance between two points using Haversine formula
        latDistance = Math.toRadians(latitude2) - Math.toRadians(latitude1);
        longDistance = Math.toRadians(longitude2) - Math.toRadians(longitude1);

        // let declare a double variable tempCalc
        double tempCalc = Math.pow(Math.sin(latDistance / 2), 2)
                + Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2))
                * Math.pow(Math.sin(longDistance / 2), 2);

        // Now Calculating final distance
        distance = 2 * earthRadius * Math.asin(Math.sqrt(tempCalc));

        // Finally Returning distance
        return distance;
    }

    // method for debugging purpose only
    private String extractAddress(Address address) {
        return String.format("Country: %s\nLocality: %s\nSubLocality: %s\nAdminArea: %s\nSubAdminArea: %s\nAddressLine: %s\nPostalCode: %s\nFeatureName: %s\nPremises: %s\nLatitude: %s\nLongitude: %s",
                address.getCountryName(),
                address.getLocality(),
                address.getSubLocality(),
                address.getAdminArea(),
                address.getSubAdminArea(),
                address.getAddressLine(0),
                address.getPostalCode(),
                address.getFeatureName(),
                address.getPremises(),
                String.valueOf(address.getLatitude()),
                String.valueOf(address.getLongitude()));
    }


}
