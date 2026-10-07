package app.vista.one;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ErrorActivity extends AppCompatActivity {

    private TextView errorTitle;
    private TextView errorDescription;
    private Button btnRetry;
    private TextView btnSupport;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        setupSafeArea();
        bindViews();

        int statusCode = getIntent().getIntExtra("status_code", 0);
        applyErrorMessage(statusCode);

        btnRetry.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("page_preloaded", false);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });

        btnSupport.setOnClickListener(v -> openSupport());
    }

    private void bindViews() {
        errorTitle = findViewById(R.id.errorTitle);
        errorDescription = findViewById(R.id.errorDescription);
        btnRetry = findViewById(R.id.btnRetry);
        btnSupport = findViewById(R.id.btnSupport);
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
    // نمایش پیام مناسب بر اساس کد خطا
    // ============================================
    private void applyErrorMessage(int statusCode) {
        int titleRes;
        int descRes;

        switch (statusCode) {
            case 404:
                titleRes = R.string.error_404;
                descRes = R.string.error_404_desc;
                break;

            case 403:
                titleRes = R.string.error_403;
                descRes = R.string.error_403_desc;
                break;

            case 500:
                titleRes = R.string.error_500;
                descRes = R.string.error_500_desc;
                break;

            case 503:
                titleRes = R.string.error_503;
                descRes = R.string.error_503_desc;
                break;

            default:
                titleRes = R.string.error_load_failed;
                descRes = R.string.error_load_failed_desc;
                break;
        }

        errorTitle.setText(titleRes);
        errorDescription.setText(descRes);
    }

    // ============================================
    // تماس با پشتیبانی
    // ============================================
    private void openSupport() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(
                "https://www.rosha-24.ir/app/app1/contact"
            ));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception ignored) {
        }
    }

    // ============================================
    // Back → برگرد به Main
    // ============================================
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
