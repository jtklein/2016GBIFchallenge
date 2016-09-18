package speciemongo.net.speciemongo.ui;

import android.app.ProgressDialog;
import android.content.Context;

import speciemongo.net.speciemongo.R;

/**
 * A {@link ProgressDialog} that sets the default message and style.
 */
public class SgProgressDialog extends ProgressDialog {

    public SgProgressDialog(Context context) {
        // use the constructor with theme
        this(context, 0);

    }

    public SgProgressDialog(Context context, int theme) {
        super(context, theme);
        this.setMessage(this.getContext().getString(R.string.ui_loading));
        this.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.setCancelable(false);

    }

    @Override
    public void show() {
        super.show();

    }
}
