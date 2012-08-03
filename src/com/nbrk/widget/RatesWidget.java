package com.nbrk.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.Toast;
import junit.framework.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: X120e
 * Date: 11.05.12
 * Time: 9:10
 * To change this template use File | Settings | File Templates.
 */
public class RatesWidget extends AppWidgetProvider {
    //static fields
    static final String URL = "http://nationalbank.kz/rss/rates.xml";
    static final String KEY_FC = "title";
    static final String KEY_PRICE = "description";
    static final String KEY_PUBDATE = "pubDate";
    static final String KEY_INDEX = "index";

    private final String ACTION_WIDGET_RECEIVER = "com.nbrk.widget.ACTION_WIDGET_RECEIVER";
    private final RemoteViews ratesView = new RemoteViews("com.nbrk.widget", R.layout.widget_layout);

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
            loadRates(context);
        }
    }

    public class HttpQuery extends AsyncTask<String, String, ArrayList<HashMap<String,String>>> {

        private XMLParser parser;
        private Document doc;
        private ArrayList<HashMap<String,String>> rates;
        private Context context;

        public HttpQuery(Context context) {
            this.context = context;
            parser = new XMLParser();
            rates = new ArrayList<HashMap<String, String>>();
        }

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(String... urls) {
            String xml = parser.getXMLFromUrl(urls[0]);
            doc = parser.getDomElement(xml);

            NodeList nl = doc.getElementsByTagName("item");

            for (int i=0; i<nl.getLength(); i++) {

                HashMap<String, String> map = new HashMap<String, String>();
                Element e = (Element) nl.item(i);

                //Log.d("Curency", parser.getValue(e, KEY_FC)+" "+parser.getValue(e, KEY_PRICE)+" "+parser.getValue(e, KEY_INDEX));

                map.put(KEY_FC, parser.getValue(e, KEY_FC));
                map.put(KEY_PRICE, parser.getValue(e, KEY_PRICE));
                map.put(KEY_INDEX, parser.getValue(e, KEY_INDEX));
                map.put(KEY_PUBDATE, parser.getValue(e, KEY_PUBDATE));

                rates.add(map);
            }

            return rates;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String,String>> result) {
            super.onPostExecute(result);

            ratesView.removeAllViews(R.id.main_layout);

            for (HashMap<String, String> item:result) {
                RemoteViews ratesViewItem = new RemoteViews(context.getPackageName(),R.layout.rates_frame_layout);
                ratesViewItem.setTextViewText(R.id.currencyName, item.get(KEY_FC));
                ratesViewItem.setTextViewText(R.id.sellRate, item.get(KEY_PRICE));
                ratesViewItem.setImageViewResource(R.id.flag,getDrawable(context,item.get(KEY_FC)));
                ratesViewItem.setImageViewResource(R.id.arrow,getDrawable(context,item.get(KEY_INDEX)));

                ratesView.addView(R.id.main_layout, ratesViewItem);
            }

            ratesView.setOnClickPendingIntent(R.id.main_layout, createPendingIntent(context));

            ComponentName thisAppWidget = new ComponentName(context,RatesWidget.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(thisAppWidget, ratesView);
        }
    }

    public void loadRates(Context context){
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            new HttpQuery(context).execute(URL);
        } else {
            //RemoteViews messageView = new RemoteViews(context.getPackageName(),R.layout.message_layout);
            //ratesView.removeAllViews(R.id.main_layout);
            //ratesView.addView(R.id.main_layout, messageView);
            Toast.makeText(context, R.string.noConnection, Toast.LENGTH_LONG).show();
        }
    }

    public static int getDrawable(Context context, String name) {
        Assert.assertNotNull(context);
        Assert.assertNotNull(name);
        //Log.d("Flag: ", name + " " + context.getResources().getIdentifier(name,"drawable",context.getPackageName()));
        return context.getResources().getIdentifier(name.toLowerCase(),"drawable",context.getPackageName());
    }

}