/*
 * Created by Mikaela on 20/5/2018.
 */
package projektv2.iot.example.com.mobapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import projektv2.iot.example.com.mobapp.R;

public class Tlak extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Timer timer;
    private TextView mTlak;

    private static final String TAG = Tlak.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tlak);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        ArtikCloudSession.getInstance().setContext(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    // Kreiranje navigacijske trake, prebacivanje s jedne aktivnosti na drugu
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id){

            case R.id.nav_home:
                Intent h = new Intent(Tlak.this,Home.class);
                startActivity(h);
                break;
            case R.id.nav_temp:
                Intent t = new Intent(Tlak.this,Temperatura.class);
                startActivity(t);
                break;
            case R.id.nav_tlak:
                Intent p = new Intent(Tlak.this,Tlak.class);
                startActivity(p);
                break;
            case R.id.nav_info:
                Intent i = new Intent(Tlak.this,AboutUs.class);
                startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Timer za tlak
    private void PressTimer() {
        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ChangePicture(ArtikCloudSession.getPress());
                        mTlak = (TextView) findViewById(R.id.textView3);
                        mTlak.setText("Tlak: "+ ArtikCloudSession.getPress());

                    }
                });
            }
        }, 0, 3500);
    }


    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(mWSUpdateReceiver,
                makeWebsocketUpdateIntentFilter());
        ArtikCloudSession.getInstance().connectFirehoseWS();//non blocking

        PressTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mWSUpdateReceiver);
        // Ovo bi trebalo raditi!
        // ArtikCloudSession.getInstance().disconnectFirehoseWS(); //non blocking
    }
    private final BroadcastReceiver mWSUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (ArtikCloudSession.WEBSOCKET_LIVE_ONOPEN.equals(action)) {
                displayLiveStatus("WebSocket /live connected");
            } else if (ArtikCloudSession.WEBSOCKET_LIVE_ONMSG.equals(action)) {
                String status = intent.getStringExtra(ArtikCloudSession.DEVICE_DATA);
                String updateTime = intent.getStringExtra(ArtikCloudSession.TIMESTEP);
                displayDeviceStatus(status, updateTime);
            } else if (ArtikCloudSession.WEBSOCKET_LIVE_ONCLOSE.equals(action) ||
                    ArtikCloudSession.WEBSOCKET_LIVE_ONERROR.equals(action)) {
                displayLiveStatus(intent.getStringExtra("error"));
            }
        }
    };


    private void displayLiveStatus(String status) {
        Log.d(TAG, status);
    }

    private void displayDeviceStatus(String status, String updateTimems) {
        long time_ms = Long.parseLong(updateTimems);
    }


    private static IntentFilter makeWebsocketUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ArtikCloudSession.WEBSOCKET_LIVE_ONOPEN);
        intentFilter.addAction(ArtikCloudSession.WEBSOCKET_LIVE_ONMSG);
        intentFilter.addAction(ArtikCloudSession.WEBSOCKET_LIVE_ONCLOSE);
        intentFilter.addAction(ArtikCloudSession.WEBSOCKET_LIVE_ONERROR);
        return intentFilter;
    }

    // Funkcija ChangePicture služi za izmjenu slika s obzirom koliki će tlak pokazati senzor
    private void ChangePicture(float press)
    {

        Log.d(TAG, "Radi!");

        if (press <= 975)
        {
            findViewById(R.id.stormyImage).setVisibility(View.VISIBLE);
            findViewById(R.id.rainyImage).setVisibility(View.INVISIBLE);
            findViewById(R.id.changeImage).setVisibility(View.INVISIBLE);
            findViewById(R.id.fairImage).setVisibility(View.INVISIBLE);
            findViewById(R.id.dryImage).setVisibility(View.INVISIBLE);
        }
        else if (press <= 985)
        {
            findViewById(R.id.stormyImage).setVisibility(View.INVISIBLE);
            findViewById(R.id.rainyImage).setVisibility(View.VISIBLE);
            findViewById(R.id.changeImage).setVisibility(View.INVISIBLE);
            findViewById(R.id.fairImage).setVisibility(View.INVISIBLE);
            findViewById(R.id.dryImage).setVisibility(View.INVISIBLE);

        }
        else if (press <= 1005)
        {
            findViewById(R.id.stormyImage).setVisibility(View.INVISIBLE);
            findViewById(R.id.rainyImage).setVisibility(View.INVISIBLE);
            findViewById(R.id.changeImage).setVisibility(View.VISIBLE);
            findViewById(R.id.fairImage).setVisibility(View.INVISIBLE);
            findViewById(R.id.dryImage).setVisibility(View.INVISIBLE);

        }
        else if (press <= 1025)
        {
            findViewById(R.id.stormyImage).setVisibility(View.INVISIBLE);
            findViewById(R.id.rainyImage).setVisibility(View.INVISIBLE);
            findViewById(R.id.changeImage).setVisibility(View.INVISIBLE);
            findViewById(R.id.fairImage).setVisibility(View.VISIBLE);
            findViewById(R.id.dryImage).setVisibility(View.INVISIBLE);

        }
        else if (press <= 1050)
        {
            findViewById(R.id.stormyImage).setVisibility(View.INVISIBLE);
            findViewById(R.id.rainyImage).setVisibility(View.INVISIBLE);
            findViewById(R.id.changeImage).setVisibility(View.INVISIBLE);
            findViewById(R.id.fairImage).setVisibility(View.INVISIBLE);
            findViewById(R.id.dryImage).setVisibility(View.VISIBLE);

        }
        else
        {
            Log.d(TAG, "Ne radi!");
        }

    }


}
