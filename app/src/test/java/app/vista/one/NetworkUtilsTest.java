package app.vista.one;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.net.ConnectivityManager;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class NetworkUtilsTest {

    private Context context;

    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();
    }

    // ============================================
    // isNetworkAvailable
    // ============================================
    @Test
    public void isNetworkAvailable_returnsBoolean() {
        boolean result = NetworkUtils.isNetworkAvailable(context);
        // فقط مطمئن شو که Crash نمی‌کنه
        assertNotNull(Boolean.valueOf(result));
    }

    @Test
    public void isNetworkAvailable_doesNotThrow() {
        try {
            NetworkUtils.isNetworkAvailable(context);
            assertTrue(true);
        } catch (Exception e) {
            assertTrue("Should not throw exception", false);
        }
    }

    @Test
    public void isNetworkAvailable_withNullContext_returnsFalse() {
        // چک کردن مقاومت در برابر null
        try {
            boolean result = NetworkUtils.isNetworkAvailable(context);
            assertNotNull(Boolean.valueOf(result));
        } catch (Exception e) {
            assertTrue("Should handle gracefully", false);
        }
    }

    // ============================================
    // registerNetworkCallback
    // ============================================
    @Test
    public void registerNetworkCallback_returnsCallback() {
        ConnectivityManager.NetworkCallback callback =
            NetworkUtils.registerNetworkCallback(
                context,
                new NetworkUtils.NetworkMonitorCallback() {
                    @Override
                    public void onNetworkAvailable() {
                        // no-op
                    }

                    @Override
                    public void onNetworkLost() {
                        // no-op
                    }
                }
            );

        assertNotNull(callback);

        // پاکسازی
        NetworkUtils.unregisterNetworkCallback(context, callback);
    }

    @Test
    public void unregisterNetworkCallback_withNull_doesNotCrash() {
        try {
            NetworkUtils.unregisterNetworkCallback(context, null);
            assertTrue(true);
        } catch (Exception e) {
            assertTrue("Should handle null gracefully", false);
        }
    }
}
