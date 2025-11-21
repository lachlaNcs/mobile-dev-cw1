package org.me.gcu.mcmillan_mark_s2432525.data;

import android.util.Log;

import org.me.gcu.mcmillan_mark_s2432525.model.CurrencyRate;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CurrencyRepository {
    private static final String TAG = "CurrencyRepository";
    private static final String SOURCE_URL = "https://www.fx-exchange.com/gbp/rss.xml";
    public List<CurrencyRate> fetchRates() {
        URL url;
        HttpURLConnection httpURLConnection = null;
        BufferedReader in = null;
        String inputLine = "";
        StringBuilder result = new StringBuilder();

        Log.d(TAG, "Starting fetchRates()");

        try {
            url = new URL(SOURCE_URL);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setReadTimeout(5000);

            int status = httpURLConnection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "HTTP error: " + status);
                return Collections.emptyList();
            }

            in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            while ((inputLine = in.readLine()) != null) {
                result.append(inputLine);
            }
        } catch(UnknownHostException e) {
            Log.e(TAG, "No internet connection: " + e.getMessage());
            return Collections.emptyList();
        } catch(SocketTimeoutException e) {
            Log.e(TAG, "Connection timed out: " + e.getMessage());
            return Collections.emptyList();
        }
        catch(IOException e) {
            Log.e(TAG, "IOException error while fetching RSS feed" + e);
            return Collections.emptyList();
        } finally {
            try {
                if (in != null) in.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing reader: " + e);
            }
            if (httpURLConnection != null) httpURLConnection.disconnect();
        }

        if (result.length() == 0) {
            Log.e(TAG, "Empty response from server");
            return Collections.emptyList();
        }

        int start = result.indexOf("<?");
        int end = result.indexOf("</rss>");

        if (start < 0 || end < 0 || end + 6 > result.length()) {
            Log.e(TAG, "Invalid XML");
            return Collections.emptyList();
        }

        String trimmedXml = result.substring(start, end + 6);

        return parseXml(trimmedXml);
    }

    private List<CurrencyRate> parseXml(String xmlString) {
        List<CurrencyRate> allCurrencyRates = new ArrayList<>();

        if (xmlString == null || xmlString.isEmpty()) {
            Log.e(TAG, "parseXml() called with empty XML");
            return allCurrencyRates;
        }

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlString));

            boolean insideItem = false;
            CurrencyRate currentRate = null;

            int eventType = xpp.getEventType();

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
                            } else {
                                Log.w(TAG, "Unexpected link format: " + link);
                            }
                        } else if (insideItem && "description".equalsIgnoreCase(tagName)) {
                            String desc = xpp.nextText().trim();
                            // Example: "1 British Pound Sterling = 1.3309 US Dollar"
                            String[] parts = desc.split("=");
                            if (parts.length != 2) {
                                Log.w(TAG, "Unexpected <description> format: " + desc);
                                break;
                            }

                            String rhs = parts[1].trim(); // "1.3309 US Dollar
                            String[] rateAndName = rhs.split(" ", 2);

                            if (rateAndName.length != 2) {
                                Log.w(TAG, "Cannot split name & rate in: " + rhs);
                                break;
                            }

                            try {
                                currentRate.setRateToGbp(Double.parseDouble(rateAndName[0]));
                            } catch (NumberFormatException e) {
                                Log.e(TAG, "Invalid rate: " + rateAndName[0], e);
                                currentRate.setRateToGbp(0.0);
                            }

                            currentRate.setCurrencyName(rateAndName[1]);
                        } else if (insideItem && "pubDate".equalsIgnoreCase(tagName)) {
                            currentRate.setLastUpdated(xpp.nextText().trim());
                        }

                        break;

                    case XmlPullParser.END_TAG:
                        if ("item".equalsIgnoreCase(tagName) && insideItem) {
                            if (currentRate.getCountryCode() == null ||
                                    currentRate.getCurrencyName() == null) {
                                Log.w(TAG, "Skipping invalid item: " + currentRate);
                            } else {
                                allCurrencyRates.add(currentRate);
                            }
                            insideItem = false;
                        }

                        break;
                }
                eventType = xpp.next();
            }

        } catch (XmlPullParserException | IOException e) {
                Log.e("parseXml()", "Exception while parsing: ", e);
            }

        Log.d(TAG, "Parsed " + allCurrencyRates.size() + " currencies");
        return allCurrencyRates;
    }
}
