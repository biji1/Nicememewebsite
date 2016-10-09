package com.dev.biji.nicememewebsite;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import layout.NewAppWidget;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private SharedPreferences _sharedPref;
    private SharedPreferences.Editor _editor;
    private int _count;
    private TextView _countText;
    private GoogleApiClient _googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get local
        _sharedPref = this.getSharedPreferences("default", this.MODE_PRIVATE);
        _editor = _sharedPref.edit();
        _count = _sharedPref.getInt("nmw_count", 0);

        // set font family
        TextView labelText = (TextView) findViewById(R.id.labelText);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/arialbd.ttf");
        labelText.setTypeface(face);

        // update view
        _countText = (TextView) findViewById(R.id.countText);
        _countText.setText(String.valueOf(_count));

        // google
        _googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                // add other APIs and scopes here as needed
                .build();
        _googleApiClient.connect();
        System.out.println("Connect");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            _googleApiClient.connect();
            System.out.println("Connect result");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        _googleApiClient.connect();
        System.out.println("Connect start");
    }

    @Override
    protected void onStop() {
        super.onStop();
        _googleApiClient.disconnect();
        System.out.println("dConnect");
    }

    @Override
    public void onResume() {
        super.onResume();
        _count = _sharedPref.getInt("nmw_count", 0);
        _countText.setText(String.valueOf(_count));
    }

    @Override
    public void onPause() {
        super.onPause();
        _count = _sharedPref.getInt("nmw_count", 0);

        // update view
        RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.new_app_widget);
        views.setTextViewText(R.id.countWidget, String.valueOf(_count));

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ComponentName thisAppWidget = new ComponentName(this.getPackageName(), NewAppWidget.class.getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        try {
            appWidgetManager.updateAppWidget(appWidgetIds[0], views);
        } catch (Exception e) {
            System.err.println("exception no widget : " + e);
        }
    }

    public void frameLayout_onClick(View view) {
        // increment
        ++_count;

        // save in local
        _editor.putInt("nmw_count", _count);
        _editor.commit();

        // update view
        _countText.setText(String.valueOf(_count));

        // play sound
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.nicememewebsite);
                mp.start();
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                mp.release();
                            }
                        }, 2000);
            }
        });

        // achievement
        if (_googleApiClient != null && _googleApiClient.isConnected()) {
            System.out.println("check achiev");
            switch (_count) {
                case 1:
                    Games.Achievements.unlock(_googleApiClient, getString(R.string.achievement_1));
                case 10:
                    Games.Achievements.unlock(_googleApiClient, getString(R.string.achievement_2));
                case 100:
                    Games.Achievements.unlock(_googleApiClient, getString(R.string.achievement_3));
                case 1000:
                    Games.Achievements.unlock(_googleApiClient, getString(R.string.achievement_4));
                case 10000:
                    Games.Achievements.unlock(_googleApiClient, getString(R.string.achievement_5));
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        _googleApiClient.connect();
        System.out.println("Connect suspend");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
