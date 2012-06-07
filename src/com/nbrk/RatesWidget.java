package com.nbrk;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: X120e
 * Date: 11.05.12
 * Time: 9:10
 * To change this template use File | Settings | File Templates.
 */
public class RatesWidget extends AppWidgetProvider {

    private final String ACTION_WIDGET_RECEIVER = "com.nbrk.ACTION_WIDGET_RECEIVER";
    private final RemoteViews ratesView = new RemoteViews("com.nbrk", R.layout.widget_layout);

    private PendingIntent createPendingIntent(Context context) {
        Intent intent = new Intent(context, RatesWidget.class);
        intent.setAction(ACTION_WIDGET_RECEIVER);
        return PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(createPendingIntent(context));
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC,
                SystemClock.elapsedRealtime()+AlarmManager.INTERVAL_HOUR,
                AlarmManager.INTERVAL_HOUR,
                createPendingIntent(context));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(intent.getAction().equals(ACTION_WIDGET_RECEIVER)) {
            updateWidget(context);
        }
    }

    void updateWidget(Context context) {

        Parser parser = new Parser();
        ArrayList<Rates> ratesList = parser.getRates(context.getResources().getString(R.string.serviceURL));

        if (ratesList.isEmpty()) {
            ratesView.setTextViewText(R.id.message,context.getText(R.string.noConnection));
        } else {
            ratesView.setTextViewText(R.id.message,"");
            ratesView.removeAllViews(R.id.widget_layout);
        }

        for (Rates rates : ratesList) {
            Log.d("Curency", rates.getCurrency()+" "+rates.getRate()+" "+rates.getIndex());
            RemoteViews ratesViewItem = new RemoteViews(context.getPackageName(),R.layout.rates_frame_layout);
            if (rates.getIndex().equalsIgnoreCase("UP")) {
                ratesViewItem.setImageViewResource(R.id.arrow,R.drawable.up);
            } else {
                ratesViewItem.setImageViewResource(R.id.arrow,R.drawable.down);
            }
            ratesViewItem.setImageViewResource(R.id.flag,rates.getFlag(rates.getCurrency()));
            ratesViewItem.setTextViewText(R.id.currencyName, rates.getCurrency());
            ratesViewItem.setTextViewText(R.id.sellRate,rates.getRate());

            ratesView.addView(R.id.widget_layout, ratesViewItem);
        }

        ratesView.setOnClickPendingIntent(R.id.main_layout, createPendingIntent(context));

        ComponentName thisAppWidget = new ComponentName(context.getPackageName(),getClass().getName());
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(thisAppWidget, ratesView);
    }

}
