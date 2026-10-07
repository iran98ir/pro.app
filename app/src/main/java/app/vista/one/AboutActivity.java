package app.vista.one;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AboutActivity extends AppCompatActivity {

    private ImageView btnBack;
    private ImageView appLogo;
    private TextView appName;
    private TextView appVersion;
    private TextView appDescription;
    private TextView textCopyright;
    private LinearLayout itemWebsite;
    private LinearLayout itemContact;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setupSafeArea();
        bindViews();
        loadInfo();
        setupListeners();
    }

    // ============================================
    // اتصال ویوها
    // ============================================
    private void bindViews() {
        btnBack = findViewById(R.id.btnBack);
        appLogo = findViewById(R.id.appLogo);
        appName = findViewById(R.id.appName);
        appVersion = findViewById(R.id.appVersion);
        appDescription = findViewById(R.id.appDescription);
        textCopyright = findViewById(R.id.textCopyright);
        itemWebsite = findViewById(R.id.itemWebsite);
        itemContact = findViewById(R.id.itemContact);
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
    // بارگذاری اطلاعات اپ
    // ============================================
    private void loadInfo() {
        appName.setText(R.string.app_name);

        try {
            String version = getPackageManager()
                .getPackageInfo(getPackageName(), 0).versionName;
            appVersion.setText(getString(R.string.app_version_label) + " " + version);
        } catch (Exception e) {
            appVersion.setText(getString(R.string.app_version_label) + " 1.0.0");
        }

        appDescription.setText(R.string.about_description);
        textCopyright.setText(R.string.about_copyright);
    }

    // ============================================
    // Listeners
    // ============================================
    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        itemWebsite.setOnClickListener(v ->
            openUrl(getString(R.string.about_website_url))
        );

        itemContact.setOnClickListener(v ->
            openUrl("https://www.rosha-24.ir/app/app1/contact")
        );
    }

    private void openUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception ignored) {
        }
    }
}
