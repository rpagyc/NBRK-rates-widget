package com.nbrk;

import android.os.StrictMode;
import android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: X120e
 * Date: 11.05.12
 * Time: 10:06
 * To change this template use File | Settings | File Templates.
 */
class Parser {

    public ArrayList<Rates> getRates(String serviceURL) {

        String pubDate;
        String currency;
        String rate;
        String index;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ArrayList<Rates> ratesList = new ArrayList<Rates>();

        try {
            URL url = new URL(serviceURL);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(new InputSource(url.openStream()));

            document.getDocumentElement().normalize();

            NodeList nodeList = document.getElementsByTagName("item");

            for (int i=0; i<nodeList.getLength(); i++) {

                Element node = (Element)nodeList.item(i);
                currency = node.getElementsByTagName("title").item(0).getTextContent();
                pubDate = node.getElementsByTagName("pubDate").item(0).getTextContent();
                rate = node.getElementsByTagName("description").item(0).getTextContent();
                index = node.getElementsByTagName("index").item(0).getTextContent();

                Rates rates = new Rates();
                rates.setCurrency(currency);
                rates.setPubDate(pubDate);
                rates.setRate(rate);
                rates.setIndex(index);

                ratesList.add(rates);
            }

        } catch (Exception e) {
            Log.d("DEBUG", "XML parsing exception: " + e);
        }
        return  ratesList;
    }
}
