// Name                 Mark McMillan
// Student ID           S2432525
// Programme of Study   BSc (Hons) Software Development

package org.me.gcu.mcmillan_mark_s2432525;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;

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
import org.me.gcu.mcmillan_mark_s2432525.viewmodel.RatesViewModel;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String PREF_FILE_NAME = "user_settings";
    private static final String PREF_THEME_MODE = "theme_mode";
    private MenuItem themeToggleItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applySavedTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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