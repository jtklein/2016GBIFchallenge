package speciemongo.net.speciemongo.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import speciemongo.net.speciemongo.R;

public class SgActivityMap extends SgActivityMain {

    MapView mMapViewMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If the user did not grant Location permissions request them
        if (!this.hasLocationPermissions()) {
            this.requestLocationPermissions();
        }

        // Set Content View
        setContentView(R.layout.sg_activity_map);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public Boolean hasLocationPermissions() {

    }

    public void requestLocationPermissions() {


    }
}
