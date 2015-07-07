package com.majeur.fixedlistview;

import android.support.annotation.NonNull;
import android.view.View;

public interface OnItemClickListener {

    public void onItemClick(@NonNull FixedListView parent, @NonNull View v, int position);
}
