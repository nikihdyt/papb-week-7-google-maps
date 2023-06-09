package com.example.android.google_maps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.android.google_maps.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.PointOfInterest;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private static final String TAG = MapsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // resize bitmap for marker
        int width = 100;
        int height = 100;
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.geography);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false);

        // set maps style
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }


        // Add a marker in GSP UGM and move the camera
        LatLng gsp = new LatLng(-7.7651543, 110.3666396);
        mMap.addMarker(new MarkerOptions().
                position(gsp).
                title("Marker in GSP UGM").
                icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gsp, 20));

        setMapLongClick(mMap);
        setPoiClick(mMap);
        enableMyLocation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Change the map type based on the user's selection.
        switch (item.getItemId()) {
            case R.id.normal_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.hybrid_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.satellite_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.terrain_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setMapLongClick(final GoogleMap map) {
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NotNull LatLng latLng) {
                // A Snippet is Additional text that's displayed below the title.
                String snippet = String.format(Locale.getDefault(),
                        "Lat: %1$.5f, Long: %2$.5f",
                        latLng.latitude,
                        latLng.longitude);

                // load custom marker image from resources
                BitmapDescriptor icMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker);

                // Add a marker for the user-created location. Add the snippet.
                map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Dropped Pin")
                        .snippet(snippet)
                        .icon(icMarker));
            }
        });
    }

    private void setPoiClick(final GoogleMap map) {
        map.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(@NotNull PointOfInterest pointOfInterest) {
                // load custom marker image from resources
                BitmapDescriptor icMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin);

                // Create a new marker for the POI that was clicked, and add it to the map.
                Marker poiMarker = map.addMarker(new MarkerOptions()
                        .position(pointOfInterest.latLng)
                        .title(pointOfInterest.name)
                        .icon(icMarker));
                poiMarker.showInfoWindow();
            }
        });
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation();
                    break;
                }
            }
        }
    }
}