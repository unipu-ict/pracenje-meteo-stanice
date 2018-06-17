package com.example.iot.projektv2;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

//importiranje Rainbowhat i Bmx280 drivera
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat;
import com.google.android.things.contrib.driver.bmx280.Bmx280SensorDriver;


import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;


public class MainActivity extends Activity {

    //Varijable za spremanje vrijednosti iz senzora
    float temp;
    float press;


    //Driver za Bmx280
    private Bmx280SensorDriver mEnvironmentalSensorDriver;

    //Novi senzor manager
    private SensorManager mSensorManager;


    //onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSensorManager = getSystemService(SensorManager.class);


        // Inicijalizacija senzora
        try {
            mEnvironmentalSensorDriver =  RainbowHat.createSensorDriver();
            mEnvironmentalSensorDriver.registerTemperatureSensor();
            mEnvironmentalSensorDriver.registerPressureSensor();

            Log.d(TAG, "BMP280 Inicijaliziran");
        } catch (IOException e) {
            throw new RuntimeException("Greska u inicijalizaciji", e);
        }

        initScheduler();
    }

    //onStart
    @Override
    protected void onStart(){
        super.onStart();

       //Registracija BMP280 senzora temperature
        Sensor temperature = mSensorManager
                .getDynamicSensorList(Sensor.TYPE_AMBIENT_TEMPERATURE).get(0);
        mSensorManager.registerListener(mSensorEventListener, temperature,
                SensorManager.SENSOR_DELAY_NORMAL);

        //Registracija BMP280 senzora tlaka
        Sensor pressure = mSensorManager
                .getDynamicSensorList(Sensor.TYPE_PRESSURE).get(0);
        mSensorManager.registerListener(mSensorEventListener, pressure,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onStop
    @Override
    protected void onStop() {
        super.onStop();

        //Gasenje listenera
        mSensorManager.unregisterListener(mSensorEventListener);
    }

    //onDestroy
    protected void onDestroy() {
        super.onDestroy();

        //Brisanje senzora
        if (mEnvironmentalSensorDriver != null) {
            try {
                mEnvironmentalSensorDriver.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing sensors", e);
            } finally {
                mEnvironmentalSensorDriver = null;
            }
        }
    }

  //Funkcija za ispis temperature u logcat
    private void printTemperature(float temperature) {

           Log.d(TAG, "Temperature:" + temperature);
        }


    //Funkcija za ispis tlaka zraka u logcat
    private void printPressure(float pressure) {

            Log.d(TAG, "Tlak zraka:" + pressure);
        }


    //Funkcija koja ocitava stanje senzora
    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            final float value = event.values[0];

            //Ocitanje stanja senzora temperature
            //postavlja varijablu temp na vrijednost senzora i poziva funkciju za ispis vrijednosti
            if(event.sensor.getType()==Sensor.TYPE_AMBIENT_TEMPERATURE){
                temp=value;    
                printTemperature(value);
            }
            //Ocitanje stanja senzora tlaka
            //postavlja varijablu press na vrijednost senzora i poziva funkciju za ispis vrijednosti
            if(event.sensor.getType()==Sensor.TYPE_PRESSURE){
                press=value;    
                printPressure(value);
            }
        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.d(TAG, "accuracy changed:" + accuracy);
        }



    };


    //Prosljedivanje podataka sendData metodi u ArtikClient klasi u odredenim intervalima
    private void initScheduler() {
        ScheduledExecutorService scheduler=
                Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Temp --" + temp); //Ispis temperature
                Log.d(TAG, "Press --" + press);//Ispis tlaka
            ArtikClient.getInstance(MainActivity.this,
                    "9f167ec1e53f4e3e91ad3ab6222d50e0", "d1add190c69e418288ba3f5849a38e41").sendData(temp, press);
            }
            }, 1, 1, TimeUnit.SECONDS);//Odredivanje inicijalne pauze nakon koje se pokrece kod; Odredivanje vremena izmedu pokretanja koda; Odredivanje jedinice vremena
}

}
