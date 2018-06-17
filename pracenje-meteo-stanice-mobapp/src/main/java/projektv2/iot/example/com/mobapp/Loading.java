/*
 * Created by Mikaela on 20/5/2018.
 */
package projektv2.iot.example.com.mobapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import projektv2.iot.example.com.mobapp.R;

public class Loading extends AppCompatActivity {

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        Thread background = new Thread() {
            public void run() {
                try {
                    // Postavljanje dretve na mirovanje od 5 sekundi
                    sleep(5 * 1000);

                    // Nakon 5 sekundi se prebacuje na sljedeću aktivnost
                    Intent i = new Intent(getBaseContext(), Home.class);
                    startActivity(i);

                    // Uklanjanje aktivnosti
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        // Početak dretve
        background.start();
    }
}
