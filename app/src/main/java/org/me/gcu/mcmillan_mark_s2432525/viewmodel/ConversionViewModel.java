package org.me.gcu.mcmillan_mark_s2432525.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.me.gcu.mcmillan_mark_s2432525.model.CurrencyRate;

public class ConversionViewModel extends ViewModel {
    private final MutableLiveData<CurrencyRate> selectedRate = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isGbpToOther = new MutableLiveData<>(true);

    public LiveData<CurrencyRate> getSelectedRate() {
        return selectedRate;
    }

    public void setSelectedRate(CurrencyRate rate) {
        selectedRate.postValue(rate);
    }

    public LiveData<Boolean> isGbpToOther() {
        return isGbpToOther;
    }

    public void toggleDirection() {
        Boolean curr = isGbpToOther.getValue();
        isGbpToOther.setValue(curr == null ? true : !curr);
    }
}
