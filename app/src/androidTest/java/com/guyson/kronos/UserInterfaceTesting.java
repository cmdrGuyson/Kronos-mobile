package com.guyson.kronos;

import android.app.Activity;
import android.content.ComponentName;
import android.view.Gravity;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitor;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import com.guyson.kronos.LoginActivity;
import com.guyson.kronos.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName;
import static androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


@RunWith(AndroidJUnit4.class)
public class UserInterfaceTesting {

    private static final String STUDENT_USERNAME = "CB6964";
    private static final String STUDENT_PASSWORD = "CB6964";

    private static final String ADMIN_USERNAME = "ADMIN001";
    private static final String ADMIN_PASSWORD = "password";

    private static final String AC_ADMIN_USERNAME = "ADMIN002";
    private static final String AC_ADMIN_PASSWORD = "password";

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testUILoginAsStudent_AndViewTimetable() {

        loginAsStudent();

        logout();
    }

    @Test
    public void testUILoginAsAdmin_AndViewAllStudents() {

        loginAsAdmin();

        logout();
    }

    @Test
    public void testUILoginAsAcademicAdmin_AndViewAllLecturers() {

        loginAsAcademicAdmin();

        logout();
    }

    @Test
    public void testUIViewAllRooms() {
        loginAsAdmin();

        //Open drawer and click button
        onView(withId(R.id.drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_manage_rooms));

        String mainActivityName = ManageRoomsActivity.class.getName();
        WaitActivityIsResumedIdlingResource resource = new WaitActivityIsResumedIdlingResource(mainActivityName);
        Espresso.registerIdlingResources(resource);
        intended(hasComponent(hasClassName(mainActivityName)));
        Espresso.unregisterIdlingResources(resource);

        logout();
    }

    @Test
    public void testUIViewAllClasses() {
        loginAsAdmin();

        //Open drawer and click button
        onView(withId(R.id.drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_manage_classes));

        String mainActivityName = ManageClassesActivity.class.getName();
        WaitActivityIsResumedIdlingResource resource = new WaitActivityIsResumedIdlingResource(mainActivityName);
        Espresso.registerIdlingResources(resource);
        intended(hasComponent(hasClassName(mainActivityName)));
        Espresso.unregisterIdlingResources(resource);

        logout();
    }

    @Test
    public void testUIViewAllModules() {
        loginAsAcademicAdmin();

        //Open drawer and click button
        onView(withId(R.id.drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_manage_modules));

        String mainActivityName = ManageModulesActivity.class.getName();
        WaitActivityIsResumedIdlingResource resource = new WaitActivityIsResumedIdlingResource(mainActivityName);
        Espresso.registerIdlingResources(resource);
        intended(hasComponent(hasClassName(mainActivityName)));
        Espresso.unregisterIdlingResources(resource);

        logout();
    }

    @Test
    public void testUIViewAllLectures() {
        loginAsAcademicAdmin();

        //Open drawer and click button
        onView(withId(R.id.drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_manage_lectures));

        String mainActivityName = ManageLecturesActivity.class.getName();
        WaitActivityIsResumedIdlingResource resource = new WaitActivityIsResumedIdlingResource(mainActivityName);
        Espresso.registerIdlingResources(resource);
        intended(hasComponent(hasClassName(mainActivityName)));
        Espresso.unregisterIdlingResources(resource);

        logout();
    }

    @Test
    public void testUIViewMyModules() {
        loginAsStudent();

        //Open drawer and click button
        onView(withId(R.id.drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_my_modules));

        String mainActivityName = MyModulesActivity.class.getName();
        WaitActivityIsResumedIdlingResource resource = new WaitActivityIsResumedIdlingResource(mainActivityName);
        Espresso.registerIdlingResources(resource);
        intended(hasComponent(hasClassName(mainActivityName)));
        Espresso.unregisterIdlingResources(resource);

        logout();
    }

    @Test
    public void testUISettingsPage() {
        loginAsStudent();

        //Open drawer and click button
        onView(withId(R.id.drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_settings));

        String mainActivityName = SettingsActivity.class.getName();
        WaitActivityIsResumedIdlingResource resource = new WaitActivityIsResumedIdlingResource(mainActivityName);
        Espresso.registerIdlingResources(resource);
        intended(hasComponent(hasClassName(mainActivityName)));
        Espresso.unregisterIdlingResources(resource);

        logout();
    }

