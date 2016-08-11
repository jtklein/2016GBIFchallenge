package speciemongo.net.speciemongo.ui.fragments.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import speciemongo.net.speciemongo.R;
import speciemongo.net.speciemongo.ui.fragments.SgFragment;

/**
 * Created by johannesklein on 11.08.16.
 */

public class SgFragmentMainProfile extends SgFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.sg_fragment_main_profile, container, false);
    }
}
