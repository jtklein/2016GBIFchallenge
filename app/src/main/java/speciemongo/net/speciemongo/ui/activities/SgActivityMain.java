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
        Intent i = new Intent(this, SgActivityMap.class);
        this.startActivity(i);
        this.finish();

    }
}