    @Test
    public void testUIViewAllBookmarkedLectures() {
        loginAsStudent();

        //Open drawer and click button
        onView(withId(R.id.drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_bookmarks));

        String mainActivityName = BookmarkedActivity.class.getName();
        WaitActivityIsResumedIdlingResource resource = new WaitActivityIsResumedIdlingResource(mainActivityName);
        Espresso.registerIdlingResources(resource);
        intended(hasComponent(hasClassName(mainActivityName)));
        Espresso.unregisterIdlingResources(resource);

        logout();
    }

    private void loginAsStudent() {
        // Enter username and password.
        onView(withId(R.id.input_username)).perform(typeText(STUDENT_USERNAME), closeSoftKeyboard());
        onView(withId(R.id.input_password)).perform(typeText(STUDENT_PASSWORD), closeSoftKeyboard());

        // Click login button
        onView(withId(R.id.login_button)).perform(click());

        // Directed to student home activity
        String mainActivityName = MainActivity.class.getName();

        WaitActivityIsResumedIdlingResource resource = new WaitActivityIsResumedIdlingResource(mainActivityName);

        Espresso.registerIdlingResources(resource);
        intended(hasComponent(hasClassName(mainActivityName)));
        Espresso.unregisterIdlingResources(resource);
    }

    private void loginAsAcademicAdmin() {
        // Enter username and password.
        onView(withId(R.id.input_username)).perform(typeText(AC_ADMIN_USERNAME), closeSoftKeyboard());
        onView(withId(R.id.input_password)).perform(typeText(AC_ADMIN_PASSWORD), closeSoftKeyboard());

        // Click login button
        onView(withId(R.id.login_button)).perform(click());

        // Directed to student home activity
        String mainActivityName = ManageLecturersActivity.class.getName();

        WaitActivityIsResumedIdlingResource resource = new WaitActivityIsResumedIdlingResource(mainActivityName);

        Espresso.registerIdlingResources(resource);
        intended(hasComponent(hasClassName(mainActivityName)));
        Espresso.unregisterIdlingResources(resource);
    }

    private void loginAsAdmin() {
        // Enter username and password.
        onView(withId(R.id.input_username)).perform(typeText(ADMIN_USERNAME), closeSoftKeyboard());
        onView(withId(R.id.input_password)).perform(typeText(ADMIN_PASSWORD), closeSoftKeyboard());

        // Click login button
        onView(withId(R.id.login_button)).perform(click());

        // Directed to student home activity
        String mainActivityName = ManageStudentsActivity.class.getName();

        WaitActivityIsResumedIdlingResource resource = new WaitActivityIsResumedIdlingResource(mainActivityName);

        Espresso.registerIdlingResources(resource);
        intended(hasComponent(hasClassName(mainActivityName)));
        Espresso.unregisterIdlingResources(resource);
    }

    private void logout() {
        // Open Drawer
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer

        // Click on logout button
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_logout));
    }

    //Handle Idling resources where waiting for REST API response
    private static class WaitActivityIsResumedIdlingResource implements IdlingResource {
        private final ActivityLifecycleMonitor instance;
        private final String activityToWaitClassName;
        private volatile ResourceCallback resourceCallback;
        boolean resumed = false;
        public WaitActivityIsResumedIdlingResource(String activityToWaitClassName) {
            instance = ActivityLifecycleMonitorRegistry.getInstance();
            this.activityToWaitClassName = activityToWaitClassName;
        }

        @Override
        public String getName() {
            return this.getClass().getName();
        }

        @Override
        public boolean isIdleNow() {
            resumed = isActivityLaunched();
            if(resumed && resourceCallback != null) {
                resourceCallback.onTransitionToIdle();
            }

            return resumed;
        }

        private boolean isActivityLaunched() {
            Collection<Activity> activitiesInStage = instance.getActivitiesInStage(Stage.RESUMED);
            for (Activity activity : activitiesInStage) {
                if(activity.getClass().getName().equals(activityToWaitClassName)){
                    return true;
                }
            }
            return false;
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
            this.resourceCallback = resourceCallback;
        }
    }
}