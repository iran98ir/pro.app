package app.vista.one;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SplashActivityTest {

    @Rule
    public ActivityScenarioRule<SplashActivity> rule =
        new ActivityScenarioRule<>(SplashActivity.class);

    // ============================================
    // نمایش اجزای Splash
    // ============================================
    @Test
    public void splashContent_isDisplayed() {
        onView(withId(R.id.splashContent))
            .check(matches(isDisplayed()));
    }

    @Test
    public void splashLogo_isDisplayed() {
        onView(withId(R.id.splashLogo))
            .check(matches(isDisplayed()));
    }

    @Test
    public void splashBrand_isDisplayed() {
        onView(withId(R.id.splashBrand))
            .check(matches(isDisplayed()));
    }

    @Test
    public void splashTagline_isDisplayed() {
        onView(withId(R.id.splashTagline))
            .check(matches(isDisplayed()));
    }

    @Test
    public void splashPrivacy_isDisplayed() {
        onView(withId(R.id.splashPrivacy))
            .check(matches(isDisplayed()));
    }
}
