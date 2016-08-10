package speciemongo.net.speciemongo.ui.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import speciemongo.net.speciemongo.R;

/**
 * A {@link SgActivity} enabling the user to log himself in to the app.
 */

public class SgActivityLogin extends SgActivity {

    /**
     * The {@link android.widget.EditText} for the email
     */
    @Bind(R.id.editTextEmail)
    TextView mEditTextEmail;

    @Bind(R.id.buttonLogin)
    Button mButtonLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set Content View
        this.setContentView(R.layout.sg_activity_login);

        // Bind Views
        ButterKnife.bind(this);

    }
}
