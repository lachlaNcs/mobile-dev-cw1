package org.me.gcu.mcmillan_mark_s2432525.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.me.gcu.mcmillan_mark_s2432525.R;
import org.me.gcu.mcmillan_mark_s2432525.model.CurrencyRate;
import org.me.gcu.mcmillan_mark_s2432525.viewmodel.RatesViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AllRatesFragment extends Fragment {
    private RatesViewModel viewModel;
    private TextView statusText;
    private TextView lastUpdatedText;
    private RecyclerView ratesRecyclerView;
    private EditText searchInput;
    private SwipeRefreshLayout swipeRefresh;
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
        lastUpdatedText = root.findViewById(R.id.lastUpdatedText);
        ratesRecyclerView = root.findViewById(R.id.ratesRecyclerView);
        searchInput = root.findViewById(R.id.searchInput);
        swipeRefresh = root.findViewById(R.id.swipeRefresh);



        adapter = new CurrencyRateAdapter();
        ratesRecyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(RatesViewModel.class);

        viewModel.getFilteredRates().observe(getViewLifecycleOwner(), adapter::setItems);

        List<CurrencyRate> cached = viewModel.getCachedRates();
        if (cached != null && !cached.isEmpty()) {
            Log.d("Persistence: AllRatesFragment", "Loaded cached data from viewmodel");
            statusText.setText("Fetched " + cached.size() + " currencies:" );

            String latest = cached.get(0).getLastUpdated();

            try {
                SimpleDateFormat in = new SimpleDateFormat("EEE MMM d yyyy H:mm:ss 'UTC'", Locale.ENGLISH);
                SimpleDateFormat out = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.UK);
                Date date = in.parse(latest);
                lastUpdatedText.setText("Last updated: " + out.format(date));
            } catch (ParseException e) {
                lastUpdatedText.setText("Last updated: " + latest);
            }

            adapter.setItems(cached);
        } else {
            Log.d("Persistence: AllRatesFragment", "No cached data, fetching...");
            statusText.setText("Fetching data...");
            viewModel.fetchRates(handler);
        }

        swipeRefresh.setOnRefreshListener(() -> {
            Log.i("onRefresh()", "onRefresh event detected");
            statusText.setText("Refreshing data...");
            searchInput.setText("");
            viewModel.fetchRates(handler);
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.filterRates(s.toString());
            }
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
            swipeRefresh.setRefreshing(false);
            if (msg.what == 1) {
                List<CurrencyRate> rates = (List<CurrencyRate>) msg.obj;
                if (rates == null || rates.isEmpty()) {
                    statusText.setText("No data received, please try again later.");
                    lastUpdatedText.setText("Last updated: N/A");
                    adapter.setItems(null);
                } else {
                    statusText.setText("Fetched " + rates.size() + " currencies:");
                    String latest = rates.get(0).getLastUpdated();
                    try {
                        SimpleDateFormat in = new SimpleDateFormat("EEE MMM d yyyy H:mm:ss 'UTC'", Locale.ENGLISH);
                        SimpleDateFormat out = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.UK);
                        Date date = in.parse(latest);
                        lastUpdatedText.setText("Last updated: " + out.format(date));
                    } catch (ParseException e) {
                        lastUpdatedText.setText("Last updated: " + latest);
                    }
                    adapter.setItems(rates);
                }
            }
        }
    };
}