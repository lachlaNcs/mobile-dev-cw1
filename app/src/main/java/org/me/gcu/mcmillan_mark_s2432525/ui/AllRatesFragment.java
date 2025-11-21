package org.me.gcu.mcmillan_mark_s2432525.ui;

import android.animation.ValueAnimator;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.me.gcu.mcmillan_mark_s2432525.R;
import org.me.gcu.mcmillan_mark_s2432525.model.CurrencyRate;
import org.me.gcu.mcmillan_mark_s2432525.viewmodel.RatesViewModel;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AllRatesFragment extends Fragment {
    private static final String TAG = "AllRatesFragment";
    private static final String KEY_SIDEBAR_VISIBLE = "sidebar_visible";

    private static final int MSG_SUCCESS = 1;
    private static final int MSG_ERROR = -1;
    private boolean sidebarVisible = true;

    private RatesViewModel viewModel;
    private View sidebarContainer;
    private View cardMainUsd;
    private View cardMainEur;
    private View cardMainJpy;

    private TextView textMainUsdTitle;
    private TextView textMainUsdRate;
    private TextView textMainEurTitle;
    private TextView textMainEurRate;
    private TextView textMainJpyTitle;
    private TextView textMainJpyRate;

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
            Log.d(TAG, "Auto-refresh triggered");
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

        if (savedInstanceState != null) {
            sidebarVisible = savedInstanceState.getBoolean(KEY_SIDEBAR_VISIBLE, true);
        }

        statusText = root.findViewById(R.id.statusText);
        lastUpdatedText = root.findViewById(R.id.lastUpdatedText);
        ratesRecyclerView = root.findViewById(R.id.ratesRecyclerView);
        searchInput = root.findViewById(R.id.searchInput);
        swipeRefresh = root.findViewById(R.id.swipeRefresh);
        cardMainUsd = root.findViewById(R.id.cardMainUsd);
        cardMainEur = root.findViewById(R.id.cardMainEur);
        cardMainJpy = root.findViewById(R.id.cardMainJpy);

        sidebarContainer = root.findViewById(R.id.sidebarContainer);
        if (sidebarContainer != null) {
            ViewGroup.LayoutParams params = sidebarContainer.getLayoutParams();
            params.width = sidebarVisible ? dpToPx(120) : 0;
            sidebarContainer.setLayoutParams(params);
        }

        textMainUsdTitle = root.findViewById(R.id.textMainUsdTitle);
        textMainUsdRate  = root.findViewById(R.id.textMainUsdRate);
        textMainEurTitle = root.findViewById(R.id.textMainEurTitle);
        textMainEurRate  = root.findViewById(R.id.textMainEurRate);
        textMainJpyTitle = root.findViewById(R.id.textMainJpyTitle);
        textMainJpyRate  = root.findViewById(R.id.textMainJpyRate);


        adapter = new CurrencyRateAdapter((CurrencyRate rate) -> {
            if (getActivity() instanceof CurrencyRateAdapter.OnCurrencyClickListener) {
                ((CurrencyRateAdapter.OnCurrencyClickListener) getActivity())
                        .onCurrencyClicked(rate);
            }
        });

        ratesRecyclerView.setAdapter(adapter);
        swipeRefresh.setNestedScrollingEnabled(false);
        ratesRecyclerView.setNestedScrollingEnabled(false);

        viewModel = new ViewModelProvider(requireActivity()).get(RatesViewModel.class);

        viewModel.getFilteredRates().observe(getViewLifecycleOwner(), adapter::setItems);

        List<CurrencyRate> cached = viewModel.getCachedRates();
        if (cached != null && !cached.isEmpty()) {
            Log.d(TAG, "Loaded cached data from ViewModel");

            displayFetchedRates(cached);
            bindMainCurrencies(cached);

            adapter.setItems(cached);
        } else {
            Log.d(TAG, "No cached data, fetching...");
            statusText.setText("Fetching data...");
            viewModel.fetchRates(handler);
        }

        swipeRefresh.setOnRefreshListener(() -> {
            Log.i(TAG, "Swipe refresh triggered");
            statusText.setText("Refreshing data...");
            if (searchInput != null) {
                searchInput.setText("");
            }
            viewModel.fetchRates(handler);
        });

        if (searchInput != null) {
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
        }
        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_SIDEBAR_VISIBLE, sidebarVisible);
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

            switch (msg.what) {
                case MSG_SUCCESS:
                    List<CurrencyRate> rates = (List<CurrencyRate>) msg.obj;

                    if (rates == null || rates.isEmpty()) {
                        Log.d(TAG, "Handler received empty list");
                        statusText.setText("No data received. Try again later.");
                        lastUpdatedText.setText("Last updated: N/A");
                        adapter.setItems(Collections.emptyList());
                    } else {
                        displayFetchedRates(rates);
                        adapter.setItems(rates);
                        bindMainCurrencies(rates);
                    }
                    break;

                case MSG_ERROR:
                    Log.e(TAG, "Handler received error: " + msg.obj);
                    statusText.setText("Failed to fetch data.");
                    Toast.makeText(getContext(),
                            msg.obj != null ? msg.obj.toString() : "Network error",
                            Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Log.w(TAG, "Unknown handler message: " + msg.what);
                    break;
            }
        }
    };

    private void displayFetchedRates(List<CurrencyRate> rates) {
        statusText.setText("Fetched " + rates.size() + " currencies:");

        String latest = rates.get(0).getLastUpdated();

        if (latest == null || latest.isEmpty()) {
            lastUpdatedText.setText("Last updated: Unknown");
            return;
        }

        try {
            SimpleDateFormat in = new SimpleDateFormat("EEE MMM d yyyy H:mm:ss 'UTC'", Locale.ENGLISH);
            SimpleDateFormat out = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.UK);
            Date date = in.parse(latest);
            lastUpdatedText.setText("Last updated: " + out.format(date));
        } catch (Exception e) {
            Log.e(TAG, "Date parse failed: " + e.getMessage());
            lastUpdatedText.setText("Last updated: " + latest);
        }
    }

    private void bindMainCurrencies(List<CurrencyRate> rates) {
        if (rates == null || rates.isEmpty()) {
            cardMainUsd.setVisibility(View.GONE);
            cardMainEur.setVisibility(View.GONE);
            cardMainJpy.setVisibility(View.GONE);
            return;
        }

        CurrencyRate usd = null;
        CurrencyRate eur = null;
        CurrencyRate jpy = null;

        for (CurrencyRate rate : rates) {
            String code = rate.getCountryCode();
            if ("USD".equalsIgnoreCase(code)) {
                usd = rate;
            } else if ("EUR".equalsIgnoreCase(code)) {
                eur = rate;
            } else if ("JPY".equalsIgnoreCase(code)) {
                jpy = rate;
            }
        }

        bindMainCard(cardMainUsd, textMainUsdTitle, textMainUsdRate, usd);
        bindMainCard(cardMainEur, textMainEurTitle, textMainEurRate, eur);
        bindMainCard(cardMainJpy, textMainJpyTitle, textMainJpyRate, jpy);
    }

    private void bindMainCard(View card,
                              TextView titleView,
                              TextView rateView,
                              CurrencyRate rate) {

        if (rate == null) {
            card.setVisibility(View.GONE);
            return;
        }

        card.setVisibility(View.VISIBLE);

        String code = rate.getCountryCode();

        titleView.setText("GBP \u2192 " + code);

        rateView.setText(String.format(
                Locale.UK,
                "1 GBP = %.4f %s",
                rate.getRateToGbp(),
                code
        ));

        card.setOnClickListener(v -> {
            if (getActivity() instanceof CurrencyRateAdapter.OnCurrencyClickListener) {
                ((CurrencyRateAdapter.OnCurrencyClickListener) getActivity())
                        .onCurrencyClicked(rate);
            }
        });
    }

    public void toggleSidebar() {
        if (sidebarContainer == null) return;

        int start = sidebarContainer.getWidth();
        int end = sidebarVisible ? 0 : dpToPx(120);

        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(200);
        animator.addUpdateListener(anim -> {
            int val = (int) anim.getAnimatedValue();
            ViewGroup.LayoutParams params = sidebarContainer.getLayoutParams();
            params.width = val;
            sidebarContainer.setLayoutParams(params);
        });

        animator.start();
        sidebarVisible = !sidebarVisible;
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
