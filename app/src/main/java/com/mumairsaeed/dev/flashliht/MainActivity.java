package com.mumairsaeed.dev.flashliht;

import static android.Manifest.permission.RECORD_AUDIO;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecognitionListener{

    AppCompatImageButton btnLight;
    CardView cardView;
    AppCompatTextView txtLightState;
    AppCompatImageView imgLightState;

    static final String SWITCH_SOUND = "switch_sound",
            TERN_ON_AT_START = "tern_on_at_start",
            TERN_OFF_AT_EXIT = "tern_off_at_exit",
            DB_NAME = "local";

    SharedPreferences localDB;
    Boolean isSwitchSound, isTernOnAtStart, isTernOffAtExit, isLightState;

    private AppCompatToggleButton toggleButton;
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;

    private static final int REQUEST_RECORD_PERMISSION = 100;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        progressBar = findViewById(R.id.progressBar1);
        toggleButton = findViewById(R.id.toggleButton1);

        btnLight = findViewById(R.id.btn_lightstate);
        txtLightState = findViewById(R.id.txt_lightstatus);
        imgLightState = findViewById(R.id.img_lightstatus);
        cardView = findViewById(R.id.cardview);

        updateLocalDB();
        isLightState = isTernOnAtStart;
        changeLightState(isLightState);



        btnLight.setOnClickListener(view -> {
            changeLightState(!isLightState);
            isLightState = !isLightState;

            if(isSwitchSound){
                final MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.click_sound);
                mediaPlayer.start();
            }

        });


        progressBar.setVisibility(View.INVISIBLE);
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,"US-en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setIndeterminate(true);
                ActivityCompat.requestPermissions (MainActivity.this, new String[]{RECORD_AUDIO},
                        REQUEST_RECORD_PERMISSION);
            } else {
                progressBar.setIndeterminate(false);
                progressBar.setVisibility(View.INVISIBLE);
                speech.stopListening();

            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                speech.startListening(recognizerIntent);
            } else {
                Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();

            }
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        updateLocalDB();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isTernOffAtExit){
            changeLightState(false);
        }

        if (speech != null) {
            speech.destroy();
        }
    }

    public void changeLightState(Boolean flag){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
            String camID;


            try {
                camID = cameraManager.getCameraIdList()[0];
                cameraManager.setTorchMode(camID, flag);
            } catch (CameraAccessException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }


            if(flag){
                txtLightState.setText("Light On");
                imgLightState.setImageResource(R.drawable.ic_flashlight_on);
                btnLight.setImageResource(R.drawable.light_on);
            }else {
                txtLightState.setText("Light Off");
                imgLightState.setImageResource(R.drawable.ic_flashlight_off);
                btnLight.setImageResource(R.drawable.light_off1);
            }


        }else {
            Toast.makeText(this, "Your Device is not eligible for that", Toast.LENGTH_LONG).show();
        }
    }

    public void gotoSetting(View view) {
        Intent setting = new Intent(this, SettingActivity.class);
        startActivity(setting);
    }

    public void updateLocalDB(){
        localDB = getSharedPreferences(DB_NAME,MODE_PRIVATE);
        isSwitchSound = localDB.getBoolean(SWITCH_SOUND,true);
        isTernOnAtStart = localDB.getBoolean(TERN_ON_AT_START,false);
        isTernOffAtExit = localDB.getBoolean(TERN_OFF_AT_EXIT,false);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (speech != null) {
//            speech.destroy();
//        }
    }


    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Toast.makeText(this, "...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBeginningOfSpeech() {

        progressBar.setIndeterminate(false);
        progressBar.setMax(10);

    }

    @Override
    public void onRmsChanged(float rmsdB) {

        progressBar.setProgress((int) rmsdB);

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {

        progressBar.setIndeterminate(true);
        toggleButton.setChecked(false);

    }

    @Override
    public void onError(int errorCode) {


        String errorMessage = getErrorText(errorCode);

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();

        if(toggleButton.isChecked()){
            toggleButton.setChecked(false);
            startSpeach();
        }else {
            toggleButton.setChecked(false);
        }




    }

    @Override
    public void onResults(Bundle results) {

        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text = result + " ";

        text = text.toLowerCase().trim();

        if(text.equals("light on") || text.equals("lite on")){
            changeLightState(true);
        }

        if(text.equals("light off") || text.equals("lite off") || text.equals("light of") || text.equals("lite of")){
            changeLightState(false);
        }

        if(!(text.equals("light on") || text.equals("lite on") || text.equals("light off") || text.equals("lite off") || text.equals("light of") || text.equals("lite of"))){
            Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
        }


        if(text.equals("stop")){
            toggleButton.setChecked(false);
        }else{
            startSpeach();
        }

        if (text.equals("exit app")) {
            finish();
        }


    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }


    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    public void startSpeach(){
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
        toggleButton.setChecked(true);
    }


    public void gotoInfo(View view) {
    }
}