package com.example.emily.simplehealthtracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.emily.simplehealthtracker.data.Entry;
import com.example.emily.simplehealthtracker.data.EntryViewModel;
import com.example.emily.simplehealthtracker.data.SHTDatabase;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

/* Emily Stuckey
    November, 2018
    Simple Health Tracker
    for Udacity Nanodegree
 */
public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_ID = "SH";
    private static final String ADMOB_ID = "";
    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();
        MobileAds.initialize(this, ADMOB_ID);

        new CheckOnlineForAdsTask().execute();

    }



    public void startSimpleOrDetailed(View view){
        if (view.getId() == R.id.btn_simple) {
            Intent intent = new Intent(this, SimpleActivity.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, DetailedActivity.class);
            startActivity(intent);
        }
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    class CheckOnlineForAdsTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // Code in this try block is adapted from Levit:
                // https://stackoverflow.com/a/27312494
                int timeout = 1500;
                Socket socket = new Socket();
                SocketAddress socketAddress = new InetSocketAddress("8.8.8.8", 53);
                socket.connect(socketAddress, timeout);
                socket.close();

                mAdView = findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);

            } catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }
    }

}
