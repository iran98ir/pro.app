package app.vista.one;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FilterNoticeActivity extends AppCompatActivity {

    private ImageView shieldIcon;
    private TextView noticeTitle;
    private TextView noticeDescription;
    private View brandRow;
    private Button btnStart;

    private boolean pagePreloaded = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_notice);

        setupSafeArea();
        bindViews();

        pagePreloaded = getIntent().getBooleanExtra("page_preloaded", false);

        // مخفی کردن اولیه
        hideAllViews();

        // انیمیشن ورود
        playEntranceAnimation();

        // دکمه شروع
        btnStart.setOnClickListener(v -> navigateNext());
    }

    // ============================================
    // اتصال ویوها
    // ============================================
    private void bindViews() {
        shieldIcon = findViewById(R.id.shieldIcon);
        noticeTitle = findViewById(R.id.noticeTitle);
        noticeDescription = findViewById(R.id.noticeDescription);
        brandRow = findViewById(R.id.brandRow);
        btnStart = findViewById(R.id.btnStart);
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
        shieldIcon.setAlpha(0f);
        shieldIcon.setScaleX(0.7f);
        shieldIcon.setScaleY(0.7f);

        noticeTitle.setAlpha(0f);
        noticeTitle.setTranslationY(20f);

        noticeDescription.setAlpha(0f);
        noticeDescription.setTranslationY(20f);

        brandRow.setAlpha(0f);

        btnStart.setAlpha(0f);
        btnStart.setTranslationY(20f);
    }

    // ============================================
    // انیمیشن ورود
    // ============================================
    private void playEntranceAnimation() {
        // آیکون سپر
        ObjectAnimator shieldAlpha = ObjectAnimator.ofFloat(shieldIcon, View.ALPHA, 0f, 1f);
        shieldAlpha.setDuration(600);

        ObjectAnimator shieldScaleX = ObjectAnimator.ofFloat(shieldIcon, View.SCALE_X, 0.7f, 1f);
        shieldScaleX.setDuration(700);

        ObjectAnimator shieldScaleY = ObjectAnimator.ofFloat(shieldIcon, View.SCALE_Y, 0.7f, 1f);
        shieldScaleY.setDuration(700);

        // تیتر
        ObjectAnimator titleAlpha = ObjectAnimator.ofFloat(noticeTitle, View.ALPHA, 0f, 1f);
        titleAlpha.setDuration(600);
        titleAlpha.setStartDelay(250);

        ObjectAnimator titleTrans = ObjectAnimator.ofFloat(noticeTitle, View.TRANSLATION_Y, 20f, 0f);
        titleTrans.setDuration(600);
        titleTrans.setStartDelay(250);

        // توضیح
        ObjectAnimator descAlpha = ObjectAnimator.ofFloat(noticeDescription, View.ALPHA, 0f, 1f);
        descAlpha.setDuration(600);
        descAlpha.setStartDelay(450);

        ObjectAnimator descTrans = ObjectAnimator.ofFloat(noticeDescription, View.TRANSLATION_Y, 20f, 0f);
        descTrans.setDuration(600);
        descTrans.setStartDelay(450);

        // برند
        ObjectAnimator brandAlpha = ObjectAnimator.ofFloat(brandRow, View.ALPHA, 0f, 1f);
        brandAlpha.setDuration(600);
        brandAlpha.setStartDelay(700);

        // دکمه
        ObjectAnimator btnAlpha = ObjectAnimator.ofFloat(btnStart, View.ALPHA, 0f, 1f);
        btnAlpha.setDuration(600);
        btnAlpha.setStartDelay(900);

        ObjectAnimator btnTrans = ObjectAnimator.ofFloat(btnStart, View.TRANSLATION_Y, 20f, 0f);
        btnTrans.setDuration(600);
        btnTrans.setStartDelay(900);

        // اجرا
        DecelerateInterpolator interp = new DecelerateInterpolator();

        shieldAlpha.setInterpolator(interp);
        shieldScaleX.setInterpolator(interp);
        shieldScaleY.setInterpolator(interp);
        titleAlpha.setInterpolator(interp);
        titleTrans.setInterpolator(interp);
        descAlpha.setInterpolator(interp);
        descTrans.setInterpolator(interp);
        brandAlpha.setInterpolator(interp);
        btnAlpha.setInterpolator(interp);
        btnTrans.setInterpolator(interp);

        shieldAlpha.start();
        shieldScaleX.start();
        shieldScaleY.start();
        titleAlpha.start();
        titleTrans.start();
        descAlpha.start();
        descTrans.start();
        brandAlpha.start();
        btnAlpha.start();
        btnTrans.start();
    }

    // ============================================
    // ناوبری به مرحله بعد
    // ============================================
    private void navigateNext() {
        Intent intent;

        // اگه Onboarding دیده شده → مستقیم Main
        if (PrefsManager.isOnboardingSeen(this)) {
            intent = new Intent(this, MainActivity.class);
        } else {
            // وگرنه → Onboarding
            intent = new Intent(this, OnboardingActivity.class);
        }

        intent.putExtra("page_preloaded", pagePreloaded);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    // ============================================
    // Back Press → نمی‌ذاریم بره عقب
    // ============================================
    @Override
    public void onBackPressed() {
        // هیچ کاری نکن
    }
          }
