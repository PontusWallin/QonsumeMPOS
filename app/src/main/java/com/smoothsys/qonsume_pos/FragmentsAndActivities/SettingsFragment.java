package com.smoothsys.qonsume_pos.FragmentsAndActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.smoothsys.qonsume_pos.R;
import com.smoothsys.qonsume_pos.StateManagement.StateChanger;
import com.smoothsys.qonsume_pos.StateManagement.ScreenState;

import java.util.Locale;

public class SettingsFragment extends Fragment  {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        return view;
    }

    Button registrationButton;
    @Override
    public void onStart() {
        super.onStart();
        StateChanger.setState(ScreenState.onSettingsScreen);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = prefs.edit();
        setupMerchIdField();


        registrationButton = getView().findViewById(R.id.registerCardBtn);
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NativeCardRegistrationActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).setTitleBarText(getString(R.string.settings_name));
    }

    private void setupRadioGroup() {

        String activeLang = prefs.getString("_language","english");

        // find radio buttons, activate btn with matching tag
        RadioGroup group = getView().findViewById(R.id.language_group);

        for(int i = 0; i<group.getChildCount(); i++) {
            RadioButton currentButton = (RadioButton) group.getChildAt(i);
            if(currentButton.getTag().equals(activeLang)) {
                currentButton.setChecked(true);
                break;
            }
        }

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton button = getView().findViewById(checkedId);
                if(button.getTag().equals("english")) {
                    editor.putString("_language","english");
                    editor.apply();
                    setLocale("en_US");
                }

                if(button.getTag().equals("swedish")) {
                    editor.putString("_language","swedish");
                    editor.apply();
                    setLocale("sv_SE");
                }
            }
        });

    }

    private void setupMerchIdField() {

        final EditText merchIDEdit = getView().findViewById(R.id.bambura);
        String storedId = prefs.getString("_bambura_merch_id", "");
        if(!storedId.equals("")) {
            merchIDEdit.setText(storedId);
        }

        final Button applyBamburaBtn = getView().findViewById(R.id.applyBambura_btn);

        applyBamburaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(), "This demo is using a test GMID. Real GMID:s will be used in the future.", Toast.LENGTH_LONG).show();
                /*String editText = String.valueOf(merchIDEdit.getText());
                if(editText.equals("")) {
                    Toast.makeText(getContext(), "Bambura Merchand ID field is empty!", Toast.LENGTH_SHORT).show();
                }

                editor.putString("_bambura_merch_id", editText);
                editor.commit();*/
            }
        });
    }


    // == Localization ==
    public void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
    }
}
