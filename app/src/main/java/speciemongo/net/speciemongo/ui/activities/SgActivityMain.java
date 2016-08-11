package speciemongo.net.speciemongo.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import speciemongo.net.speciemongo.R;

public class SgActivityMain extends SgActivity {

    /**
     * The request code for the ACCESS_FINE_LOCATION permission
     */
    private final static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1122;

    /**
     * The request code for the ACCESS_COARSE_LOCATION permission
     */
    private final static int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 2233;

    /**
     * The {@link Fragment} that links to the profile activity
     */
    @Bind(R.id.cardViewProfile)
    CardView mFragmentProfile;

    /**
     * The {@link Fragment} that links to the explore activity
     */
    @Bind(R.id.cardViewExplore)
    CardView mFragmentExplore;

    /**
     * The {@link Fragment} that links to the Speciesdex activity
     */
    @Bind(R.id.cardViewSpeciesdex)
    CardView mFragmentSpeciesdex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set Content View
        setContentView(R.layout.sg_activity_main);

        // Bind Views
        ButterKnife.bind(this);

    }

    @OnClick(R.id.cardViewExplore)
    public void onExploreClicked() {

        // If the user did not grant Fine Location permission, request it
        if (!this.hasFineLocationPermission()) {
            this.requestFineLocationPermissions();
        }
        // If the user did not grant Coarse Location permission, request it
        if (!this.hasCoarseLocationPermission()) {
            this.requestCoarseLocationPermissions();
        }

        if (this.hasFineLocationPermission() && this.hasCoarseLocationPermission()) {
            this.startMap();

        }

        // TODO do something wihout permission
    }

    /**
     * Starts the map activity
     */
    public void startMap() {
        Intent i = new Intent(this, SgActivityMap.class);
        this.startActivity(i);
        this.finish();

    }

    /**
     * Checks if the app has the ACCESS_FINE_LOCATION permission
     * @return true if permission is granted
     */
    public Boolean hasFineLocationPermission() {
        // Check if we have fine location access
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            return true;

        } else {
            return false;

        }
    }

    /**
     * Checks if the app has the ACCESS_COARSE_LOCATION permission
     * @return true if permission is granted
     */
    public Boolean hasCoarseLocationPermission() {
        // Check if we have fine location access
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            return true;

        } else {
            return false;

        }
    }

    /**
     * Requests the ACCESS_FINE_LOCATION permission at runtime
     */
    public void requestFineLocationPermissions() {
        // Request the permission.
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

    }

    /**
     * Requests the ACCESS_COARSE_LOCATION permission at runtime
     */
    public void requestCoarseLocationPermissions() {
        // Request the permission.
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        // The results of the asking for permission dialogue
        switch (requestCode) {
            // Callback is from the ACCESS_FINE_LOCATION permission
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // ACCESS_FINE_LOCATION permission was granted
                    // nothing to do here

                } else {

                    // ACCESS_FINE_LOCATION permission denied
                    // TODO show explanation why we need it
                }
                return;
            }
            // Callback is from the ACCESS_COARSE_LOCATION permission
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // ACCESS_COARSE_LOCATION permission was granted
                    // start the map activity
                    // TODO enough for MVP under expected UI flow, future: make sure only one dialog, control async permission grant
                    this.startMap();

                } else {

                    // ACCESS_COARSE_LOCATION permission denied
                    // TODO show explanation why we need it
                }
                return;
            }
        }
    }
}
