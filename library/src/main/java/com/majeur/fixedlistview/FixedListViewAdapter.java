package com.majeur.fixedlistview;

import android.support.annotation.NonNull;
import android.view.View;

public interface FixedListViewAdapter {

    public int getCount();

    public View getView(@NonNull FixedListView parent, int position);

}
