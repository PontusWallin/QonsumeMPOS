package com.smoothsys.qonsume_pos.FragmentsAndActivities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.smoothsys.qonsume_pos.R;
import com.smoothsys.qonsume_pos.StateManagement.ScreenState;
import com.smoothsys.qonsume_pos.StateManagement.StateChanger;
import com.smoothsys.qonsume_pos.Utilities.Utils;

/**
 * Created by Pontus on 2017-04-09.
 *  Refactored 18-02-22
 */

public class LoginFragment extends Fragment{

    EditText passwordText, nameText;

    public LoginFragment() {}

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_login, container, false);
        setupUI();
        return view;
    }

    void setupUI() {
        setupTextFields();
        setupLoginBtn();
    }

    private void setupTextFields() {
        nameText = view.findViewById(R.id.userNameEditView);
        passwordText = view.findViewById(R.id.passwordEditView);
    }

    private void setupLoginBtn() {
        FrameLayout loginButton = view.findViewById(R.id.login_btn_layout);
        loginButton.setOnClickListener(v -> {

            String name = nameText.getText().toString();
            String password = passwordText.getText().toString();
            ((MainActivity)getActivity()).doLogin(name,password);
            ((MainActivity) getActivity()).setVersionLabel();
            Utils.hideKeyboard(getActivity());
        });
    }

    public void onStart() {
        super.onStart();
        StateChanger.setState(ScreenState.onLoginScreen);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void onResume() {
        ((MainActivity)getActivity()).setTitleBarText(getString(R.string.home_name));
        super.onResume();
    }
}