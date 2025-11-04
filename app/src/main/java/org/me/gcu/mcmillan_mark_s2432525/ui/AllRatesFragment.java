package org.me.gcu.mcmillan_mark_s2432525.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.me.gcu.mcmillan_mark_s2432525.R;
import org.me.gcu.mcmillan_mark_s2432525.model.CurrencyRate;
import org.me.gcu.mcmillan_mark_s2432525.viewmodel.RatesViewModel;

import java.util.List;

public class AllRatesFragment extends Fragment {
    private RatesViewModel viewModel;
    private TextView rawDataDisplay;
    private Button startButton;


    public AllRatesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_allrates, container, false);
        rawDataDisplay = root.findViewById(R.id.rawDataDisplay);
        startButton = root.findViewById(R.id.startButton);

        viewModel = new ViewModelProvider(requireActivity()).get(RatesViewModel.class);

        List<CurrencyRate> cached = viewModel.getCachedRates();
        if (cached != null && !cached.isEmpty()) {
            Log.d("Persistence: AllRatesFragment", "Loaded cached data from viewmodel");
            rawDataDisplay.setText(viewModel.formatRates(cached));
        } else {
            Log.d("Persistence: AllRatesFragment", "No cached data, fetching...");
            rawDataDisplay.setText("Fetching data...");
            viewModel.fetchRates(handler);
        }

        startButton.setOnClickListener(v -> {
            Log.d("ThreadCheck from AllRatesFragment", "Button clicked on Thread: " + Thread.currentThread().getName());
            rawDataDisplay.setText("Refreshing data...");
            viewModel.fetchRates(handler);
        });

        return root;
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                List<CurrencyRate> rates = (List<CurrencyRate>) msg.obj;
                Log.d("Handler in AllRatesFragment", "Received: " + rates.size() + " rates");
                rawDataDisplay.setText(viewModel.formatRates(rates));
            }
        }
    };
}