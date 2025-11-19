package org.me.gcu.mcmillan_mark_s2432525.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.me.gcu.mcmillan_mark_s2432525.R;
import org.me.gcu.mcmillan_mark_s2432525.model.CurrencyRate;
import org.me.gcu.mcmillan_mark_s2432525.viewmodel.ConversionViewModel;

import java.util.Locale;

public class CurrencyConverterFragment extends BottomSheetDialogFragment {

    private ConversionViewModel viewModel;

    private TextView txtPairTitle;
    private TextView txtRateInfo;
    private TextView txtFromCode;
    private TextView txtToCode;
    private TextView txtToAmount;
    private EditText editFromAmount;
    private Button btnSwapDirection;
    private Button btnConvert;

    private CurrencyRate currentRate;
    private boolean isGbpToOther;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_currency_converter, container, false);

        txtPairTitle = view.findViewById(R.id.txtPairTitle);
        txtRateInfo = view.findViewById(R.id.txtRateInfo);
        txtFromCode = view.findViewById(R.id.txtFromCode);
        txtToCode = view.findViewById(R.id.txtToCode);
        txtToAmount = view.findViewById(R.id.txtToAmount);
        editFromAmount = view.findViewById(R.id.editFromAmount);
        btnSwapDirection = view.findViewById(R.id.btnSwapDirection);
        btnConvert = view.findViewById(R.id.btnConvert);

        viewModel = new ViewModelProvider(requireActivity()).get(ConversionViewModel.class);

        viewModel.getSelectedRate().observe(getViewLifecycleOwner(), rate -> {
            currentRate = rate;
            updateLabels();
        });

        viewModel.isGbpToOther().observe(getViewLifecycleOwner(), dir -> {
            isGbpToOther = dir;
            updateLabels();
            performConversion();
        });

        btnSwapDirection.setOnClickListener(v -> viewModel.toggleDirection());
        btnConvert.setOnClickListener(v -> performConversion());

        return view;
    }

    private void updateLabels() {
        if (currentRate == null) return;

        String other = currentRate.getCountryCode();
        double rate = currentRate.getRateToGbp();

        txtPairTitle.setText("GBP / " + other);
        txtRateInfo.setText(String.format(Locale.UK,
                "1 GBP = %.4f %s", rate, other));

        if (isGbpToOther) {
            txtFromCode.setText("GBP");
            txtToCode.setText(other);
            btnSwapDirection.setText("Swap GBP ⇄ " + other);
        } else {
            txtFromCode.setText(other);
            txtToCode.setText("GBP");
            btnSwapDirection.setText("Swap " + other + " ⇄ GBP");
        }
    }

    private void performConversion() {
        if (currentRate == null) return;

        String input = editFromAmount.getText().toString().trim();
        if (input.isEmpty()) {
            txtToAmount.setText("0.00");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            Log.e("CurrencyConverterFragment", "Number format exception: " + e);
            txtToAmount.setText("0.00");
            editFromAmount.setError("Please enter a valid number");
            return;
        }

        if (amount < 0) {
            Log.e("CurrencyConverterFragment", "Amount less than 0");
            txtToAmount.setText("0.00");
            editFromAmount.setError("Amount must be positive");
            return;
        }

        double rate = currentRate.getRateToGbp();
        double result = isGbpToOther ? amount * rate : amount / rate;

        txtToAmount.setText(String.format(Locale.UK, "%.2f", result));
    }
}
