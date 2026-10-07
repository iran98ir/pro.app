package app.vista.one;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.lang.ref.WeakReference;

public final class PreloadManager {

    private static final String BASE_URL = "https://www.rosha-24.ir/app/app1/";

    // WebView نگه‌داشته‌شده با WeakReference (بدون Memory Leak)
    private static WeakReference<WebView> cachedWebView = new WeakReference<>(null);

    private PreloadManager() {
        // No instance
    }

    /**
     * شروع Preload در پس‌زمینه
     */
    public static void preload(Context context, PreloadCallback callback) {
        Context appContext = context.getApplicationContext();

        // اگه از قبل WebView آماده داریم، دوباره لود نکن
        WebView existing = cachedWebView.get();
        if (existing != null) {
            if (callback != null) callback.onPageLoaded();
            return;
        }

        WebView wv = new WebView(appContext);
        setupWebView(wv);

        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // فقط صفحه‌ی اول
                if (url != null && url.startsWith(BASE_URL)) {
                    cachedWebView = new WeakReference<>(view);
                    if (callback != null) callback.onPageLoaded();
                }
            }

            @Override
            public void onReceivedError(WebView view,
                                         WebResourceRequest request,
                                         WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (request.isForMainFrame()) {
                    if (callback != null) callback.onPageFailed();
                }
            }
        });

        wv.loadUrl(BASE_URL);
    }

    /**
     * تنظیمات WebView برای Preload
     */
    private static void setupWebView(WebView wv) {
        WebSettings s = wv.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);

        // ✅ استراتژی Cache حرفه‌ای
        // اگه Cache معتبره، استفاده کن. اگه نه، از شبکه بگیر.
        s.setCacheMode(WebSettings.LOAD_DEFAULT);

        s.setUserAgentString(
            s.getUserAgentString() + " MuMuApp/1.0"
        );

        // Cookies
        CookieManager cm = CookieManager.getInstance();
        cm.setAcceptCookie(true);
        cm.setAcceptThirdPartyCookies(wv, false);
    }

    /**
     * گرفتن WebView آماده — اگه آماده نبود، null برمی‌گردونه
     * (فقط یک بار قابل گرفتنه)
     */
    public static WebView takeWebView() {
        WebView wv = cachedWebView.get();
        cachedWebView = new WeakReference<>(null);
        return wv;
    }

    /**
     * آزاد کردن منابع
     */
    public static void destroy() {
        WebView wv = cachedWebView.get();
        if (wv != null) {
            try {
                wv.stopLoading();
                wv.loadUrl("about:blank");
                wv.removeAllViews();
                wv.destroy();
            } catch (Exception ignored) {
            }
        }
        cachedWebView = new WeakReference<>(null);
    }

    /**
     * Callback برای Preload
     */
    public interface PreloadCallback {
        void onPageLoaded();
        void onPageFailed();
    }
}
