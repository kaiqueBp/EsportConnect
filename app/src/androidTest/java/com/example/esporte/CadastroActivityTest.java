package com.example.esporte;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.anything;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.esporte.view.CadastroActivity;

import org.junit.Rule;
import org.junit.Test;

public class CadastroActivityTest {
    @Rule
    public ActivityScenarioRule<CadastroActivity> activityRule =
            new ActivityScenarioRule<>(CadastroActivity.class);
    @Test
    public void testElementsDisplayed() {
        onView(withId(R.id.idImg)).check(matches(isDisplayed()));
        onView(withId(R.id.btAdicionarFt)).check(matches(isDisplayed()));
        onView(withId(R.id.idNome)).check(matches(isDisplayed()));
        onView(withId(R.id.idEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.idSenha)).check(matches(isDisplayed()));
        onView(withId(R.id.idCidade)).check(matches(isDisplayed()));
        onView(withId(R.id.spinner)).check(matches(isDisplayed()));
        onView(withId(R.id.radioGroup)).check(matches(isDisplayed()));
        onView(withId(R.id.idListaEsporte)).check(matches(isDisplayed()));
        onView(withId(R.id.idBotaoCadastrar)).check(matches(isDisplayed()));
    }

    @Test
    public void testInput() {
        onView(withId(R.id.idNome)).perform(typeText("Nome Teste"));
        onView(withId(R.id.idNome)).check(matches(withText("Nome Teste")));
        onView(withId(R.id.idEmail)).perform(typeText("email@teste.com"));
        onView(withId(R.id.idEmail)).check(matches(withText("email@teste.com")));
        onView(withId(R.id.idSenha)).perform(typeText("123456"));
        onView(withId(R.id.idSenha)).check(matches(withText("123456")));
    }


    @Test
    public void testRadioButtonSelection() {
        onView(withId(R.id.radioButtonM)).perform(click());
        onView(withId(R.id.radioButtonM)).check(matches(isDisplayed()));
    }

    @Test
    public void testSpinner() {
        onView(withId(R.id.spinner)).check(matches(isDisplayed())).perform(click());
    }

    @Test
    public void testSubmitButton() {
        onView(withId(R.id.idNome)).perform(typeText("Nome Teste"));
        onView(withId(R.id.idEmail)).perform(typeText("email@teste.com"));
        onView(withId(R.id.idSenha)).perform(typeText("123456"));
        onView(withId(R.id.idCidade)).perform(typeText("arame"));
        onView(withId(R.id.spinner)).perform(click());
        onData(anything()).atPosition(10).perform(click());
        onView(withId(R.id.radioButtonM)).perform(click());
        onView(withId(R.id.idBotaoCadastrar)).check(matches(isEnabled())).perform(click());
    }
}
