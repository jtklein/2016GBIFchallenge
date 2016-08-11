package speciemongo.net.speciemongo.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import speciemongo.net.speciemongo.R;

public class SgActivityMap extends SgActivityMain {

    MapView mMapViewMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token only needs to be configured once in the app
        MapboxAccountManager.start(this, this.getString(R.string.keychain_mapbox_access_token));

        // Set Content View
        // This contains the MapView in XML and needs to be called after the account manager
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

    }
}
