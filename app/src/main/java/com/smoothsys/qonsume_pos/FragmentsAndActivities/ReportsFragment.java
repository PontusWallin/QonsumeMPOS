package com.smoothsys.qonsume_pos.FragmentsAndActivities;

import android.app.DatePickerDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.DatePicker;

import com.smoothsys.qonsume_pos.R;
import com.smoothsys.qonsume_pos.Retrofit.RetrofitManager;
import com.smoothsys.qonsume_pos.StateManagement.ScreenState;
import com.smoothsys.qonsume_pos.StateManagement.StateChanger;
import com.smoothsys.qonsume_pos.Utilities.Cache;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ReportsFragment extends Fragment {

    private WebView webView;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        // == Web View ==
        webView = view.findViewById(R.id.reportsWebView);
        webView.setWebViewClient(new WebViewClient());

        // == Listener and Buttons ==
        mDateSetListener = setupDateListener();
        setupButtons(view);

        return view;
    }

    private DatePickerDialog.OnDateSetListener setupDateListener() {

        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                // == URL ==
                month = month + 1;
                String dateString = year + "-" + month + "-" + dayOfMonth;

                String shopId = Cache.mRestaurant.getId();
                String url = "http://qonsume.se/reports/zreport.php?start=" + dateString + " 00:00:01&end=" + dateString + " 23:59:59&shop_id=" + shopId;

                webView.loadUrl(url,
                        setupHeader());
            }
        };
    }

    private void setupButtons(View view) {

        Button btnXReport = view.findViewById(R.id.x_btn);
        btnXReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                webView.loadUrl("http://qonsume.se/reports/xreport.php?shop_id=" + Cache.mRestaurant.getId(),
                        setupHeader());
            }
        });

        Button btnYReport = view.findViewById(R.id.z_btn);
        btnYReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupDatePicker();
            }
        });
    }

    private void setupDatePicker() {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener
                ,year,month,day);

        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }

    private Map<String, String> setupHeader() {

        Map<String, String> header = new HashMap<>();
        header.put("Token", RetrofitManager.getDevToken());
        return header;
    }

    @Override
    public void onResume() {
        ((MainActivity)getActivity()).setTitleBarText(getString(R.string.reports_name));
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        StateChanger.setState(ScreenState.onReportsScreen);
    }
}