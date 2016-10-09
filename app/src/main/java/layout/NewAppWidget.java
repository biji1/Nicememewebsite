package layout;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.RemoteViews;

import com.dev.biji.nicememewebsite.R;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {

    private static final String MyOnClick = "myOnClickTag";
    private int _count;

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // get local
        SharedPreferences sharedPref = context.getSharedPreferences("default", context.MODE_PRIVATE);
        _count = sharedPref.getInt("nmw_count", 0);

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        views.setOnClickPendingIntent(R.id.frameLayout2, getPendingSelfIntent(context, MyOnClick));
        views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setTextViewText(R.id.countWidget, String.valueOf(_count));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);//add this line
        if (MyOnClick.equals(intent.getAction())){
            // increment
            SharedPreferences sharedPref = context.getSharedPreferences("default", context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            _count = sharedPref.getInt("nmw_count", 0);
            ++_count;
            editor.putInt("nmw_count", _count);
            editor.commit();

            // update view
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
            views.setTextViewText(R.id.countWidget, String.valueOf(_count));

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), NewAppWidget.class.getName());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
            appWidgetManager.updateAppWidget(appWidgetIds[0], views);
            onUpdate(context, appWidgetManager, appWidgetIds);

            // play sound
            final MediaPlayer mp = MediaPlayer.create(context, R.raw.nicememewebsite);
            mp.start();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mp.release();
                }
            }, 2000);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

