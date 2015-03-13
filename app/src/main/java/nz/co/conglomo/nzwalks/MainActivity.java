/*
 * Copyright (c) 2015 Peter Chapman.
 * Please see the file LICENCE.md for licence details.
 */
package nz.co.conglomo.nzwalks;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * The main activity.
 */
public class MainActivity extends FragmentActivity {

    /**
     * The map.
     */
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    /**
     * The tile layer.
     */
    private final TileProvider tileProvider = new UrlTileProvider(256, 256) {
        @Override
        public URL getTileUrl(int x, int y, int zoom) {

            /* Define the URL pattern for the tile images */
            String s = String.format("https://nzwalks.azurewebsites.net/geoserver/gwc/service/gmaps?zoom=%d&x=%d&y=%d&format=image/png&layers=doc_tracks:doc-tracks&srs=EPSG:3857", zoom, x, y);
            try {
                return new URL(s);
            } catch (MalformedURLException e) {
                throw new AssertionError(e);
            }
        }
    };
    /**
     * The on create event handler.
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        this.setUpMapIfNeeded();
    }

    /**
     * The on resume event handler.
     */
    @Override
    protected void onResume() {
        super.onResume();
        this.setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        // Set up the default position
        LatLng ll = new LatLng(-39.2833, 175.566);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 10));

        // Add the tile layer
        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));

        // Enable widgets
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        // Get LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Get Current Location
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {

            // Get the latitude and longitude
            ll = new LatLng(location.getLatitude(), location.getLongitude());

            // Make sure the position is in New Zealand
            if ((ll.longitude > 160 || ll.longitude < -172) && ll.latitude > -53 && ll.latitude < -30)
            {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 10));
            }

        }
    }
}
