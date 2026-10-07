package app.vista.one;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PrefsManagerTest {

    private Context context;

    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();

        // ریست کردن به حالت پیش‌فرض قبل از هر تست
        PrefsManager.setOnboardingSeen(context, false);
        PrefsManager.setDarkMode(context, PrefsManager.DARK_MODE_AUTO);
        PrefsManager.setNotificationsEnabled(context, true);
        PrefsManager.setSoundEnabled(context, true);
        PrefsManager.setFcmToken(context, null);
    }

    // ============================================
    // Onboarding
    // ============================================
    @Test
    public void onboarding_default_isFalse() {
        assertFalse(PrefsManager.isOnboardingSeen(context));
    }

    @Test
    public void onboarding_setTrue_persists() {
        PrefsManager.setOnboardingSeen(context, true);
        assertTrue(PrefsManager.isOnboardingSeen(context));
    }

    // ============================================
    // Dark Mode
    // ============================================
    @Test
    public void darkMode_default_isAuto() {
        assertEquals(PrefsManager.DARK_MODE_AUTO,
            PrefsManager.getDarkMode(context));
    }

    @Test
    public void darkMode_setOn_persists() {
        PrefsManager.setDarkMode(context, PrefsManager.DARK_MODE_ON);
        assertEquals(PrefsManager.DARK_MODE_ON,
            PrefsManager.getDarkMode(context));
    }

    @Test
    public void darkMode_setOff_persists() {
        PrefsManager.setDarkMode(context, PrefsManager.DARK_MODE_OFF);
        assertEquals(PrefsManager.DARK_MODE_OFF,
            PrefsManager.getDarkMode(context));
    }

    // ============================================
    // Notifications
    // ============================================
    @Test
    public void notifications_default_isTrue() {
        assertTrue(PrefsManager.isNotificationsEnabled(context));
    }

    @Test
    public void notifications_setFalse_persists() {
        PrefsManager.setNotificationsEnabled(context, false);
        assertFalse(PrefsManager.isNotificationsEnabled(context));
    }

    @Test
    public void sound_default_isTrue() {
        assertTrue(PrefsManager.isSoundEnabled(context));
    }

    @Test
    public void sound_setFalse_persists() {
        PrefsManager.setSoundEnabled(context, false);
        assertFalse(PrefsManager.isSoundEnabled(context));
    }

    // ============================================
    // FCM Token
    // ============================================
    @Test
    public void fcmToken_default_isNull() {
        assertNull(PrefsManager.getFcmToken(context));
    }

    @Test
    public void fcmToken_saveAndRetrieve() {
        String token = "test_fcm_token_123";
        PrefsManager.setFcmToken(context, token);
        assertEquals(token, PrefsManager.getFcmToken(context));
    }

    // ============================================
    // First Launch
    // ============================================
    @Test
    public void firstLaunchTime_returnsValidTimestamp() {
        long time = PrefsManager.getFirstLaunchTime(context);
        assertTrue(time > 0);
    }

    @Test
    public void firstLaunchTime_returnsSameValueOnSecondCall() {
        long first = PrefsManager.getFirstLaunchTime(context);
        long second = PrefsManager.getFirstLaunchTime(context);
        assertEquals(first, second);
    }

    // ============================================
    // Version
    // ============================================
    @Test
    public void lastVersion_saveAndRetrieve() {
        String version = "1.0.0";
        PrefsManager.setLastVersion(context, version);
        assertEquals(version, PrefsManager.getLastVersion(context));
    }
}
