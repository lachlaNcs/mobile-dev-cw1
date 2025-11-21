package org.me.gcu.mcmillan_mark_s2432525.viewmodel;

import android.os.Message;
import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.me.gcu.mcmillan_mark_s2432525.data.CurrencyRepository;
import org.me.gcu.mcmillan_mark_s2432525.model.CurrencyRate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RatesViewModel extends ViewModel {
    private static final String TAG = "RatesViewModel";
    private final CurrencyRepository repository = new CurrencyRepository();
    private List<CurrencyRate> cachedRates;
    private final MutableLiveData<List<CurrencyRate>> allRates = new MutableLiveData<>();
    private final MutableLiveData<List<CurrencyRate>> filteredRates = new MutableLiveData<>();

    public LiveData<List<CurrencyRate>> getFilteredRates() {
        return filteredRates;
    }

    public void fetchRates(Handler handler) {
        Thread thread = new Thread(() -> {
            Log.d(TAG, "Running fetchRates() on: " + Thread.currentThread().getName());

            List<CurrencyRate> rates = repository.fetchRates();

            if (rates != null && !rates.isEmpty()) {
                cachedRates = rates;

                allRates.postValue(rates);
                filteredRates.postValue(rates);

                Message successMsg = handler.obtainMessage(1, rates);
                handler.sendMessage(successMsg);
                return;
            }

            Log.e(TAG, "fetchRates(): repository returned empty list");

            if (cachedRates != null && !cachedRates.isEmpty()) {
                Log.w(TAG, "Using cached offline data");

                allRates.postValue(cachedRates);
                filteredRates.postValue(cachedRates);

                Message offlineMsg = handler.obtainMessage(-1, "No internet connection. Please check your network and try again.");
                handler.sendMessage(offlineMsg);
                return;
            }

            Message errorMsg = handler.obtainMessage(-1, "No internet connection and no cached data available");
            handler.sendMessage(errorMsg);
        });

        thread.start();
    }

    public void filterRates(String query) {
        if (cachedRates == null || cachedRates.isEmpty()) return;

        if (query == null || query.trim().isEmpty()) {
            filteredRates.postValue(cachedRates);
            return;
        }

        String lower = query.trim().toLowerCase(Locale.ROOT);
        List<CurrencyRate> result = new ArrayList<>();

        for (CurrencyRate rate : cachedRates) {
            if (rate.getCurrencyName().toLowerCase(Locale.ROOT).contains(lower)
                || rate.getCountryCode().toLowerCase(Locale.ROOT).contains(lower)) {
                result.add(rate);
            }
        }

        filteredRates.postValue(result);
    }

    public List<CurrencyRate> getCachedRates() {
        return cachedRates;
    }
}
