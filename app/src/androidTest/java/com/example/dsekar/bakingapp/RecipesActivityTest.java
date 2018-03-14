package com.example.dsekar.bakingapp;

import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class RecipesActivityTest {

    private IdlingResource mIdlingResource;
    @Rule
    public ActivityTestRule<MainActivity> mRecipeActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);


    @Before
    public void setUpActivity() {
        mRecipeActivityTestRule.getActivity();
    }

    @Test
    public void clickRecyclerViewItem_checkPositionOne() throws InterruptedException {
        Thread.sleep(5000);
        onView(withRecyclerView(R.id.receipeView).atPosition(1)).check(matches(isDisplayed()));

        onView(withRecyclerView(R.id.receipeView).atPositionOnView(1, R.id.recipe_title)).check(matches(withText("Brownies")));

    }

    @Test
    public void clickRecyclerViewItem_checkPositionTwo() throws InterruptedException {
        Thread.sleep(5000);
        onView(withId(R.id.receipeView))
                .perform(scrollToPosition(3));
        onView(withRecyclerView(R.id.receipeView).atPosition(3)).check(matches(isDisplayed()));

        onView(withRecyclerView(R.id.receipeView).atPositionOnView(3, R.id.recipe_title)).check(matches(withText("Cheesecake")));
    }

    public RecyclerViewMatcher withRecyclerView(int id) {
        return new RecyclerViewMatcher(id);
    }
}
