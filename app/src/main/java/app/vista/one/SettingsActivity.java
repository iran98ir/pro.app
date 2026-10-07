package app.vista.one;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    private ImageView btnBack;
    private Switch switchDarkMode;
    private Switch switchNotifications;
    private Switch switchSound;

    private LinearLayout itemPrivacy;
    private LinearLayout itemTerms;
    private LinearLayout itemShare;
    private LinearLayout itemAbout;

    private TextView textVersion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setupSafeArea();
        bindViews();
        loadSettings();
        setupListeners();
    }

    // ============================================
    // اتصال ویوها
    // ============================================
    private void bindViews() {
        btnBack = findViewById(R.id.btnBack);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchNotifications = findViewById(R.id.switchNotifications);
        switchSound = findViewById(R.id.switchSound);

        itemPrivacy = findViewById(R.id.itemPrivacy);
        itemTerms = findViewById(R.id.itemTerms);
        itemShare = findViewById(R.id.itemShare);
        itemAbout = findViewById(R.id.itemAbout);

        textVersion = findViewById(R.id.textVersion);
    }

    // ============================================
    // Safe Area
    // ============================================
    private void setupSafeArea() {
        View root = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            int bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            v.setPadding(0, top, 0, bottom);
            return insets;
        });
    }

    // ============================================
    // بارگذاری تنظیمات فعلی
    // ============================================
    private void loadSettings() {
        int darkMode = PrefsManager.getDarkMode(this);
        switchDarkMode.setChecked(darkMode == PrefsManager.DARK_MODE_ON);

        switchNotifications.setChecked(PrefsManager.isNotificationsEnabled(this));
        switchSound.setChecked(PrefsManager.isSoundEnabled(this));

        // نمایش نسخه
        try {
            String version = getPackageManager()
                .getPackageInfo(getPackageName(), 0).versionName;
            textVersion.setText(getString(R.string.app_version_label) + " " + version);
        } catch (Exception e) {
            textVersion.setText(getString(R.string.app_version_label) + " 1.0.0");
        }
    }

    // ============================================
    // Listeners
    // ============================================
    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        // Dark Mode
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int newMode = isChecked
                ? PrefsManager.DARK_MODE_ON
                : PrefsManager.DARK_MODE_OFF;

            PrefsManager.setDarkMode(this, newMode);

            AppCompatDelegate.setDefaultNightMode(
                isChecked
                    ? AppCompatDelegate.MODE_NIGHT_YES
                    : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        // Notifications
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) ->
            PrefsManager.setNotificationsEnabled(this, isChecked)
        );

        // Sound
        switchSound.setOnCheckedChangeListener((buttonView, isChecked) ->
            PrefsManager.setSoundEnabled(this, isChecked)
        );

        // Privacy
        itemPrivacy.setOnClickListener(v -> openUrl(
            "https://www.rosha-24.ir/app/app1/privacy"
        ));

        // Terms
        itemTerms.setOnClickListener(v -> openUrl(
            "https://www.rosha-24.ir/app/app1/terms"
        ));

        // Share
        itemShare.setOnClickListener(v -> shareApp());

        // About
        itemAbout.setOnClickListener(v -> {
            startActivity(new Intent(this, AboutActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    // ============================================
    // باز کردن URL
    // ============================================
    private void openUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                android.net.Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception ignored) {
        }
    }

    // ============================================
    // اشتراک‌گذاری اپ
    // ============================================
    private void shareApp() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT,
                getString(R.string.settings_share_text) +
                "\nhttps://www.rosha-24.ir/app/app1/");
            startActivity(Intent.createChooser(intent, getString(R.string.settings_share)));
        } catch (Exception ignored) {
        }
    }
        }
