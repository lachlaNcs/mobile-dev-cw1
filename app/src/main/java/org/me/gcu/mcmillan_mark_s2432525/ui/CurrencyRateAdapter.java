package org.me.gcu.mcmillan_mark_s2432525.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.me.gcu.mcmillan_mark_s2432525.R;
import org.me.gcu.mcmillan_mark_s2432525.model.CurrencyRate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CurrencyRateAdapter extends RecyclerView.Adapter<CurrencyRateAdapter.RateViewHolder> {
    private final List<CurrencyRate> items = new ArrayList<>();

    @NonNull
    @Override
    public RateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_currency_rate, parent, false);
        return new RateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RateViewHolder holder, int position) {
        CurrencyRate rate = items.get(position);

        holder.codeAndName.setText(
                String.format(Locale.UK, "%s (%s)",
                        rate.getCountryCode(),
                        rate.getCurrencyName())
        );

        holder.rate.setText(
                String.format(Locale.UK, "1 GBP = %.4f", rate.getRateToGbp())
        );

        double val = rate.getRateToGbp();
        int colorRes;

        if (val < 1.0) {
            colorRes = R.color.rate_very_strong;
        } else if (val < 5.0 ) {
            colorRes = R.color.rate_strong;
        } else if (val < 10.0) {
            colorRes = R.color.rate_moderate;
        } else {
            colorRes = R.color.rate_weak;
        }

        holder.rate.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), colorRes));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<CurrencyRate> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    static class RateViewHolder extends RecyclerView.ViewHolder {
        final TextView codeAndName;
        final TextView rate;

        RateViewHolder(@NonNull View itemView) {
            super(itemView);
            codeAndName = itemView.findViewById(R.id.textCodeAndName);
            rate = itemView.findViewById(R.id.textRate);
        }
    }
}
