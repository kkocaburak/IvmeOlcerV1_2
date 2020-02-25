package com.burakkarakoca.ivmeolcerv1_2;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.burakkarakoca.ivmeolcerv1_2.App.CHANNEL_ID;

public class ExampleService extends Service implements SensorEventListener {

    long lastUpdate;
    float last_x, last_y, last_z = 0;
    float delta_x, delta_y, delta_z = 0;
    public static boolean readVal = false;
    public static boolean alarmIsPlaying = false;
    public static boolean arkaplanModu = false;

    Ringtone r;
    public static MediaPlayer mp;
    private SensorManager sensorManager;

    @Override
    public void onCreate() {
        Uri alarm = MainActivity.getAlert();
        r = RingtoneManager.getRingtone(getApplicationContext(), alarm);
        mp = MediaPlayer.create(getApplicationContext(), alarm);
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Sensor accelerometer = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL, new Handler());

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Deprem Alarmı")
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        //do heavy work on a background thread
        //stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    CountDownTimer countDownTimer = new CountDownTimer((MainActivity.baslatmaSuresi+2)*1000,1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            if(!MainActivity.durdurClicked){
                if(millisUntilFinished/1000 + 1 < 3){
                    readVal = true;
                    System.out.println("Çalışıyor");
                    MainActivity.durdurButon.setText("Durdur");
                    MainActivity.calisiyorText.setText("Çalışıyor");
                    MainActivity.calisiyorText.setTextColor(Color.parseColor("#00FF00"));
                    MainActivity.durdurClicked = true;
                } else{
                    MainActivity.durdurButon.setText("Durdur (" + ((millisUntilFinished / 1000) -1) +")");
                    System.out.println("Durdur ("+(millisUntilFinished/1000 - 1)+")");
                }
            } else if(MainActivity.durdurClicked){
                countDownTimer.cancel();
            }
        }

        @Override
        public void onFinish() {
            //
        }

    }.start();


    @Override
    public void onSensorChanged(SensorEvent event) {

        if(readVal){
            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 50) {
                lastUpdate = curTime;

                delta_x = Math.abs(last_x - event.values[0]);
                delta_y = Math.abs(last_y - event.values[1]);
                delta_z = Math.abs(last_z - event.values[2]);

                // 0.11 -- 0.0011 #### 1.7
                if (delta_x * 100 > MainActivity.hassasiyetValue) {
                    if (readVal && !mp.isPlaying() && !alarmIsPlaying){
                        alertOn();
                        System.out.println("alarm X");
                    }

                }

                if (delta_y * 100 > MainActivity.hassasiyetValue) {
                    if (readVal && !mp.isPlaying() && !alarmIsPlaying){
                        alertOn();
                        System.out.println("alarm Y");
                    }

                }

                if (delta_z * 100 > MainActivity.hassasiyetValue) {
                    if (readVal && !mp.isPlaying() && !alarmIsPlaying){
                        alertOn();
                        System.out.println("alarm Z");
                    }
                }

            }

//            if(!arkaplanModu){
                    String x = String.format("%.3f", delta_x);
                    String y = String.format("%.3f", delta_y);
                    String z = String.format("%.3f", delta_z);
                    MainActivity.degisimText.setText("X : " + x + " \n\nY : " + y + "\n\nZ : " + z);
//            }

        }

        last_x = event.values[0];
        last_y = event.values[1];
        last_z = event.values[2];

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //
    }

    public void alertOn() {

        mp.start();
        MainActivity.alarmText.setText("Alarm");
        MainActivity.alarmText.setTextColor(Color.parseColor("#ff0000"));
        alarmIsPlaying = true;

    }





}
