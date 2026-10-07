package app.vista.one;

import android.content.Context;
import android.content.SharedPreferences;

public final class PrefsManager {

    private static final String PREFS_NAME = "mu_prefs";

    // Onboarding
    private static final String KEY_ONBOARDING_SEEN = "onboarding_seen";

    // Dark Mode
    private static final String KEY_DARK_MODE = "dark_mode";
    public static final int DARK_MODE_AUTO = -1;
    public static final int DARK_MODE_OFF = 0;
    public static final int DARK_MODE_ON = 1;

    // Notifications
    private static final String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";
    private static final String KEY_SOUND_ENABLED = "sound_enabled";

    // FCM
    private static final String KEY_FCM_TOKEN = "fcm_token";

    // Version
    private static final String KEY_LAST_VERSION = "last_version";
    private static final String KEY_FIRST_LAUNCH = "first_launch_time";
    private static final String KEY_LAST_CACHE_CLEAR = "last_cache_clear";

    private PrefsManager() {
        // No instance
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext()
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // ============================================
    // Onboarding
    // ============================================
    public static boolean isOnboardingSeen(Context context) {
        return prefs(context).getBoolean(KEY_ONBOARDING_SEEN, false);
    }

    public static void setOnboardingSeen(Context context, boolean seen) {
        prefs(context).edit().putBoolean(KEY_ONBOARDING_SEEN, seen).apply();
    }

    // ============================================
    // Dark Mode
    // ============================================
    public static int getDarkMode(Context context) {
        return prefs(context).getInt(KEY_DARK_MODE, DARK_MODE_AUTO);
    }

    public static void setDarkMode(Context context, int mode) {
        prefs(context).edit().putInt(KEY_DARK_MODE, mode).apply();
    }

    // ============================================
    // Notifications
    // ============================================
    public static boolean isNotificationsEnabled(Context context) {
        return prefs(context).getBoolean(KEY_NOTIFICATIONS_ENABLED, true);
    }

    public static void setNotificationsEnabled(Context context, boolean enabled) {
        prefs(context).edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply();
    }

    public static boolean isSoundEnabled(Context context) {
        return prefs(context).getBoolean(KEY_SOUND_ENABLED, true);
    }

    public static void setSoundEnabled(Context context, boolean enabled) {
        prefs(context).edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply();
    }

    // ============================================
    // FCM Token
    // ============================================
    public static String getFcmToken(Context context) {
        return prefs(context).getString(KEY_FCM_TOKEN, null);
    }

    public static void setFcmToken(Context context, String token) {
        prefs(context).edit().putString(KEY_FCM_TOKEN, token).apply();
    }

    // ============================================
    // Version
    // ============================================
    public static String getLastVersion(Context context) {
        return prefs(context).getString(KEY_LAST_VERSION, null);
    }

    public static void setLastVersion(Context context, String version) {
        prefs(context).edit().putString(KEY_LAST_VERSION, version).apply();
    }

    public static long getFirstLaunchTime(Context context) {
        long time = prefs(context).getLong(KEY_FIRST_LAUNCH, 0L);
        if (time == 0L) {
            time = System.currentTimeMillis();
            prefs(context).edit().putLong(KEY_FIRST_LAUNCH, time).apply();
        }
        return time;
    }

    // ============================================
    // Cache
    // ============================================
    public static long getLastCacheClear(Context context) {
        return prefs(context).getLong(KEY_LAST_CACHE_CLEAR, 0L);
    }

    public static void setLastCacheClear(Context context, long timestamp) {
        prefs(context).edit().putLong(KEY_LAST_CACHE_CLEAR, timestamp).apply();
    }
            }
