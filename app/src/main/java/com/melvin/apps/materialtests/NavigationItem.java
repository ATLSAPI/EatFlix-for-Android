package com.melvin.apps.materialtests;

import android.graphics.drawable.Drawable;
import android.media.Image;

/**
 * Created by melvin on 24/10/2014.
 */
public class NavigationItem {
    private String mText;
    private Drawable mDrawable;
    //private Image mImage;

    public NavigationItem(String text, Drawable drawable) {
        mText = text;
        mDrawable = drawable;
        //mImage = image;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    public void setDrawable(Drawable drawable) {
        mDrawable = drawable;
    }
}
