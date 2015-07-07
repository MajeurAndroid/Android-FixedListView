package com.majeur.fixedlistview;

import android.os.Build;

class Utils {

    public static boolean isApi17() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    public static boolean checkFlag(int value, int flagToCheck) {
        return (value & flagToCheck) == flagToCheck;
    }
}
