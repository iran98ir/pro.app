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
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> rule =
        new ActivityScenarioRule<>(MainActivity.class);

    // ============================================
    // نمایش اجزای Main
    // ============================================
    @Test
    public void rootLayout_isDisplayed() {
        onView(withId(R.id.rootLayout))
            .check(matches(isDisplayed()));
    }

    @Test
    public void progressBar_isPresent() {
        onView(withId(R.id.progressBar))
            .check(matches(isDisplayed()));
    }
}
