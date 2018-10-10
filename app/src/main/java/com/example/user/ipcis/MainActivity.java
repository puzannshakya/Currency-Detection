package com.example.user.ipcis;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.user.ipcis.preferences.CurrencySettingsActivity;
import com.example.user.ipcis.preferences.CurrencySettingsFragment;

import java.util.prefs.PreferenceChangeListener;
import java.util.zip.Inflater;
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static boolean isFirstRun = true;

    private final String LOG_TAG = this.getClass().getSimpleName();

    private FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wrapper);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        this.setTitle("IPCIS");

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Fragment stockAndConverterFragment = new StockAndConverterFragment();

        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, stockAndConverterFragment);
        ft.addToBackStack(null);
        ft.commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() == 1){


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int elementId = item.getItemId();
        Class fragmentClass = null;
        Fragment fragment = null;
        if (elementId == R.id.action_camera){
            //fragmentClass = CurrencySettingsFragment.class;
            Intent intent = new Intent(this, ReaderMainActivity.class);
            startActivity(intent);
//            try {
//                fragment = (Fragment) fragmentClass.newInstance();
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.container, fragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
//            }catch (Exception e){
//                Log.e(LOG_TAG, "Error Creating fragment " + e);
//            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Class fragmentClass = null;
        Fragment fragment = null;

        int id = item.getItemId();
        if (id == R.id.home_page_item){
            fragmentClass = StockAndConverterFragment.class;
        }else if (id == R.id.exchanger_rates_item) {
            fragmentClass = ExchangerFragment.class;
        }else if(id == R.id.camera){
            Intent intent = new Intent(this, ReaderMainActivity.class);
            startActivity(intent);

        }else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, CurrencySettingsActivity.class);
            startActivity(intent);
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error Creating fragment " + e);
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        try {
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }catch (Exception e){
            Log.e(LOG_TAG, "Transaction error" + e);
        }

        setTitle(item.getTitle());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }
}
