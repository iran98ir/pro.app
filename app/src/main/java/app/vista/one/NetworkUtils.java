package app.vista.one;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;

import androidx.annotation.NonNull;

public final class NetworkUtils {

    private NetworkUtils() {
        // No instance
    }

    /**
     * آیا نت در دسترس هست؟ (چک لحظه‌ای)
     */
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager)
                context.getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) return false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = cm.getActiveNetwork();
                if (network == null) return false;

                NetworkCapabilities caps = cm.getNetworkCapabilities(network);
                return caps != null
                    && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            } else {
                NetworkInfo info = cm.getActiveNetworkInfo();
                return info != null && info.isConnected();
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * ثبت Callback برای تغییرات شبکه (زنده)
     */
    public static ConnectivityManager.NetworkCallback registerNetworkCallback(
            Context context,
            NetworkMonitorCallback callback) {

        ConnectivityManager cm = (ConnectivityManager)
            context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) return null;

        ConnectivityManager.NetworkCallback networkCallback =
            new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                if (callback != null) callback.onNetworkAvailable();
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                if (callback != null) callback.onNetworkLost();
            }
        };

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                cm.registerDefaultNetworkCallback(networkCallback);
            } else {
                NetworkRequest request = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();
                cm.registerNetworkCallback(request, networkCallback);
            }
        } catch (Exception ignored) {
            return null;
        }

        return networkCallback;
    }

    /**
     * لغو ثبت Callback
     */
    public static void unregisterNetworkCallback(
            Context context,
            ConnectivityManager.NetworkCallback callback) {

        if (callback == null) return;

        try {
            ConnectivityManager cm = (ConnectivityManager)
                context.getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            if (cm != null) {
                cm.unregisterNetworkCallback(callback);
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Callback برای تغییرات شبکه
     */
    public interface NetworkMonitorCallback {
        void onNetworkAvailable();
        void onNetworkLost();
    }
}
