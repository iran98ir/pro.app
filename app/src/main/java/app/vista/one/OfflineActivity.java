package app.vista.one;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OfflineActivity extends AppCompatActivity {

    private Button btnRetry;
    private ConnectivityManager.NetworkCallback networkCallback;
    private boolean navigated = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);

        setupSafeArea();

        btnRetry = findViewById(R.id.btnRetry);

        btnRetry.setOnClickListener(v -> {
            if (NetworkUtils.isNetworkAvailable(this)) {
                goToMain();
            } else {
                // لرزش کوچیک برای بازخورد
                v.animate().scaleX(0.95f).scaleY(0.95f)
                    .setDuration(80)
                    .withEndAction(() -> v.animate()
                        .scaleX(1f).scaleY(1f).setDuration(80).start())
                    .start();
            }
        });

        setupNetworkMonitor();
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
    // Network Monitor — وقتی نت برگشت، خودکار برو
    // ============================================
    private void setupNetworkMonitor() {
        networkCallback = NetworkUtils.registerNetworkCallback(
            this,
            new NetworkUtils.NetworkMonitorCallback() {
                @Override
                public void onNetworkAvailable() {
                    runOnUiThread(() -> goToMain());
                }

                @Override
                public void onNetworkLost() {
                    // نادیده بگیر
                }
            }
        );
    }

    // ============================================
    // ناوبری به Main
    // ============================================
    private void goToMain() {
        if (navigated) return;
        navigated = true;

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("page_preloaded", false);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (networkCallback != null) {
            NetworkUtils.unregisterNetworkCallback(this, networkCallback);
            networkCallback = null;
        }
        super.onDestroy();
    }
}
