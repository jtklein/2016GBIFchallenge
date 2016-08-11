package speciemongo.net.speciemongo.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
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

        // Initializing NavigationView
        try {
            this.mMapViewMain = (MapView) findViewById(R.id.mapviewMain);
            this.mMapViewMain.onCreate(savedInstanceState);
            this.mMapViewMain.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(MapboxMap mapboxMap) {

                    // Customize map with markers, polylines, etc.

                }
            });

        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), e.getMessage());
        }
    }

    // Add the MapView lifecycle to the activity's lifecycle methods
    @Override
    public void onResume() {
        super.onResume();
        this.mMapViewMain.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.mMapViewMain.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        this.mMapViewMain.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mMapViewMain.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.mMapViewMain.onSaveInstanceState(outState);
    }
}
