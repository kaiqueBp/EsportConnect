package com.example.esporte;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.anything;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.esporte.model.Endereco;
import com.example.esporte.model.Usuarios;
import com.example.esporte.view.EditarActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EditarActivityTest {
    @Rule
    public ActivityScenarioRule<EditarActivity> activityRule = new ActivityScenarioRule<EditarActivity>(
            new Intent(ApplicationProvider.getApplicationContext(), EditarActivity.class) {{
                Usuarios usuario = new Usuarios();
                usuario.setNome("Teste Nome");
                usuario.setEmail("teste@exemplo.com");
                Endereco endereco = new Endereco();
                endereco.setLocalidade("Teste cidade");
                endereco.setUf("Teste Estado");
                usuario.setEndereco(endereco);
                usuario.setSexo("M");
                putExtra("usuario", usuario);
            }}
    );
    @Before
    public void setUp() {

    }

    @Test
    public void testElementsDisplayed() {
        onView(withId(R.id.editNome)).check(matches(isDisplayed()));
        onView(withId(R.id.editCidade)).check(matches(isDisplayed()));
        onView(withId(R.id.editEstado)).check(matches(isDisplayed()));
        onView(withId(R.id.editM)).check(matches(isDisplayed()));
        onView(withId(R.id.editF)).check(matches(isDisplayed()));
        onView(withId(R.id.editO)).check(matches(isDisplayed()));
        onView(withId(R.id.btEditar)).check(matches(isDisplayed()));
        onView(withId(R.id.btEdiFoto)).check(matches(isDisplayed()));
    }

    @Test
    public void testEditUserName() {
        onView(withId(R.id.editNome)).check(matches(withText("Teste Nome")));
    }

    @Test
    public void testEditCity() {
        onView(withId(R.id.editCidade)).check(matches(withText("Teste cidade")));
    }

    @Test
    public void testSelectState() {
        onView(withId(R.id.editEstado)).perform(click());
    }

    @Test
    public void testGenderSelection() {
        onView(withId(R.id.editM)).perform(click());
        onView(withId(R.id.editM)).check(matches(isDisplayed()));
    }

    @Test
    public void testCompleteEditUser () {
        onView(withId(R.id.editNome)).check(matches(isDisplayed()));
        onView(withId(R.id.editCidade)).check(matches(isDisplayed()));
        onView(withId(R.id.editEstado)).check(matches(isDisplayed()));

        onView(withId(R.id.editEstado)).perform(click());
        onData(anything()).atPosition(12).perform(click());

        onView(withId(R.id.editM)).perform(click());

        onView(withId(R.id.btEditar)).perform(click());
    }
}
