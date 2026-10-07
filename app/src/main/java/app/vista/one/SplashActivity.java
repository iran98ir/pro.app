package app.vista.one;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.atomic.AtomicBoolean;

public class SplashActivity extends AppCompatActivity {

    private static final long MIN_DISPLAY_TIME = 1500L;
    private static final long MAX_WAIT_TIME = 8000L;

    private LinearLayout splashContent;
    private ImageView splashLogo;
    private TextView splashBrand;
    private TextView splashTagline;
    private TextView splashTaglineSub;
    private View splashDivider;
    private TextView splashPrivacy;
    private ProgressBar splashProgress;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final AtomicBoolean hasNavigated = new AtomicBoolean(false);
    private long splashStartTime = 0L;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // نصب Splash Screen API
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        setupSafeArea();
        bindViews();

        splashStartTime = System.currentTimeMillis();

        hideAllViews();
        playEntranceAnimation();

        // شروع Preload پس‌زمینه
        startBackgroundPreload();

        // تایمر حداکثر انتظار
        handler.postDelayed(() -> navigate(false), MAX_WAIT_TIME);
    }

    // ============================================
    // اتصال ویوها
    // ============================================
    private void bindViews() {
        splashContent = findViewById(R.id.splashContent);
        splashLogo = findViewById(R.id.splashLogo);
        splashBrand = findViewById(R.id.splashBrand);
        splashTagline = findViewById(R.id.splashTagline);
        splashTaglineSub = findViewById(R.id.splashTaglineSub);
        splashDivider = findViewById(R.id.splashDivider);
        splashPrivacy = findViewById(R.id.splashPrivacy);
        splashProgress = findViewById(R.id.splashProgress);
    }

    // ============================================
    // Safe Area
    // ============================================
    private void setupSafeArea() {
        View root = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            int bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            v.setPadding(0, top, 0, bottom);
            return insets;
        });
    }

    // ============================================
    // مخفی کردن اولیه
    // ============================================
    private void hideAllViews() {
        splashLogo.setAlpha(0f);
        splashBrand.setAlpha(0f);
        splashTagline.setAlpha(0f);
        splashTaglineSub.setAlpha(0f);
        splashDivider.setAlpha(0f);
        splashPrivacy.setAlpha(0f);
        splashProgress.setAlpha(0f);
    }

    // ============================================
    // انیمیشن ورود
    // ============================================
    private void playEntranceAnimation() {
        // لوگو
        ObjectAnimator logoAlpha = ObjectAnimator.ofFloat(splashLogo, View.ALPHA, 0f, 1f);
        logoAlpha.setDuration(700);

        ObjectAnimator logoScaleX = ObjectAnimator.ofFloat(splashLogo, View.SCALE_X, 0.85f, 1f);
        logoScaleX.setDuration(700);

        ObjectAnimator logoScaleY = ObjectAnimator.ofFloat(splashLogo, View.SCALE_Y, 0.85f, 1f);
        logoScaleY.setDuration(700);

        // نام برند
        ObjectAnimator brandAlpha = ObjectAnimator.ofFloat(splashBrand, View.ALPHA, 0f, 1f);
        brandAlpha.setDuration(600);
        brandAlpha.setStartDelay(250);

        ObjectAnimator brandTrans = ObjectAnimator.ofFloat(splashBrand, View.TRANSLATION_Y, 20f, 0f);
        brandTrans.setDuration(600);
        brandTrans.setStartDelay(250);

        // خط جداکننده
        ObjectAnimator dividerScaleX = ObjectAnimator.ofFloat(splashDivider, View.SCALE_X, 0f, 1f);
        dividerScaleX.setDuration(500);
        dividerScaleX.setStartDelay(400);

        ObjectAnimator dividerAlpha = ObjectAnimator.ofFloat(splashDivider, View.ALPHA, 0f, 1f);
        dividerAlpha.setDuration(500);
        dividerAlpha.setStartDelay(400);

        // شعار اصلی
        ObjectAnimator taglineAlpha = ObjectAnimator.ofFloat(splashTagline, View.ALPHA, 0f, 1f);
        taglineAlpha.setDuration(600);
        taglineAlpha.setStartDelay(550);

        ObjectAnimator taglineTrans = ObjectAnimator.ofFloat(splashTagline, View.TRANSLATION_Y, 12f, 0f);
        taglineTrans.setDuration(600);
        taglineTrans.setStartDelay(550);

        // شعار فرعی
        ObjectAnimator taglineSubAlpha = ObjectAnimator.ofFloat(splashTaglineSub, View.ALPHA, 0f, 1f);
        taglineSubAlpha.setDuration(600);
        taglineSubAlpha.setStartDelay(700);

        // متن حریم خصوصی
        ObjectAnimator privacyAlpha = ObjectAnimator.ofFloat(splashPrivacy, View.ALPHA, 0f, 1f);
        privacyAlpha.setDuration(600);
        privacyAlpha.setStartDelay(900);

        // ProgressBar
        ObjectAnimator progressAlpha = ObjectAnimator.ofFloat(splashProgress, View.ALPHA, 0f, 1f);
        progressAlpha.setDuration(500);
        progressAlpha.setStartDelay(700);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(
            logoAlpha, logoScaleX, logoScaleY,
            brandAlpha, brandTrans,
            dividerScaleX, dividerAlpha,
            taglineAlpha, taglineTrans,
            taglineSubAlpha,
            privacyAlpha,
            progressAlpha
        );
        set.setInterpolator(new DecelerateInterpolator());
        set.start();
    }

    // ============================================
    // شروع Preload
    // ============================================
    private void startBackgroundPreload() {
        PreloadManager.preload(this, new PreloadManager.PreloadCallback() {
            @Override
            public void onPageLoaded() {
                runOnUiThread(() -> navigate(true));
            }

            @Override
            public void onPageFailed() {
                runOnUiThread(() -> navigate(false));
            }
        });
    }

    // ============================================
    // ناوبری
    // ============================================
    private void navigate(boolean pageLoaded) {
        if (!hasNavigated.compareAndSet(false, true)) {
            return;
        }

        handler.removeCallbacksAndMessages(null);

        long elapsed = System.currentTimeMillis() - splashStartTime;
        long remaining = MIN_DISPLAY_TIME - elapsed;

        if (remaining > 0) {
            handler.postDelayed(() -> performNavigation(pageLoaded), remaining);
        } else {
            performNavigation(pageLoaded);
        }
    }

    private void performNavigation(boolean pageLoaded) {
        splashContent.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction(() -> {

                Intent intent;
                if (PrefsManager.isOnboardingSeen(SplashActivity.this)) {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, OnboardingActivity.class);
                }

                intent.putExtra("page_preloaded", pageLoaded);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();

            })
            .start();
    }

    // ============================================
    // Back Press
    // ============================================
    @Override
    public void onBackPressed() {
        // توی Splash، هیچ کاری نکن
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
