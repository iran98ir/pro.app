package app.vista.one;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

public class CustomWebViewClient extends WebViewClient {

    private static final String BASE_DOMAIN = "rosha-24.ir";

    private final WeakReference<Context> contextRef;

    public CustomWebViewClient(Context context) {
        this.contextRef = new WeakReference<>(context);
    }

    // ============================================
    // مدیریت لینک‌ها
    // ============================================
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return handleUrl(request.getUrl().toString());
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return handleUrl(url);
    }

    private boolean handleUrl(String url) {
        Context context = contextRef.get();
        if (context == null) return false;

        if (url == null || url.isEmpty()) return false;

        // ۱. دامنه‌ی داخلی → داخل WebView
        if (url.contains(BASE_DOMAIN)) {
            return false;
        }

        // ۲. لینک‌های خاص → باز کردن در اپ مناسب
        try {
            if (url.startsWith("tel:")
                || url.startsWith("mailto:")
                || url.startsWith("sms:")
                || url.startsWith("whatsapp:")
                || url.startsWith("tg:")
                || url.startsWith("instagram:")
                || url.startsWith("intent:")
                || url.startsWith("market:")) {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return true;
            }

            // ۳. لینک‌های خارجی (http/https) → مرورگر
            if (url.startsWith("http://") || url.startsWith("https://")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return true;
            }

        } catch (Exception e) {
            return false;
        }

        return false;
    }

    // ============================================
    // مدیریت خطاها
    // ============================================
    @Override
    public void onReceivedError(WebView view,
                                 WebResourceRequest request,
                                 WebResourceError error) {
        super.onReceivedError(view, request, error);

        if (request.isForMainFrame()) {
            Context context = contextRef.get();
            if (context instanceof MainActivity) {
                MainActivity activity = (MainActivity) context;

                if (!NetworkUtils.isNetworkAvailable(activity)) {
                    activity.goToOffline();
                } else {
                    activity.goToError(0);
                }
            }
        }
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

    // ============================================
    // مدیریت SSL Errors (رد کردن خطاهای SSL ناامن)
    // ============================================
    @Override
    public void onReceivedSslError(WebView view,
                                    android.webkit.SslErrorHandler handler,
                                    @NonNull android.net.http.SslError error) {
        // فقط HTTPS معتبر رو قبول کن — به هیچ وجه proceed نکن
        handler.cancel();

        Context context = contextRef.get();
        if (context instanceof MainActivity) {
            ((MainActivity) context).goToError(0);
        }
    }
}
