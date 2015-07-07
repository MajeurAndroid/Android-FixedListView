package com.majeur.fixedlistview;

import android.support.annotation.NonNull;
import android.view.View;

public interface OnItemLongClickListener {

    public boolean onItemLongClick(@NonNull FixedListView parent, @NonNull View view, int position);
}
