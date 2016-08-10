package speciemongo.net.speciemongo.ui.activities;

import android.os.Bundle;

import speciemongo.net.speciemongo.R;

/**
 * A {@link SgActivity} enabling the user to log himself in to the app.
 */

public class SgActivityLogin extends SgActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set Content View
        this.setContentView(R.layout.sg_activity_login);

    }
}
