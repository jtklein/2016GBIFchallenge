package speciemongo.net.speciemongo.ui.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.loopj.android.http.*;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.*;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import speciemongo.net.speciemongo.R;
import speciemongo.net.speciemongo.ui.SgProgressDialog;
import speciemongo.net.speciemongo.utility.SgGbifRestClient;

/**
 *  The activity that shows the map element.
 */
public class SgActivityMap extends SgActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        ResultCallback<LocationSettingsResult>,
        LocationListener {

    /**
     * The {@link GoogleApiClient} instance
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * The {@link MapView} instance
     */
    private MapView mMapViewMain;

    /**
     * The {@link MapboxMap} instance
     */
    public MapboxMap mMap;

    /**
     * Boolean to track whether the app is already resolving an error onConnectionFailed
     */
    private boolean mResolvingError = false;

    /**
     * Key for the resolving error boolean
     */
    private static final String STATE_RESOLVING_ERROR = "resolving_error";

    /**
     * Request code to use when launching the resolution activity for onConnectionFailed
     */
    private static final int REQUEST_RESOLVE_ERROR = 1001;

    /**
     * Unique tag for the error dialog fragment, showing onConnectionFailed
     */
    private static final String DIALOG_ERROR = "dialog_error";

    /**
     * Boolean to track whether the app is already resolving changing the location settings
     */
    private boolean mResolvingSettings = false;

    /**
     * Key for the resolving checking settings error boolean
     */
    private static final String STATE_CHECKING_SETTINGS = "checking_settings";

    /**
     * Request code to use when checking the location settings
     */
    private static final int REQUEST_CHECK_SETTINGS = 1002;

    /**
     * Boolean to track whether the app is already listening to location updates
     */
    private Boolean mUpdatingLocation = false;

    /**
     * Key for the app is updating location boolean
     */
    private static final String STATE_LOCATION_UPDATES = "location_updates";

    /**
     * The last known {@link Location} on start
     */
    private Location mLastLocation;

    /**
     * The current {@link Location}
     */
    private Location mCurrentLocation;

    /**
     * Key for the current location
     */
    private static final String LOCATION_KEY = "last_location";

    /**
     * The request code for the CAMERA permission
     */
    private final static int MY_PERMISSIONS_REQUEST_CAMERA = 3344;

    /**
     * The {@link ProgressDialog} used
     */
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token only needs to be configured once in the app
        MapboxAccountManager.start(this, this.getString(R.string.keychain_mapbox_access_token));

        // Set Content View
        // This contains the MapView in XML and needs to be called after the account manager
        setContentView(R.layout.sg_activity_map);

        // Bind Views
        ButterKnife.bind(this);

        // Check if GPS is enabled and if not send user to the GSP settings
        // TODO Better solution would be to display a dialog and suggesting to go to the settings
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabledGPS = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabledGPS) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // Initializing MapView
        try {
            this.mMapViewMain = (MapView) findViewById(R.id.mapviewMain);
            this.mMapViewMain.onCreate(savedInstanceState);
            this.mMapViewMain.getMapAsync(new OnMapReadyCallback() {

                @Override
                public void onMapReady(MapboxMap mapboxMap) {
                    mMap = mapboxMap;

                    // Customize map with markers, polylines, etc.
                    // TODO disable scrolling
                }
            });

        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), e.getMessage());
        }



        // Save the resolving error boolean if present
        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

        // Save the resolving settings boolean if present
        mResolvingSettings = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_CHECKING_SETTINGS, false);

        // Update location data
        this.updateValuesFromBundle(savedInstanceState);

    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mUpdatingLocation from the Bundle
            if (savedInstanceState.keySet().contains(STATE_LOCATION_UPDATES)) {
                mUpdatingLocation = savedInstanceState.getBoolean(STATE_LOCATION_UPDATES);
            }

            // Update the value of mCurrentLocation from the Bundle
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocation is not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Connect to Google API client
        mGoogleApiClient.connect();
        Log.i(this.getClass().getSimpleName(), "Connected to Google API Client");
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Disconnect the Google API client
        mGoogleApiClient.disconnect();
        Log.i(this.getClass().getSimpleName(), "Disconnected from Google API Client");
    }


    @Override
    public void onResume() {
        super.onResume();

        // Add the MapView lifecycle to the activity's lifecycle methods
        this.mMapViewMain.onResume();

        // Connect to location API for location updates
        if (mGoogleApiClient.isConnected() && !mUpdatingLocation) {
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // Add the MapView lifecycle to the activity's lifecycle methods
        this.mMapViewMain.onPause();

        // Disconnect from location API updates
        this.stopLocationUpdates();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        // Add the MapView lifecycle to the activity's lifecycle methods
        this.mMapViewMain.onLowMemory();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Add the MapView lifecycle to the activity's lifecycle methods
        this.mMapViewMain.onDestroy();

        // Hide the camera progress dialog to prevent leaking
        this.hideProgressDialog();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Add the MapView lifecycle to the activity's lifecycle methods
        this.mMapViewMain.onSaveInstanceState(savedInstanceState);

        // Save the resolving error boolean in the activity's saved instance data
        savedInstanceState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);

        // Save the resolving error boolean in the activity's saved instance data
        savedInstanceState.putBoolean(STATE_CHECKING_SETTINGS, mResolvingSettings);

        // Save the location updates boolean in the activity's saved instance data
        savedInstanceState.putBoolean(STATE_LOCATION_UPDATES, mUpdatingLocation);

        // Save the current location to the activity's saved instance data
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(this.getClass().getSimpleName(), "Connected to Google Location API");

        // Get the last known location
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        // Move the map position to last known location
        if (mLastLocation != null) {
            Log.i(this.getClass().getSimpleName(), "Last known location was: " + mLastLocation.toString());

            // Move the map camera to where the user location is
            mMap.setCameraPosition(new CameraPosition.Builder()
                    .target(new LatLng(mLastLocation))
                    .build());
            mMap.setMyLocationEnabled(true);

            String queryGeometry = buildQueryGeometry(mLastLocation);
            queryOccurrence(queryGeometry);
        }

        // Create location request
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Get the current location settings of the user's device
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        // Check whether the current location settings are satisfied
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        // Set the callback for checking the location settings
        result.setResultCallback(this);

    }

    public String buildQueryGeometry(Location lastLocation) {

        double longitude = lastLocation.getLongitude();
        double latitude = lastLocation.getLatitude();
        double ad = 0.004;

        double[] polygonLongs = new double[2];
        double[] polygonLats = new double[2];

        polygonLongs[0] = longitude + ad;
        polygonLongs[1] = longitude - ad;

        polygonLats[0] = latitude + ad;
        polygonLats[1] = latitude - ad;

        // Build query string fo GBIF occurence search
        String queryGeometry = "geometry=POLYGON(("
                + longitude + " " + polygonLats[0] + ", "
                + polygonLongs[0] + " " + latitude + ", "
                + longitude + " " + polygonLats[1] + ", "
                + polygonLongs[1] + " " + latitude + ", "
                + longitude + " " + polygonLats[0]
                + "))";

        return queryGeometry;
    }

    private void queryOccurrence(String queryGeometry) {

        mMap.clear();

        // Make RESTful webservice call using AsyncHttpClient object
        SgGbifRestClient.get("occurrence/search?HAS_GEOSPATIAL_ISSUE=false&HAS_COORDINATE=true&" + queryGeometry,
                null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                // If the response is JSONObject instead of expected JSONArray

                try {
                    int numberOccurrences = response.getInt("count");

                    JSONArray results = response.getJSONArray("results");

                    for (int i = 0; i < numberOccurrences; i++) {
                        JSONObject dataPoint = results.getJSONObject(i);

                        double longitude = dataPoint.getDouble("decimalLongitude");
                        double latitude = dataPoint.getDouble("decimalLatitude");

                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude, longitude)));

                    }

                } catch (JSONException e) {
                    Log.e(this.getClass().getSimpleName(), e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                // Called when response HTTP status is "4XX" (eg. 401, 403, 404)

                // TODO something meaningful here
            }
        });
    }

    @Override
    public void onResult(LocationSettingsResult result) {
        // Get status
        final Status status = result.getStatus();
        Log.i(this.getClass().getSimpleName(), "LocationSettingsResult is: " + status.getStatusMessage());

        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                // All location settings are satisfied. Initialize location updates request.
                if (!mUpdatingLocation) {
                    this.startLocationUpdates();
                }

                break;

            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    mResolvingSettings = true;
                    status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);

                } catch (IntentSender.SendIntentException e) {
                    // Ignore the error.
                }
                break;

            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are not satisfied. However, we have no way
                // to fix the settings so we won't show the dialog.
                // TODO explain to user
                break;
        }
    }

    /**
     * Start listening to location updates from the location API
     */
    protected void startLocationUpdates() {
        Log.i(this.getClass().getSimpleName(), "Starting location updates");

        // Create location request
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        mUpdatingLocation = true;

    }

    @Override
    public void onLocationChanged(Location location) {
        // Store the new location
        mCurrentLocation = location;

        Log.i(this.getClass().getSimpleName(), "Latitude is: " + String.valueOf(mCurrentLocation.getLatitude()));
        Log.i(this.getClass().getSimpleName(), "Longitude is: " + String.valueOf(mCurrentLocation.getLongitude()));

        if (mCurrentLocation != null) {
            // Move the map camera to where the user location is
            mMap.setCameraPosition(new CameraPosition.Builder()
                    .target(new LatLng(location))
                    .build());

            // TODO update current location layer on map

        }

        // TODO do this on a separate thread or updating of location is blocked
        String queryGeometry = buildQueryGeometry(mCurrentLocation);
        queryOccurrence(queryGeometry);

    }

    /**
     * Stop listening to location updates from the location API
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mUpdatingLocation = false;
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(this.getClass().getSimpleName(), "Connection to Google Location API suspended");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.i(this.getClass().getSimpleName(), "Connection to Google Location API failed. Result is: " + result.getErrorMessage());
        // An error has occurred and a connection to Google Location API
        // could not be established. Display an error message.
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;

        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);

            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Create a fragment for the error dialog
            ErrorDialogFragment dialogFragment = new ErrorDialogFragment();

            // Pass the error that should be displayed and show the dialog
            Bundle args = new Bundle();
            args.putInt(DIALOG_ERROR, result.getErrorCode());
            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(), "errordialog");

            mResolvingError = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the request code was from onConnectionFailed to the location API client
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            // If the error is resolved
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect, then connect
                if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }

        // If the request code was from checking the location settings
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            mResolvingSettings = false;
            // If the error is resolved
            if (resultCode == RESULT_OK) {
                // All location settings are satisfied. Initialize location updates request.
                if (!mUpdatingLocation) {
                    this.startLocationUpdates();
                }
            }
        }
    }

    /**
     * A fragment to display an error dialog
     */
    public static class ErrorDialogFragment extends DialogFragment {

        // Create an instance
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((SgActivityMap) getActivity()).onDialogDismissed();
        }
    }

    /**
     * Called from ErrorDialogFragment when the dialog is dismissed
     */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    @OnClick(R.id.buttonStartCamera)
    public void onCameraClicked() {

        // If the user did not grant camera permission, request it
        if (!this.hasCameraPermission()) {
            this.requestCameraPermissions();
        }

        if (this.hasCameraPermission()) {
            this.startCamera();

        }

        // TODO do something wihout permission
    }

    /**
     * Checks if the app has the CAMERA permission
     * @return true if permission is granted
     */
    public Boolean hasCameraPermission() {
        // Check if we have camera access
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            return true;

        } else {
            return false;

        }
    }

    /**
     * Requests the CAMERA permission at runtime
     */
    public void requestCameraPermissions() {
        // Request the permission.
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                MY_PERMISSIONS_REQUEST_CAMERA);

    }

    /**
     * Starts the camera activity
     */
    public void startCamera() {
        Intent i = new Intent(this, SgActivityCamera.class);
        this.startActivity(i);
        this.finish();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        // The results of the asking for permission dialogue
        switch (requestCode) {
            // Callback is from the CAMERA permission
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // CAMERA permission was granted
                    // nothing to do here

                } else {

                    // CAMERA permission denied
                    // TODO show explanation why we need it
                }
                return;
            }
        }
    }

    /**
     * Shows the progress dialog
     */
    private void showProgressDialog() {
        // If no dialog was yet created, do so
        if(this.mProgressDialog == null) {
            this.mProgressDialog = new SgProgressDialog(SgActivityMap.this);
        }

        // Show the dialog
        this.mProgressDialog.show();

    }

    /**
     * Hides the progress dialog if set
     */
    private void hideProgressDialog() {
        // Dismiss progress dialog if set
        if(this.mProgressDialog != null) {
            this.mProgressDialog.dismiss();

        }
    }
}
