package app.vista.one;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.lang.ref.WeakReference;
import java.net.URLDecoder;

public class CustomWebViewClient extends WebViewClient {

    private static final String BASE_DOMAIN = "rosha-24.ir";
    private static final String BASE_URL = "https://www.rosha-24.ir/app/app1/";

    private final WeakReference<Context> contextRef;

    public CustomWebViewClient(Context context) {
        this.contextRef = new WeakReference<>(context);
    }

    // ============================================
    // مدیریت لینک‌ها
    // ============================================
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return handleUrl(view, request.getUrl().toString());
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return handleUrl(view, url);
    }

    private boolean handleUrl(WebView view, String url) {
        if (url == null || url.isEmpty()) return true;

        // ============================================
        // ۱. intent:// → استخراج URL و لود داخل WebView
        // ============================================
        if (url.startsWith("intent:")) {
            String extractedUrl = extractUrlFromIntent(url);
            if (extractedUrl != null && !extractedUrl.isEmpty()) {
                view.loadUrl(extractedUrl);
            }
            return true;
        }

        // ============================================
        // ۲. پروتکل‌های ناشناخته → جلوگیری از نمایش خطا
        // ============================================
        if (url.startsWith("chrome:")
            || url.startsWith("about:")
            || url.startsWith("chrome-native:")
            || url.startsWith("file://")
            || url.startsWith("data:")
            || url.startsWith("blob:")) {
            return true;
        }

        // ============================================
        // ۳. دامنه‌ی داخلی → داخل WebView
        // ============================================
        if (url.contains(BASE_DOMAIN)) {
            return false;
        }

        Context context = contextRef.get();
        if (context == null) return true;

        // ============================================
        // ۴. لینک‌های خاص (tel, mailto, ...)
        // ============================================
        try {
            if (url.startsWith("tel:")
                || url.startsWith("mailto:")
                || url.startsWith("sms:")
                || url.startsWith("whatsapp:")
                || url.startsWith("tg:")
                || url.startsWith("instagram:")
                || url.startsWith("market:")) {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return true;
            }

            // ============================================
            // ۵. لینک خارجی → مرورگر
            // ============================================
            if (url.startsWith("http://") || url.startsWith("https://")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return true;
            }

        } catch (Exception ignored) {
        }

        // هر چیز ناشناخته → جلوش رو بگیر
        return true;
    }

    // ============================================
    // استخراج URL از intent://
    // ============================================
    private String extractUrlFromIntent(String intentUrl) {
        try {
            String workUrl = intentUrl.substring("intent:".length());

            String urlPart;
            int hashIndex = workUrl.indexOf("#Intent;");
            if (hashIndex > 0) {
                urlPart = workUrl.substring(0, hashIndex);
            } else {
                urlPart = workUrl;
            }

            try {
                urlPart = URLDecoder.decode(urlPart, "UTF-8");
            } catch (Exception ignored) {
            }

            if (urlPart.startsWith("http://") || urlPart.startsWith("https://")) {
                return urlPart;
            }

            if (urlPart.startsWith("www.") || urlPart.contains(".")) {
                return "https://" + urlPart;
            }

            if (urlPart.startsWith("/")) {
                return BASE_URL.substring(0, BASE_URL.length() - 1) + urlPart;
            }

            return urlPart;

        } catch (Exception e) {
            return null;
        }
    }

    // ============================================
    // مدیریت خطا — جلوگیری از نمایش خطای خام
    // ============================================
    @Override
    public void onReceivedError(WebView view,
                                 WebResourceRequest request,
                                 WebResourceError error) {
        super.onReceivedError(view, request, error);

        // فقط خطای main frame مهمه
        if (!request.isForMainFrame()) return;

        Context context = contextRef.get();
        if (!(context instanceof MainActivity)) return;

        MainActivity activity = (MainActivity) context;
        int errorCode = error != null ? error.getErrorCode() : 0;

        // اگه نت قطع باشه → آفلاین
        if (!NetworkUtils.isNetworkAvailable(activity)) {
            activity.goToOffline();
            return;
        }

        // خطای unknown scheme → نادیده بگیر (سایت مشکل داره)
        if (errorCode == WebViewClient.ERROR_UNSUPPORTED_SCHEME
            || errorCode == WebViewClient.ERROR_UNKNOWN) {
            // تلاش کن به صفحه اصلی برگردی
            activity.goToError(0);
            return;
        }

        // خطای شبکه
        if (errorCode == WebViewClient.ERROR_HOST_LOOKUP
            || errorCode == WebViewClient.ERROR_CONNECT
            || errorCode == WebViewClient.ERROR_TIMEOUT) {
            activity.goToError(0);
            return;
        }

        // هر خطای دیگه
        activity.goToError(0);
    }

    @Override
    public void onReceivedHttpError(WebView view,
                                     WebResourceRequest request,
                                     WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);

        if (request.isForMainFrame() && errorResponse != null) {
            int status = errorResponse.getStatusCode();
            if (status >= 400) {
                Context context = contextRef.get();
                if (context instanceof MainActivity) {
                    ((MainActivity) context).goToError(status);
                }
            }
        }
    }

    @Override
    public void onReceivedSslError(WebView view,
                                    android.webkit.SslErrorHandler handler,
                                    android.net.http.SslError error) {
        handler.cancel();
        Context context = contextRef.get();
        if (context instanceof MainActivity) {
            ((MainActivity) context).goToError(0);
        }
    }
}
