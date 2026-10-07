package app.vista.one;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        applyDarkMode();
    }

    private void applyDarkMode() {
        int mode = PrefsManager.getDarkMode(this);

        switch (mode) {
            case PrefsManager.DARK_MODE_ON:
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
                break;

            case PrefsManager.DARK_MODE_OFF:
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
                break;

            case PrefsManager.DARK_MODE_AUTO:
            default:
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
}
