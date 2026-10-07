package app.vista.one;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class CustomDownloadListener implements DownloadListener {

    private final WeakReference<Context> contextRef;

    public CustomDownloadListener(Context context) {
        this.contextRef = new WeakReference<>(context);
    }

    @Override
    public void onDownloadStart(String url,
                                 String userAgent,
                                 String contentDisposition,
                                 String mimeType,
                                 long contentLength) {

        Context context = contextRef.get();
        if (context == null) return;

        try {
            // استخراج نام فایل
            String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);

            // ساخت Request
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setMimeType(mimeType);
            request.addRequestHeader("User-Agent", userAgent);
            request.setTitle(fileName);
            request.setDescription(context.getString(R.string.download_started));
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            );

            // انتخاب مسیر بر اساس نوع فایل
            String folder = getFolderForMimeType(mimeType);

            request.setDestinationInExternalPublicDir(folder, fileName);

            // ارسال به DownloadManager
            DownloadManager dm = (DownloadManager) context
                .getSystemService(Context.DOWNLOAD_SERVICE);

            if (dm != null) {
                dm.enqueue(request);
                Toast.makeText(context,
                    R.string.download_started,
                    Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(context,
                R.string.download_failed,
                Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * انتخاب پوشه‌ی مقصد بر اساس نوع فایل
     */
    private String getFolderForMimeType(String mimeType) {
        if (mimeType == null) {
            return Environment.DIRECTORY_DOWNLOADS;
        }

        if (mimeType.startsWith("image/")) {
            return Environment.DIRECTORY_PICTURES;
        }
        if (mimeType.startsWith("audio/")) {
            return Environment.DIRECTORY_MUSIC;
        }
        if (mimeType.startsWith("video/")) {
            return Environment.DIRECTORY_MOVIES;
        }
        return Environment.DIRECTORY_DOWNLOADS;
    }
}
