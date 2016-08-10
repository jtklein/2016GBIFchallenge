package speciemongo.net.speciemongo.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import speciemongo.net.speciemongo.R;

public class SgActivityMap extends SgActivityMain {

    MapView mMapViewMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set Content View
        setContentView(R.layout.sg_activity_map);

    }
}
