package com.example.apt_library;

import android.app.Activity;
import android.view.View;

public class ActivityViewFinder implements ViewFinder {
    @Override
    public View findView(Object object, int id) {
        return ((Activity)object).findViewById(id);
    }
}
