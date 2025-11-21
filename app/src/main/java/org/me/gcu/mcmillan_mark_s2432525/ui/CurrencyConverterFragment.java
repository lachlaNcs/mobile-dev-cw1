package org.me.gcu.mcmillan_mark_s2432525.ui;

import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.me.gcu.mcmillan_mark_s2432525.R;
import org.me.gcu.mcmillan_mark_s2432525.model.CurrencyRate;
import org.me.gcu.mcmillan_mark_s2432525.viewmodel.ConversionViewModel;

import java.util.Locale;

public class CurrencyConverterFragment extends BottomSheetDialogFragment {
    private static final String TAG = "CurrencyConverterFragment";

    private ConversionViewModel viewModel;

    private TextView countryCodePairText;
    private TextView exchangeRateText;
    private TextView fromCountryCodeText;
    private TextView toCountryCodeText;
    private TextView conversionResultText;
    private EditText fromCountryCodeAmountInput;
    private Button swapExchangeDirectionButton;

    private CurrencyRate currentRate;
    private boolean isGbpToOther;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_currency_converter, container, false);

        countryCodePairText = view.findViewById(R.id.countryCodePairText);
        exchangeRateText = view.findViewById(R.id.exchangeRateText);
        fromCountryCodeText = view.findViewById(R.id.fromCountryCodeText);
        toCountryCodeText = view.findViewById(R.id.toCountryCodeText);
        conversionResultText = view.findViewById(R.id.conversionResultText);
        fromCountryCodeAmountInput = view.findViewById(R.id.fromCountryCodeAmountInput);
        swapExchangeDirectionButton = view.findViewById(R.id.swapExchangeDirectionButton);

        viewModel = new ViewModelProvider(requireActivity()).get(ConversionViewModel.class);

        viewModel.getSelectedRate().observe(getViewLifecycleOwner(), rate -> {
            currentRate = rate;
            updateLabels();
            performConversion();
        });

        viewModel.isGbpToOther().observe(getViewLifecycleOwner(), dir -> {
            isGbpToOther = dir;
            updateLabels();
            performConversion();
        });

        swapExchangeDirectionButton.setOnClickListener(v -> viewModel.toggleDirection());
        fromCountryCodeAmountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {performConversion();}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        boolean isLandscape =
                getResources().getConfiguration().orientation
                        == Configuration.ORIENTATION_LANDSCAPE;

        if (!isLandscape) return; // Portrait works normally.

        Dialog dialog = getDialog();
        if (dialog == null) return;

        FrameLayout bottomSheet =
                dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

        if (bottomSheet == null) return;

        BottomSheetBehavior<FrameLayout> behavior =
                BottomSheetBehavior.from(bottomSheet);

        // Force the bottom sheet to full height
        bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        bottomSheet.requestLayout();

        // Fully expand it
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        // Skip collapsed state entirely
        behavior.setSkipCollapsed(true);

        // Prevent half-expanded behavior
        behavior.setFitToContents(true);
    }


    private void updateLabels() {
        if (currentRate == null) return;

        String other = currentRate.getCountryCode();
        double rate = currentRate.getRateToGbp();

        countryCodePairText.setText("GBP / " + other);

        exchangeRateText.setText(String.format(Locale.UK,
                "1 GBP = %.4f %s", rate, other));

        if (isGbpToOther) {
            fromCountryCodeText.setText("GBP");
            toCountryCodeText.setText(other);
            swapExchangeDirectionButton.setText("Swap GBP ⇄ " + other);
        } else {
            fromCountryCodeText.setText(other);
            toCountryCodeText.setText("GBP");
            swapExchangeDirectionButton.setText("Swap " + other + " ⇄ GBP");
        }
    }

    private void performConversion() {
        if (currentRate == null) return;

        String input = fromCountryCodeAmountInput.getText().toString().trim();
        if (input.isEmpty()) {
            conversionResultText.setText("0.00");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Number format exception: " + e);
            conversionResultText.setText("0.00");
            fromCountryCodeAmountInput.setError("Please enter a valid number");
            return;
        }

        if (amount < 0) {
            Log.e(TAG, "Amount less than 0");
            conversionResultText.setText("0.00");
            fromCountryCodeAmountInput.setError("Amount must be positive");
            return;
        }

        double rate = currentRate.getRateToGbp();

        if (!isGbpToOther && rate == 0) {
            conversionResultText.setText("0.00");
            Log.e(TAG, "Attempted conversion with 0 rateToGbp");
            return;
        }

        double result = isGbpToOther ? amount * rate : amount / rate;

        conversionResultText.setText(String.format(Locale.UK, "%.2f", result));
    }
}
