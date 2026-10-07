package app.vista.one;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://www.rosha-24.ir/app/app1/";
    private static final long BACK_PRESS_INTERVAL = 2000L;

    private WebView webView;
    private ProgressBar progressBar;
    private FrameLayout rootLayout;

    private ConnectivityManager.NetworkCallback networkCallback;
    private boolean errorShown = false;
    private long lastBackPressTime = 0L;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupSafeArea();
        bindViews();

        boolean pagePreloaded = getIntent().getBooleanExtra("page_preloaded", false);

        // اگه نت نداریم → صفحه‌ی آفلاین
        if (!NetworkUtils.isNetworkAvailable(this)) {
            goToOffline();
            return;
        }

        // اگه Preload آماده بود، ازش استفاده کن
        if (pagePreloaded) {
            WebView cached = PreloadManager.takeWebView();
            if (cached != null) {
                attachCachedWebView(cached);
            } else {
                createAndLoadWebView();
            }
        } else {
            createAndLoadWebView();
        }

        setupBackPress();
        setupNetworkMonitor();
        handleDeepLink(getIntent());
    }

    // ============================================
    // اتصال ویوها
    // ============================================
    private void bindViews() {
        rootLayout = findViewById(R.id.rootLayout);
        progressBar = findViewById(R.id.progressBar);
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
    // WebView — ساخت و لود
    // ============================================
    @SuppressLint("SetJavaScriptEnabled")
    private void createAndLoadWebView() {
        webView = new WebView(this);
        webView.setLayoutParams(new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));
        rootLayout.addView(webView, 0);

        setupWebView(webView);
        webView.loadUrl(BASE_URL);
    }

    private void attachCachedWebView(WebView cached) {
        try {
            if (cached.getParent() != null) {
                ((ViewGroup) cached.getParent()).removeView(cached);
            }

            cached.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ));

            rootLayout.addView(cached, 0);
            webView = cached;

            // مطمئن شو WebViewClient و DownloadListener ست شده
            webView.setWebViewClient(new CustomWebViewClient(this));
            webView.setDownloadListener(new CustomDownloadListener(this));
            setupWebChromeClient(webView);
        } catch (Exception e) {
            createAndLoadWebView();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView(WebView wv) {
        WebSettings settings = wv.getSettings();

        // پایه
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);

        // ✅ استراتژی Cache حرفه‌ای
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        // Zoom غیرفعال
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);

        // Responsive
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setTextZoom(100);

        // Media
        settings.setMediaPlaybackRequiresUserGesture(false);

        // امنیت
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setSaveFormData(false);

        // User Agent
        settings.setUserAgentString(
            settings.getUserAgentString() + " MuMuApp/1.0"
        );

        // پس‌زمینه
        wv.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_main));

        // Cookies
        CookieManager cm = CookieManager.getInstance();
        cm.setAcceptCookie(true);
        cm.setAcceptThirdPartyCookies(wv, false);

        // Clients
        wv.setWebViewClient(new CustomWebViewClient(this));
        wv.setDownloadListener(new CustomDownloadListener(this));
        setupWebChromeClient(wv);
    }

    private void setupWebChromeClient(WebView wv) {
        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                progressBar.setVisibility(
                    newProgress < 100 ? View.VISIBLE : View.GONE
                );
            }
        });
    }

    // ============================================
    // Back Press هوشمند
    // ============================================
    private void setupBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (errorShown) {
                    showExitDialog();
                    return;
                }

                if (webView != null && webView.canGoBack()) {
                    webView.goBack();
                    return;
                }

                long now = System.currentTimeMillis();
                if (now - lastBackPressTime < BACK_PRESS_INTERVAL) {
                    finish();
                    return;
                }

                lastBackPressTime = now;
                showExitDialog();
            }
        });
    }

    // ============================================
    // دیالوگ خروج
    // ============================================
    private void showExitDialog() {
        new AlertDialog.Builder(this, R.style.Dialog_Custom)
            .setTitle(R.string.exit_title)
            .setMessage(R.string.exit_message)
            .setPositiveButton(R.string.exit_yes, (d, w) -> finish())
            .setNegativeButton(R.string.exit_no, (d, w) -> d.dismiss())
            .show();
    }

    // ============================================
    // Network Monitor
    // ============================================
    private void setupNetworkMonitor() {
        networkCallback = NetworkUtils.registerNetworkCallback(
            this,
            new NetworkUtils.NetworkMonitorCallback() {
                @Override
                public void onNetworkAvailable() {
                    runOnUiThread(() -> {
                        if (webView != null && errorShown) {
                            errorShown = false;
                            webView.reload();
                        }
                    });
                }

                @Override
                public void onNetworkLost() {
                    runOnUiThread(() -> {
                        // اگه از قبل خطا نشون داده، دوباره نرو
                    });
                }
            }
        );
    }

    // ============================================
    // Deep Link
    // ============================================
    private void handleDeepLink(Intent intent) {
        if (intent == null || intent.getData() == null) return;
        Uri data = intent.getData();
        if (webView != null && data != null) {
            webView.loadUrl(data.toString());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleDeepLink(intent);
    }

    // ============================================
    // ناوبری به صفحه‌ی خطا (از WebViewClient)
    // ============================================
    public void goToOffline() {
        Intent intent = new Intent(this, OfflineActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    public void goToError(int statusCode) {
        errorShown = true;
        Intent intent = new Intent(this, ErrorActivity.class);
        intent.putExtra("status_code", statusCode);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    // ============================================
    // Lifecycle
    // ============================================
    @Override
    protected void onResume() {
        super.onResume();
        errorShown = false;
        if (webView != null) {
            webView.onResume();
            webView.resumeTimers();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
            webView.pauseTimers();
        }
    }

    @Override
    protected void onDestroy() {
        // لغو Network Callback
        if (networkCallback != null) {
            NetworkUtils.unregisterNetworkCallback(this, networkCallback);
            networkCallback = null;
        }

        // آزادسازی WebView
        if (webView != null) {
            try {
                ViewGroup parent = (ViewGroup) webView.getParent();
                if (parent != null) parent.removeView(webView);
                webView.stopLoading();
                webView.loadUrl("about:blank");
                webView.removeAllViews();
                webView.destroy();
            } catch (Exception ignored) {
            }
            webView = null;
        }

        super.onDestroy();
    }
                }
