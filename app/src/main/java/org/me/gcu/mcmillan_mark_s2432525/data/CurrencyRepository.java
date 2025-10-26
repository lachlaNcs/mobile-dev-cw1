package org.me.gcu.mcmillan_mark_s2432525.data;

import android.util.Log;

import org.me.gcu.mcmillan_mark_s2432525.model.CurrencyRate;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CurrencyRepository {
    private static final String SOURCE_URL = "https://www.fx-exchange.com/gbp/rss.xml";
    private String result;
    public List<CurrencyRate> fetchRates() {
        URL url;
        HttpURLConnection httpURLConnection;
        BufferedReader in = null;
        String inputLine = "";

        Log.d("fetchRates()", "in repository");

        try {
            url = new URL(SOURCE_URL);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            while ((inputLine = in.readLine()) != null) {
                result = result + inputLine;
            }
            in.close();
        } catch(IOException e) {
            Log.e("fetchRates()", "IOException");
        }

        int i = result.indexOf("<?");
        result = result.substring(i);

        i = result.indexOf("</rss>");
        result = result.substring(0, i + 6);

        parseXml(result);
    }

    private List<CurrencyRate> parseXml(String xmlString) {
        CurrencyRate currencyRate = null;
        ArrayList<CurrencyRate> allCurrencyRates = new ArrayList<>();
        try {
            boolean insideCurrencyRate = false;
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(result));
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("item")) {
                        insideCurrencyRate = true;
                        currencyRate = new CurrencyRate();
                        Log.d("parseXml()", "Currency Rate found!");
                    }
                    else if (xpp.getName().equalsIgnoreCase("title")) {

                    }
                    else if (xpp.getName().equalsIgnoreCase("description")) {

                    }
                    else if (xpp.getName().equalsIgnoreCase("pubDate")) {

                    }
                }
                else if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().equalsIgnoreCase("item")) {
                        allCurrencyRates.add(currencyRate);
                        insideCurrencyRate = false;
                        Log.d("parseXml()", "Parsing of an exchange rate complete!");
                    }
                }
                eventType = xpp.next();
            }
        } catch(XmlPullParserException | IOException e) {
            Log.e("parseXml()", "PullParser Exception: " + e);
            throw new RuntimeException();
        }
    }
}
