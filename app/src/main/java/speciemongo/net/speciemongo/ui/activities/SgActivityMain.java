package speciemongo.net.speciemongo.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;
import speciemongo.net.speciemongo.R;

public class SgActivityMain extends SgActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set Content View
        setContentView(R.layout.sg_activity_main);

        // Bind Views
        ButterKnife.bind(this);

    }

    @OnClick(R.id.buttonDummyToMap)
    public void onDummyToMapClicked() {

        // If the user did not grant Location permissions request them
        if (!this.hasLocationPermissions()) {
            this.requestLocationPermissions();
        }

        if (this.hasLocationPermissions()) {
            Intent i = new Intent(this, SgActivityMap.class);
            this.startActivity(i);
            this.finish();

        }
    }

    public Boolean hasLocationPermissions() {
        return true;

    }

    public void requestLocationPermissions() {


    }
}
