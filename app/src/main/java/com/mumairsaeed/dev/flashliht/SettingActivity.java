package com.mumairsaeed.dev.flashliht;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {

    Toolbar toolbar;
    AppCompatTextView switchSound, ternOnAtStart, ternOffAtExit;
    AppCompatCheckBox btnSwitchSound, btnTernOnAtStart, btnTernOffAtExit;

    static final String SWITCH_SOUND = "switch_sound",
            TERN_ON_AT_START = "tern_on_at_start",
            TERN_OFF_AT_EXIT = "tern_off_at_exit",
            DB_NAME = "local";

    SharedPreferences localDB;
    Boolean isSwitchSound, isTernOnAtStart, isTernOffAtExit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        toolbar = findViewById(R.id.toolbar);
        switchSound = findViewById(R.id.txt_switch_sound_state);
        ternOnAtStart = findViewById(R.id.txt_toas_state);
        ternOffAtExit = findViewById(R.id.txt_toae_state);
        btnSwitchSound = findViewById(R.id.cb_swithc_sound);
        btnTernOnAtStart = findViewById(R.id.cb_toas);
        btnTernOffAtExit = findViewById(R.id.cb_toat);




        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Setting");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        updateLocalDB();

        btnSwitchSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editDB = localDB.edit();
                if(compoundButton.isChecked()){
                    editDB.putBoolean(SWITCH_SOUND, true);
                }else {
                    editDB.putBoolean(SWITCH_SOUND, false);
                }
                editDB.apply();
                updateLocalDB();
            }
        });

        btnTernOnAtStart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editDB = localDB.edit();
                if(compoundButton.isChecked()){
                    editDB.putBoolean(TERN_ON_AT_START, true);
                }else {
                    editDB.putBoolean(TERN_ON_AT_START, false);
                }
                editDB.apply();
                updateLocalDB();
            }
        });

        btnTernOffAtExit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editDB = localDB.edit();
                if(compoundButton.isChecked()){
                    editDB.putBoolean(TERN_OFF_AT_EXIT, true);
                }else {
                    editDB.putBoolean(TERN_OFF_AT_EXIT, false);
                }
                editDB.apply();
                updateLocalDB();
            }
        });


    }

    public void updateLocalDB(){
        localDB = getSharedPreferences(DB_NAME,MODE_PRIVATE);
        isSwitchSound = localDB.getBoolean(SWITCH_SOUND,true);
        isTernOnAtStart = localDB.getBoolean(TERN_ON_AT_START,false);
        isTernOffAtExit = localDB.getBoolean(TERN_OFF_AT_EXIT,false);

        updateCheckBox();


    }

    public void updateCheckBox(){
        if(isSwitchSound){
            btnSwitchSound.setChecked(true);
            switchSound.setText("Enable");
        }else {
            btnSwitchSound.setChecked(false);
            switchSound.setText("Disable");
        }

        if(isTernOnAtStart){
            btnTernOnAtStart.setChecked(true);
            ternOnAtStart.setText("Enable");
        }else {
            btnTernOnAtStart.setChecked(false);
            ternOnAtStart.setText("Disable");
        }

        if(isTernOffAtExit){
            btnTernOffAtExit.setChecked(true);
            ternOffAtExit.setText("Enable");
        }else {
            btnTernOffAtExit.setChecked(false);
            ternOffAtExit.setText("Disable");
        }
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}