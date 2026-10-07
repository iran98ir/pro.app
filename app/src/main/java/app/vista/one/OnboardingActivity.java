package app.vista.one;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private LinearLayout progressDots;
    private Button btnNext;
    private TextView btnSkip;
    private OnboardingAdapter adapter;

    private boolean pagePreloaded = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        setupSafeArea();

        pagePreloaded = getIntent().getBooleanExtra("page_preloaded", false);

        bindViews();
        setupViewPager();
        setupListeners();
    }

    private void bindViews() {
        viewPager = findViewById(R.id.viewPager);
        progressDots = findViewById(R.id.progressDots);
        btnNext = findViewById(R.id.btnNext);
        btnSkip = findViewById(R.id.btnSkip);
    }

    private void setupSafeArea() {
        View root = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            int bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            v.setPadding(0, top, 0, bottom);
            return insets;
        });
    }

    private void setupViewPager() {
        adapter = new OnboardingAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);

        viewPager.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

        buildDots(adapter.getItemCount());
        updateDots(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateDots(position);
                updateButtonText(position);
            }
        });
    }

    private void buildDots(int count) {
        progressDots.removeAllViews();

        int size = getResources().getDimensionPixelSize(R.dimen.onboarding_dot_size);
        int spacing = getResources().getDimensionPixelSize(R.dimen.onboarding_dot_spacing);

        for (int i = 0; i < count; i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(spacing, 0, spacing, 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(R.drawable.bg_dot_inactive);
            progressDots.addView(dot);
        }
    }

    private void updateDots(int activeIndex) {
        int size = getResources().getDimensionPixelSize(R.dimen.onboarding_dot_size);
        int activeSize = getResources().getDimensionPixelSize(R.dimen.onboarding_dot_size_active);
        int spacing = getResources().getDimensionPixelSize(R.dimen.onboarding_dot_spacing);

        for (int i = 0; i < progressDots.getChildCount(); i++) {
            View dot = progressDots.getChildAt(i);

            int newSize = (i == activeIndex) ? activeSize : size;

            LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(newSize, newSize);
            params.setMargins(spacing, 0, spacing, 0);
            dot.setLayoutParams(params);

            if (i == activeIndex) {
                dot.setBackgroundResource(R.drawable.bg_dot_active);
                animateDot(dot);
            } else {
                dot.setBackgroundResource(R.drawable.bg_dot_inactive);
            }
        }
    }

    private void animateDot(View dot) {
        dot.setScaleX(0.7f);
        dot.setScaleY(0.7f);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(dot, View.SCALE_X, 0.7f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(dot, View.SCALE_Y, 0.7f, 1f);

        scaleX.setDuration(250);
        scaleY.setDuration(250);
        scaleX.setInterpolator(new DecelerateInterpolator());
        scaleY.setInterpolator(new DecelerateInterpolator());

        scaleX.start();
        scaleY.start();
    }

    private void updateButtonText(int position) {
        if (position == adapter.getItemCount() - 1) {
            btnNext.setText(R.string.onboarding_start);
        } else {
            btnNext.setText(R.string.onboarding_next);
        }
    }

    private void setupListeners() {
        btnNext.setOnClickListener(v -> {
            int current = viewPager.getCurrentItem();
            if (current < adapter.getItemCount() - 1) {
                viewPager.setCurrentItem(current + 1, true);
            } else {
                finishOnboarding();
            }
        });

        btnSkip.setOnClickListener(v -> finishOnboarding());
    }

    private void finishOnboarding() {
        PrefsManager.setOnboardingSeen(this, true);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("page_preloaded", pagePreloaded);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        // کاربر نمی‌تونه برگرده — می‌ره به FilterNotice
        finishOnboarding();
    }
}
