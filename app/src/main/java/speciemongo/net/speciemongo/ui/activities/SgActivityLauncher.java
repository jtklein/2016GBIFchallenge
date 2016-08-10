package speciemongo.net.speciemongo.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * The entry {@link AppCompatActivity} of the app. The user is forwarded either to the login screen
 * or the main activity based on if he is logged in or not.
 */

// TODO This class pre-loads the profile repository to make the decision.

public class SgActivityLauncher extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create an intent
        Intent i;
        i = new Intent(this, SgActivityLogin.class);

        // TODO If the user is not logged in, go to login, else to main

        /*
        if(!this.isLoggedIn()) {
            i = new Intent(this, SgActivityLogin.class);

        }

        If the user is logged in, go to main
        else {
            i = new Intent(this, SgActivityMain.class);

        }
        */

        // Start the activity, and finish this
        this.startActivity(i);
        this.finish();

    }
}
