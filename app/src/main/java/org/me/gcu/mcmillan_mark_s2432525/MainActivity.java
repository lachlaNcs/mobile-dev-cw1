// Name                 Mark McMillan
// Student ID           S2432525
// Programme of Study   BSc (Hons) Software Development

package org.me.gcu.mcmillan_mark_s2432525;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import org.me.gcu.mcmillan_mark_s2432525.model.CurrencyRate;
import org.me.gcu.mcmillan_mark_s2432525.viewmodel.RatesViewModel;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView rawDataDisplay;
    private Button startButton;
    private RatesViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set up the raw links to the graphical components
        rawDataDisplay = (TextView)findViewById(R.id.rawDataDisplay);
        startButton = (Button)findViewById(R.id.startButton);
        viewModel = new ViewModelProvider(this).get(RatesViewModel.class);
        List<CurrencyRate> cached = viewModel.getCachedRates();
        if (cached != null && !cached.isEmpty()) {
            Log.d("Persistence", "Loaded cached data from ViewModel");
            displayRates(cached);
        } else {
            Log.d("Persistence", "No cached data, fetching...");
            viewModel.fetchRates(handler);
        }

        startButton.setOnClickListener(v -> {
            Log.d("ThreadCheck", "Button clicked on thread: " + Thread.currentThread().getName());
            rawDataDisplay.setText("Fetching data...");
            viewModel.fetchRates(handler);
        });

    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                List<CurrencyRate> rates = (List<CurrencyRate>) msg.obj;
                Log.d("Handler", "Received " + rates.size() + " rates");

                if (rates == null || rates.isEmpty()) {
                    rawDataDisplay.setText("No data received.");
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Fetched ").append(rates.size()).append(" currencies:\n\n");
                    displayRates(rates);
                }
            }
        }
    };

    private void displayRates(List<CurrencyRate> rates) {
        if (rates == null || rates.isEmpty()) {
            rawDataDisplay.setText("No data received.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Fetched ").append(rates.size()).append(" currencies:\n\n");

        for (int i = 0; i < rates.size(); i++) {
            CurrencyRate rate = rates.get(i);
            sb.append(rate.getCountryCode())
                    .append(" - ")
                    .append(rate.getCurrencyName())
                    .append(" = ")
                    .append(rate.getRateToGbp())
                    .append(" | ")
                    .append(rate.getLastUpdated())
                    .append("\n");

        }

        rawDataDisplay.setText(sb.toString());
    }
}