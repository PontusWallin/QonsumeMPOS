package com.smoothsys.qonsume_pos.FragmentsAndActivities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.smoothsys.qonsume_pos.R;
import com.smoothsys.qonsume_pos.StateManagement.StateChanger;
import com.smoothsys.qonsume_pos.StateManagement.ScreenState;

public class NativeCardRegistrationActivity extends AppCompatActivity{

    android.support.v7.widget.Toolbar mToolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_card_registration);


        // == Set up toolbar ==
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        setTitle(getResources().getString(R.string.card_register_title));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.nav_home) {
            finish();
        }
        this.finish();

        return true;
    }




    @Override
    public void onStart() {
        super.onStart();
        StateChanger.setState(ScreenState.onSettingsScreen);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.setTitle(getString(R.string.settings_name));
    }
}