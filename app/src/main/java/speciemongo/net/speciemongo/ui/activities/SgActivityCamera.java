package speciemongo.net.speciemongo.ui.activities;

import android.os.Bundle;

import speciemongo.net.speciemongo.R;
import speciemongo.net.speciemongo.ui.fragments.camera.SgFragmentCamera;

/**
 * The activity that shows the camera element.
 */
public class SgActivityCamera extends SgActivityMain {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sg_activity_camera);

        // TODO add functionality for Android versions 15-19

        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.cameraContainer, SgFragmentCamera.newInstance())
                    .commit();
        }
    }


}
