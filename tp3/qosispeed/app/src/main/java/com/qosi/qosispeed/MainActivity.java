package com.qosi.qosispeed;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SettingsFragment sf = new SettingsFragment();
    private HomeFragment hf = new HomeFragment();
    private FragmentManager fm;
    private ActionBar actionBar;
    private ActionBarDrawerToggle actionBarToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        actionBar = getSupportActionBar();
        actionBarToggle = toggle;


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_main);
        navigationView.setNavigationItemSelectedListener(this);

        fm = getSupportFragmentManager();

        hf.setSettings(sf);
        hf.setActionBar(actionBar);

        fm.beginTransaction().replace(R.id.content_frame, hf).commit();

        // Create settings file
        File file = new File(getApplicationContext().getFilesDir(), "settings.json");
        if(!file.exists()) {
            JSONObject st = new JSONObject();
            try {
                st.accumulate(getString(R.string.bandwidth), true);
                st.accumulate(getString(R.string.delay), true);
                st.accumulate(getString(R.string.jitter), true);
                FileOutputStream out = openFileOutput("settings.json", Context.MODE_PRIVATE);
                out.write(st.toString().getBytes());
            } catch (IOException | JSONException je) {
                je.printStackTrace();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Fragment recent = fm.findFragmentById(R.id.content_frame);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(recent != null && recent instanceof ResultsFragment) {
            fm.beginTransaction().replace(R.id.content_frame, hf).commit();
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            actionBarToggle.setDrawerIndicatorEnabled(true);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.nav_manage) {
            return true;
        }
        else if(id == R.id.nav_home) {
            Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivityForResult(myIntent, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fm = getSupportFragmentManager();

        if (id == R.id.nav_manage) {
            fm.beginTransaction().replace(R.id.content_frame, sf).commit();
            setTitle(item.getTitle());
        }
        else if(id == R.id.nav_home) {
            fm.beginTransaction().replace(R.id.content_frame, hf).commit();
            setTitle("QoSI Speedtest");
            hf.doOptions();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
