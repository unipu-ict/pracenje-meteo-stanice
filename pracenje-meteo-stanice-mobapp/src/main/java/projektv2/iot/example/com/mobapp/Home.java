/*
 * Created by Mikaela on 20/5/2018.
 */
package projektv2.iot.example.com.mobapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

import projektv2.iot.example.com.mobapp.R;

public class  Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // varijable za početne icone
    private ImageButton mTemp;
    private ImageButton mTlak;


    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Metoda za dohvat reference
        mTemp = (ImageButton) findViewById(R.id.button_temp);
        // Kreiranje metode za reakcije na aktivnost korisnika
        mTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Home.this,Temperatura.class);
                startActivity(i);
            }
        });
        // Metoda za dohvat reference
        mTlak = (ImageButton) findViewById(R.id.button_tlak);
        // Kreiranje metode za reakcije na aktivnost korisnika
        mTlak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent t = new Intent(Home.this,Tlak.class);
                startActivity(t);
            }
        });


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
                Intent h = new Intent(Home.this,Home.class);
                startActivity(h);
                break;
            case R.id.nav_temp:
                Intent t = new Intent(Home.this,Temperatura.class);
                startActivity(t);
                break;
            case R.id.nav_tlak:
                Intent p = new Intent(Home.this,Tlak.class);
                startActivity(p);
                break;
            case R.id.nav_info:
                Intent i = new Intent(Home.this,AboutUs.class);
                startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
