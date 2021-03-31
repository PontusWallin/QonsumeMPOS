package com.smoothsys.qonsume_pos.FragmentsAndActivities;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.smoothsys.qonsume_pos.R;
import com.smoothsys.qonsume_pos.StateManagement.ScreenState;
import com.smoothsys.qonsume_pos.StateManagement.StateChanger;
import com.smoothsys.qonsume_pos.Utilities.Cache;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class ProfileFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setupFields() {

        if(Cache.mRestaurant == null) {
            Toast.makeText(getContext(), "Restaurant is null!", Toast.LENGTH_SHORT).show();
            return;
        }

        TextView nameTv = getView().findViewById(R.id.name_tv);
        nameTv.setText(Cache.mRestaurant.getName());

        TextView emailTv = getView().findViewById(R.id.email_tv);
        emailTv.setText(Cache.mRestaurant.getEmail());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public  void onStart() {

        super.onStart();

        setupFields();

        ImageView qr_iv = getView().findViewById(R.id.qrImageView);
        createQRBitmap(Cache.mRestaurant.getId(), qr_iv);

        StateChanger.setState(ScreenState.onProfileScreen);
        ((MainActivity)getActivity()).setTitleBarText(getString(R.string.profile_title));
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void createQRBitmap(String formatedQRString, ImageView imageView) {
        Bitmap QRBitmap = null;
        try {
            QRBitmap = encodeAsBitmap(formatedQRString);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        // == We don't want to use the logo - it makes it impossible to read the QR code ==

        //Bitmap overLay = BitmapFactory.decodeResource(getResources(),R.drawable.logo);
        //addOverlay(QRBitmap,overLay);

        imageView.setImageBitmap(QRBitmap);
    }

    Bitmap encodeAsBitmap(String str) throws WriterException {

        int WIDTH = 400;
        BitMatrix result;
        try {

            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
        } catch (IllegalArgumentException iae) {
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, WIDTH, 0, 0, w, h);
        return bitmap;
    }

    void addOverlay(Bitmap source, Bitmap over) {

        Bitmap scaledOverlay = Bitmap.createScaledBitmap(over, 64,64, true);

        //Add overlaybitmap to final bitmap
        Canvas canvas = new Canvas(source);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);

        //Find the center of source
        int centerX = (canvas.getWidth() - scaledOverlay.getWidth())/2;
        int centerY = (canvas.getHeight() - scaledOverlay.getHeight())/2;

        canvas.drawBitmap(scaledOverlay,centerX,centerY,paint);
    }
}
