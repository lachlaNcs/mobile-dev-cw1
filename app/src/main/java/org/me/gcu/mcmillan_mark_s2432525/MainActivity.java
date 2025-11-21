// Name                 Mark McMillan
// Student ID           S2432525
// Programme of Study   BSc (Hons) Software Development

package org.me.gcu.mcmillan_mark_s2432525;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import org.me.gcu.mcmillan_mark_s2432525.model.CurrencyRate;
import org.me.gcu.mcmillan_mark_s2432525.ui.AllRatesFragment;
import org.me.gcu.mcmillan_mark_s2432525.ui.CurrencyConverterFragment;
import org.me.gcu.mcmillan_mark_s2432525.ui.CurrencyRateAdapter;
import org.me.gcu.mcmillan_mark_s2432525.viewmodel.ConversionViewModel;

public class MainActivity extends AppCompatActivity implements CurrencyRateAdapter.OnCurrencyClickListener {
    private static final String PREF_FILE_NAME = "user_settings";
    private static final String PREF_THEME_MODE = "theme_mode";
    private MenuItem themeToggleItem;
    private ConversionViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applySavedTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(ConversionViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AllRatesFragment())
                    .commit();
        }
    }

    @Override
    public void onCurrencyClicked(CurrencyRate rate) {
        viewModel.setSelectedRate(rate);

        CurrencyConverterFragment f = new CurrencyConverterFragment();
        f.show(getSupportFragmentManager(), "converter");
    }

    private void applySavedTheme() {
        SharedPreferences prefs = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        int savedMode = prefs.getInt(PREF_THEME_MODE, AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(savedMode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        themeToggleItem = menu.findItem(R.id.action_toggle_theme);
        updateThemeIcon();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_toggle_theme) {
            toggleTheme();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleTheme() {
        boolean isDarkMode = isDarkMode();
        int newMode = isDarkMode
                ? AppCompatDelegate.MODE_NIGHT_NO
                : AppCompatDelegate.MODE_NIGHT_YES;

        SharedPreferences prefs = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(PREF_THEME_MODE, newMode).apply();

        AppCompatDelegate.setDefaultNightMode(newMode);

        updateThemeIcon();
    }

    private boolean isDarkMode() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    private void updateThemeIcon() {
        if (themeToggleItem == null) return;

        if (isDarkMode()) {
            themeToggleItem.setIcon(R.drawable.baseline_light_mode_24);
        } else {
            themeToggleItem.setIcon(R.drawable.baseline_dark_mode_24);
        }
    }
}