package com.smoothsys.qonsume_pos.Utilities;

import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;

public class RenderUtils {

    public static void renderBorder(int color, View view) {

        GradientDrawable border = new GradientDrawable();
        border.setStroke(1, color); //black border with full opacity
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(border);
        } else {
            view.setBackground(border);
        }

    }

}
