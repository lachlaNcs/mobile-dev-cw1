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

        return parseXml(result);
    }

    private List<CurrencyRate> parseXml(String xmlString) {
        List<CurrencyRate> allCurrencyRates = new ArrayList<>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlString));

            int eventType = xpp.getEventType();
            boolean insideItem = false;
            CurrencyRate currentRate = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = xpp.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("item".equalsIgnoreCase(tagName)) {
                            insideItem = true;
                            currentRate = new CurrencyRate();
                        } else if (insideItem && "link".equalsIgnoreCase(tagName)) {
                            // Extract the Country Code from the URL
                            String link = xpp.nextText();
                            int start = link.lastIndexOf("/") + 1;
                            int end = link.lastIndexOf(".");

                            if (start > 0 && end > start) {
                                currentRate.setCountryCode(link.substring(start, end).toUpperCase());
                            }
                        } else if (insideItem && "description".equalsIgnoreCase(tagName)) {
                            String desc = xpp.nextText().trim();
                            // Example: "1 British Pound Sterling = 1.3309 US Dollar"
                            String[] parts = desc.split("=");
                            if (parts.length == 2) {
                                String rhs = parts[1].trim(); // "1.3309 US Dollar
                                String[] rateAndName = rhs.split(" ", 2);
                                if (rateAndName.length == 2) {
                                    try {
                                        currentRate.setRateToGbp(Double.parseDouble(rateAndName[0]));
                                    } catch (NumberFormatException e) {
                                        currentRate.setRateToGbp(0.0);
                                    }
                                    currentRate.setCurrencyName(rateAndName[1]);
                                }
                            }
                        } else if (insideItem && "pubDate".equalsIgnoreCase(tagName)) {
                            currentRate.setLastUpdated(xpp.nextText().trim());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("item".equalsIgnoreCase(tagName) && insideItem && currentRate != null) {
                            allCurrencyRates.add(currentRate);
                            insideItem = false;
                        }
                        break;
                }
                eventType = xpp.next();
            }

        } catch (XmlPullParserException | IOException e) {
                Log.e("parseXml()", "Exception while parsing: ", e);
            }

        Log.d("parseXml()", "Parsed " + allCurrencyRates.size() + "currencies");
        return allCurrencyRates;
    }
}
