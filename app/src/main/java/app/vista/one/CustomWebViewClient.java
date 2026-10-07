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
        if (context == null || url == null || url.isEmpty()) return false;

        // ۱. intent:// → استخراج URL اصلی و باز کردن
        if (url.startsWith("intent:")) {
            return handleIntentUrl(context, url);
        }

        // ۲. دامنه‌ی داخلی → داخل WebView
        if (url.contains(BASE_DOMAIN)) {
            return false;
        }

        // ۳. لینک‌های خاص
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

            // ۴. لینک خارجی
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

    /**
     * مدیریت intent:// URL ها
     * از فرمت intent://path#Intent;scheme=https;package=...;end
     * URL اصلی رو استخراج می‌کنه و داخل WebView باز می‌کنه
     */
    private boolean handleIntentUrl(Context context, String url) {
        try {
            // استخراج بخش اول (بعد از intent://)
            String workUrl = url.replaceFirst("^intent://", "");

            // پیدا کردن #Intent
            int intentIndex = workUrl.indexOf("#Intent;");
            String urlPart = intentIndex > 0
                ? workUrl.substring(0, intentIndex)
                : workUrl;

            // استخراج scheme
            String scheme = "https";
            if (url.contains("scheme=")) {
                int schemeStart = url.indexOf("scheme=") + "scheme=".length();
                int schemeEnd = url.indexOf(";", schemeStart);
                if (schemeEnd > schemeStart) {
                    scheme = url.substring(schemeStart, schemeEnd);
                }
            }

            // ساخت URL کامل
            String finalUrl;
            if (urlPart.startsWith("http://") || urlPart.startsWith("https://")) {
                finalUrl = urlPart;
            } else if (urlPart.startsWith("www.") || urlPart.contains(".")) {
                finalUrl = scheme + "://" + urlPart;
            } else {
                finalUrl = scheme + "://" + urlPart;
            }

            // اگه دامنه‌ی داخلیه، داخل WebView باز کن
            if (finalUrl.contains(BASE_DOMAIN)) {
                return false; // WebView خودش باز می‌کنه
            }

            // وگرنه با مرورگر باز کن
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;

        } catch (Exception e) {
            return false;
        }
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

    @Override
    public void onReceivedSslError(WebView view,
                                    android.webkit.SslErrorHandler handler,
                                    @NonNull android.net.http.SslError error) {
        handler.cancel();
        Context context = contextRef.get();
        if (context instanceof MainActivity) {
            ((MainActivity) context).goToError(0);
        }
    }
                }
