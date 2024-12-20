package com.qboxus.binder.GoogleMap;

import java.util.ArrayList;

/**
 * Created by qboxus on 3/13/2018.
 */

interface SavedPlaceListener {
    void onSavedPlaceClick(ArrayList<SavedAddressModel> mResultList, int position);
}
