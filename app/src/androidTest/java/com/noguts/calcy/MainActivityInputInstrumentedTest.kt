package com.noguts.calcy

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityInputInstrumentedTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun enteringNineThenZero_keepsMultiDigitInput() {
        onView(withId(R.id.btnClear)).perform(click())
        onView(withId(R.id.btn9)).perform(click())
        onView(withId(R.id.btn0)).perform(click())

        onView(withId(R.id.tvDisplay)).check(matches(withText("90")))
    }
}
