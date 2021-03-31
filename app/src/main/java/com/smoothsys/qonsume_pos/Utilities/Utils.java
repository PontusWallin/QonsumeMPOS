package com.smoothsys.qonsume_pos.Utilities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.smoothsys.qonsume_pos.FragmentsAndActivities.MainActivity;
import com.smoothsys.qonsume_pos.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static MainActivity activity;
    private static Typeface fromAsset;
    private static SpannableString spannableString;
    private static Fonts currentTypeface;

    public Utils(Context context){
        this.activity = (MainActivity) context;
    }

    // == UI Utilities ==
    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public static int pxToDp(int px) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static int dpToPx(int dp) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static Point getScreenSize(){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        try {
            display.getSize(size);
        }catch (NoSuchMethodError e) {
            // For lower than api 11
            size.x = display.getWidth();
            size.y = display.getHeight();
        }
        return size;
    }

    public static int getScreenHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;
    }

    public static int getScreenWidth() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.widthPixels;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    // == Assets Utils ==
    public static void saveBitmapImage(Context context, Bitmap b, String picName){
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(picName, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        }
        catch (FileNotFoundException e) {
            Log.d("Smoothsys", "file not found");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.d("Smoothsys", "io exception");
            e.printStackTrace();
        }

    }

    public static Bitmap loadBitmapImage(Context context, String picName){
        Bitmap b = null;
        FileInputStream fis;
        try {
            fis = context.openFileInput(picName);
            b = BitmapFactory.decodeStream(fis);
            fis.close();

        }
        catch (FileNotFoundException e) {
            Log.d("Smoothsys", "file not found");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.d("Smoothsys", "io exception");
            e.printStackTrace();
        }
        return b;
    }

    public static Typeface getTypeFace(Fonts fonts) {

        if(currentTypeface == fonts) {
            if (fromAsset == null) {
                if(fonts == Fonts.NOTO_SANS) {
                    fromAsset = Typeface.createFromAsset(activity.getAssets(), "fonts/NotoSans-Regular.ttf");
                }else if(fonts == Fonts.ROBOTO){
                    fromAsset = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Regular.ttf");
                }
            }
        }else{
            if(fonts == Fonts.NOTO_SANS){
                fromAsset = Typeface.createFromAsset(activity.getAssets(), "fonts/NotoSans-Regular.ttf");
            }else if(fonts == Fonts.ROBOTO){
                fromAsset = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Regular.ttf");
            }else{
                fromAsset = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Regular.ttf");
            }

            //fromAsset = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Italic.ttf");
            currentTypeface = fonts;
        }
        return fromAsset;
    }

    public static SpannableString getSpannableString(String str) {
        spannableString = new SpannableString(str);
        spannableString.setSpan(new PSTypefaceSpan("", Utils.getTypeFace(Fonts.ROBOTO)), 0, spannableString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public enum Fonts{
        ROBOTO,
        NOTO_SANS
    }

    public static Bitmap getUnRotatedImage(String imagePath, Bitmap rotatedBitmap) {
        int rotate = 0;
        try
        {
            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation)
            {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        return Bitmap.createBitmap(rotatedBitmap, 0, 0, rotatedBitmap.getWidth(), rotatedBitmap.getHeight(), matrix,
                true);
    }

    public static String removeLastChar(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length()-1)=='x') {
            str = str.substring(0, str.length()-1);
        }
        return str;
    }

    public static void unbindDrawables(View view) {
        try{
            if (view.getBackground() != null) {
                view.getBackground().setCallback(null);
            }
            if (view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    unbindDrawables(((ViewGroup) view).getChildAt(i));
                }

                if(! (view instanceof AdapterView)) {
                    ((ViewGroup) view).removeAllViews();
                }
            }}catch (Exception e){
            Utils.psErrorLogE("Error in Unbind", e);
        }
    }

    // == System UI Stuff ==

    public static void makeToast(String msg, final Activity activity) {

        if(activity == null) {
            return;
        }
        final String message = msg;
        activity.runOnUiThread(() -> Toast.makeText(activity.getBaseContext(), message, Toast.LENGTH_LONG).show());
    }

    public static void hideKeyboard(Activity a) {
        InputMethodManager inputMethodManager = (InputMethodManager)  a.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View currentFocusedView = a.getCurrentFocus();

        // If there is no view is focused on, we need to exit, or the application will crash.
        // In this case the keyboard won't be visible anyway, so this is not a problem.
        if(currentFocusedView == null) {
            return;
        }

        IBinder windowToken = currentFocusedView.getWindowToken();
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
    }

    static public void showDialog(String title, String message, Context context) {
        new android.support.v7.app.AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // == Logging ==

    public static void psErrorLog(String log, Object obj){
        try {
            Log.d("Smoothsys", log);
            Log.d("Smoothsys", "Line : " + getLineNumber());
            Log.d("Smoothsys", "Class : " + getClassName(obj));
        }catch (Exception ee){}
    }

    public static void psErrorLogE(String log, Exception e) {
        try {
            StackTraceElement l = e.getStackTrace()[0];
            Log.d("Smoothsys", log);
            Log.d("Smoothsys", "Line : " + l.getLineNumber());
            Log.d("Smoothsys", "Method : " + l.getMethodName());
            Log.d("Smoothsys", "Class : " + l.getClassName());
        }catch (Exception ee){}

    }

    public static void psLog(String log){
        Log.d("Smoothsys", log);
    }

    public static int getLineNumber() {
        return Thread.currentThread().getStackTrace()[4].getLineNumber();
    }

    public static String getClassName(Object obj) {
        return ""+ ((Object) obj).getClass();
    }

    // == Misc Utils ==
    public static boolean isAndroid_5_0(){
        String version = Build.VERSION.RELEASE;
        if(version != "" && version != null){
            String[] versionDetail = version.split("\\.");
            Log.d("Smoothsys", "0 : " + versionDetail[0] + " 1 : " + versionDetail[1]);
            if(versionDetail[0].equals("5")){
                if(versionDetail[1].equals("0") || versionDetail[1].equals("00")){
                    return true;
                }
            }
        }

        return false;
    }


    public static boolean isEmailFormatValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static DecimalFormat getDecimalFormat() {

        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(2);
        df.toLocalizedPattern();
        return df;
    }

    public static boolean convertToBoolean(String s) {

        int i = Integer.parseInt(s);
        return convertToBoolean(i);
    }

    public static boolean convertToBoolean(int i) {
        return (i != 0);
    }

    // == Date Utils ==
    public static String getPastDate(int numberOfDaysBack) {

        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - numberOfDaysBack);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = calendar.getTime();

        return format.format(date);
    }

    public static String getCurrentDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(new Date());
    }

}