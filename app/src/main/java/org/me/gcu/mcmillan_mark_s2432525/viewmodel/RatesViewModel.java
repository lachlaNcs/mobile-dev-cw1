package org.me.gcu.mcmillan_mark_s2432525.viewmodel;

import android.os.Message;
import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.me.gcu.mcmillan_mark_s2432525.data.CurrencyRepository;
import org.me.gcu.mcmillan_mark_s2432525.model.CurrencyRate;

import java.util.List;

public class RatesViewModel extends ViewModel {
    private final CurrencyRepository repository = new CurrencyRepository();
    private List<CurrencyRate> cachedRates;

    public void fetchRates(Handler handler) {
        Thread thread = new Thread(() -> {
            Log.d("RatesViewModel", "Running fetchRates() on thread: " +Thread.currentThread().getName());
            List<CurrencyRate> rates = repository.fetchRates();
            cachedRates = rates;

            Message message = handler.obtainMessage(1, rates);
            handler.sendMessage(message);
        });
        thread.start();
    }

    public List<CurrencyRate> getCachedRates() {
        return cachedRates;
    }
}
