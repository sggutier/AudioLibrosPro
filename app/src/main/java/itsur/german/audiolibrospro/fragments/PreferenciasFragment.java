package itsur.german.audiolibrospro.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import itsur.german.audiolibrospro.MainActivity;
import itsur.german.audiolibrospro.R;

public class PreferenciasFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume(){
        SelectorFragment selectorFragment = (SelectorFragment) getFragmentManager().findFragmentById(R.id.recycler_view);
        if (selectorFragment == null ) {
            ((MainActivity) getActivity()).mostrarElementos(false);
        }
        super.onResume();
    }
}
