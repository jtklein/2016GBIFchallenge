package speciemongo.net.speciemongo;

import android.app.Application;
import android.os.StrictMode;

import com.instabug.library.IBGInvocationEvent;
import com.instabug.library.Instabug;
import com.mapbox.mapboxsdk.MapboxAccountManager;

/**
 * The {@link Application} implementation for the app.
 */
public class SgApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // If we are in debug mode,
        if(BuildConfig.DEBUG) {

            // Enable strict mode
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());

        }

        MapboxAccountManager.start(this, this.getString(R.string.keychain_mapbox_access_token));

        // TODO change handled failure logs to Instabug in beta
        // Initialize Instabug
        new Instabug.Builder(this, this.getString(R.string.keychain_instabug_start_token))
                .setInvocationEvent(IBGInvocationEvent.IBGInvocationEventShake)
                .setShouldShowIntroDialog(false)
                .setDebugEnabled(true)
                .build();

    }
}
