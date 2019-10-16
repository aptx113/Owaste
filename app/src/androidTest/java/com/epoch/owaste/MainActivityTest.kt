package com.epoch.owaste


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.CAMERA"
        )

    @Test
    fun mainActivityTest() {
        val constraintLayout = onView(
            allOf(
                withId(R.id.cl_profile),
                childAtPosition(
                    allOf(
                        withId(R.id.ml_above_map),
                        childAtPosition(
                            withId(R.id.cl_main_page),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        constraintLayout.perform(click())

        val floatingActionButton = onView(
            allOf(
                withId(R.id.fab_card),
                childAtPosition(
                    allOf(
                        withId(R.id.cl_main_page),
                        childAtPosition(
                            withClassName(`is`("androidx.coordinatorlayout.widget.CoordinatorLayout")),
                            0
                        )
                    ),
                    6
                ),
                isDisplayed()
            )
        )
        floatingActionButton.perform(click())

        pressBack()

        val floatingActionButton2 = onView(
            allOf(
                withId(R.id.fab_qrcode),
                childAtPosition(
                    allOf(
                        withId(R.id.cl_main_page),
                        childAtPosition(
                            withClassName(`is`("androidx.coordinatorlayout.widget.CoordinatorLayout")),
                            0
                        )
                    ),
                    7
                ),
                isDisplayed()
            )
        )
        floatingActionButton2.perform(click())

        pressBack()

        val textView = onView(
            allOf(
                withId(R.id.txt_user_current_exp), withText("390"),
                childAtPosition(
                    allOf(
                        withId(R.id.ml_above_map),
                        childAtPosition(
                            withId(R.id.cl_main_page),
                            7
                        )
                    ),
                    6
                ),
                isDisplayed()
            )
        )
        textView.check(matches(withText("440")))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
