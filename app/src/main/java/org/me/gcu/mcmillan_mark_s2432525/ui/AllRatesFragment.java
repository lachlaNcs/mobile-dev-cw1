package org.me.gcu.mcmillan_mark_s2432525.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

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
    private TextView statusText;
    private Button startButton;
    private RecyclerView ratesRecyclerView;
    private CurrencyRateAdapter adapter;

    private final Handler autoUpdateHandler = new Handler(Looper.getMainLooper());
    private static final long UPDATE_INTERVAL = 60 * 60 * 1000;

    private final Runnable autoUpdateTask = new Runnable() {
        @Override
        public void run() {
            Log.d("AutoUpdate", "Refreshing data on interval, thread: " + Thread.currentThread().getName());
            statusText.setText("Auto-refreshing data...");
            viewModel.fetchRates(handler);
            autoUpdateHandler.postDelayed(this, UPDATE_INTERVAL);
        }
    };


    public AllRatesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_allrates, container, false);
        statusText = root.findViewById(R.id.statusText);
        startButton = root.findViewById(R.id.startButton);
        ratesRecyclerView = root.findViewById(R.id.ratesRecyclerView);

        adapter = new CurrencyRateAdapter();
        ratesRecyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(RatesViewModel.class);

        List<CurrencyRate> cached = viewModel.getCachedRates();
        if (cached != null && !cached.isEmpty()) {
            Log.d("Persistence: AllRatesFragment", "Loaded cached data from viewmodel");
            statusText.setText("Fetched " + cached.size() + " currencies:" );
            adapter.setItems(cached);
        } else {
            Log.d("Persistence: AllRatesFragment", "No cached data, fetching...");
            statusText.setText("Fetching data...");
            viewModel.fetchRates(handler);
        }

        startButton.setOnClickListener(v -> {
            Log.d("ThreadCheck from AllRatesFragment", "Button clicked on Thread: " + Thread.currentThread().getName());
            statusText.setText("Refreshing data...");
            viewModel.fetchRates(handler);
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        autoUpdateHandler.postDelayed(autoUpdateTask, UPDATE_INTERVAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        autoUpdateHandler.removeCallbacks(autoUpdateTask);
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                List<CurrencyRate> rates = (List<CurrencyRate>) msg.obj;
                if (rates == null || rates.isEmpty()) {
                    statusText.setText("No data received, please try again later.");
                    adapter.setItems(null);
                } else {
                    statusText.setText("Fetched " + rates.size() + " currencies:");
                    adapter.setItems(rates);
                }
            }
        }
    };
}