package com.qboxus.binder.interfaces;

import android.os.Bundle;

import java.io.Serializable;

/**
 * Created by qboxus on 4/4/2019.
 */

public interface FragmentCallback extends Serializable {
    void responce(Bundle bundle);
}
