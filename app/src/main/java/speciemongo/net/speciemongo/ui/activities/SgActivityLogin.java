package speciemongo.net.speciemongo.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set Content View
        this.setContentView(R.layout.sg_activity_login);

        // Bind Views
        ButterKnife.bind(this);

    }

    @OnClick(R.id.buttonLogin)
    public void onLoginClicked() {
        // Get email
        String email = this.mEditTextEmail.getText().toString();

        // TODO create profile

        Intent i = new Intent(this, SgActivityMain.class);
        this.startActivity(i);
        this.finish();

    }
}
