package com.burakkarakoca.ivmeolcerv1_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    public static boolean durdurClicked = false;

    public static Button baslatButon, durdurButon;
    public static TextView degisimText;
    public static TextView calisiyorText, alarmText;

    TextView hassasiyetDegerText, baslatmaSuresiText;
    SeekBar hassasiyetSeek, baslatmaSeek;
    public static int hassasiyetValue, baslatmaSuresi;
    String hassasiyetValueString;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        baslatButon = findViewById(R.id.buttonBaslat);
        durdurButon = findViewById(R.id.buttonDurdur);

        hassasiyetSeek = findViewById(R.id.hassasiyetSeekBar);
        baslatmaSeek = findViewById(R.id.baslatmaSeekBar);

        baslatmaSuresiText = findViewById(R.id.baslatmaSuresiText);
        hassasiyetDegerText = findViewById(R.id.hassasiyetDegeriText);
        calisiyorText = findViewById(R.id.baslatildi_text);
        alarmText = findViewById(R.id.alarm_text);
        degisimText = findViewById(R.id.textViewX);

        pref = getApplicationContext().getSharedPreferences("IvmeOlcer", 0); // 0 - for private mode
        hassasiyetValueString = pref.getString("hassasiyetStr", "Normal");
        hassasiyetValue = pref.getInt("hassasiyet",3);
        baslatmaSuresi = pref.getInt("baslatmaSuresi", 10);

        baslatmaSuresiText.setText(""+baslatmaSuresi);

        hassasiyetSeek.setProgress(hassasiyetValue);
        baslatmaSeek.setProgress(baslatmaSuresi);

        hassasiyetBarDegisti(hassasiyetValue);

        hassasiyetSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                hassasiyetBarDegisti(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        baslatmaSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                baslatmaBarDegisti(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void startService(View v) {
        baslatButon.setVisibility(View.INVISIBLE);
        durdurButon.setVisibility(View.VISIBLE);

        durdurClicked = false;

        Intent serviceIntent = new Intent(this, ExampleService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopService(View v) {
        baslatButon.setVisibility(View.VISIBLE);
        durdurButon.setVisibility(View.INVISIBLE);

        calisiyorText.setText("Beklemede");
        calisiyorText.setTextColor(Color.parseColor("#000000"));
        alarmText.setText("Alarm Yok");
        alarmText.setTextColor(Color.parseColor("#00FF00"));

        ExampleService.readVal = false;
        ExampleService.alarmIsPlaying = false;
        durdurClicked = true;

        degisimText.setText("X : " + " \n\nY : " + "\n\nZ : ");

        if(ExampleService.mp.isPlaying()){
            ExampleService.mp.pause();
            ExampleService.mp.seekTo(0);
        }

        Intent serviceIntent = new Intent(this, ExampleService.class);
        stopService(serviceIntent);
    }

    private void hassasiyetBarDegisti(int deger) {

        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("hassasiyet", deger);

        if(deger == 0){
            editor.putString("hassasiyetStr", "Çok Düşük");
            hassasiyetDegerText.setText("Çok Düşük");
            hassasiyetValue = 90;
        } else if(deger == 1){
            editor.putString("hassasiyetStr", "Düşük");
            hassasiyetDegerText.setText("Düşük");
            hassasiyetValue = 70;
        } else if(deger == 2){
            editor.putString("hassasiyetStr", "Normal");
            hassasiyetDegerText.setText("Normal");
            hassasiyetValue = 50;
        } else if(deger == 3){
            editor.putString("hassasiyetStr", "Yüksek");
            hassasiyetDegerText.setText("Yüksek");
            hassasiyetValue = 30;
        } else if(deger == 4){
            editor.putString("hassasiyetStr", "Çok Yüksek");
            hassasiyetDegerText.setText("Çok Yüksek");
            hassasiyetValue = 10;
        }

        editor.apply();
    }

    private void baslatmaBarDegisti(int progress) {

        baslatmaSuresiText.setText(""+progress);
        baslatmaSuresi = progress;
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("baslatmaSuresi", baslatmaSuresi);
        editor.apply();

    }

    public static Uri getAlert() {

        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if (alert == null) {
            Log.e("alertval", "alert null");
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            if (alert == null) {
                Log.e("alertval", "notif null");
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }

        } else {
            Log.e("alertval", "alert not null");
        }

        return alert;

    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        ExampleService.arkaplanModu = true;
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        ExampleService.arkaplanModu = false;
//    }

    //    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)){
//            Intent serviceIntent = new Intent(this, ExampleService.class);
//            stopService(serviceIntent);
//        }
//        return true;
//    }


}
