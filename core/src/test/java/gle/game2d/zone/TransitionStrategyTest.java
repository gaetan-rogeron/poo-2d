package gle.game2d.zone;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests unitaires pour les différentes stratégies de transition.
 * Vérifie les calculs d'interpolation et les validations.
 *
 * Chemin: core/src/test/java/gle/game2d/zone/TransitionStrategyTest.java
 */
public class TransitionStrategyTest {

    private static final float DELTA = 0.001f;

    // Vérifie que l'interpolation linéaire renvoie exactement t (0 -> 0, 0.5 -> 0.5, 1 -> 1).
    @Test
    public void testLinearTransition() {
        ITransitionStrategy linear = new LinearTransition();

        assertEquals("Linear: t=0", 0f, linear.interpolate(0f), DELTA);
        assertEquals("Linear: t=0.5", 0.5f, linear.interpolate(0.5f), DELTA);
        assertEquals("Linear: t=1", 1f, linear.interpolate(1f), DELTA);
    }

    // Vérifie que SmoothStep respecte les bornes (0 et 1) et qu'au milieu la valeur est proche de 0.5 (courbe lissée).
    @Test
    public void testSmoothStepTransition() {
        ITransitionStrategy smoothStep = new SmoothStepTransition();

        assertEquals("SmoothStep: t=0", 0f, smoothStep.interpolate(0f), DELTA);
        assertEquals("SmoothStep: t=1", 1f, smoothStep.interpolate(1f), DELTA);

        // Au milieu, devrait être proche de 0.5 mais pas exactement
        float midValue = smoothStep.interpolate(0.5f);
        assertTrue("SmoothStep: t=0.5 devrait être proche de 0.5",
            Math.abs(midValue - 0.5f) < 0.1f);
    }

    // Vérifie que EaseIn respecte les bornes et qu'à t=0.5 la valeur est < 0.5 (accélération progressive).
    @Test
    public void testEaseInTransition() {
        ITransitionStrategy easeIn = new EaseInTransition();

        assertEquals("EaseIn: t=0", 0f, easeIn.interpolate(0f), DELTA);
        assertEquals("EaseIn: t=1", 1f, easeIn.interpolate(1f), DELTA);

        // Devrait accélérer, donc t=0.5 devrait donner moins que 0.5
        float midValue = easeIn.interpolate(0.5f);
        assertTrue("EaseIn: t=0.5 devrait être moins que 0.5 (accélération)",
            midValue < 0.5f);
    }

    // Vérifie que EaseOut respecte les bornes et qu'à t=0.5 la valeur est > 0.5 (décélération).
    @Test
    public void testEaseOutTransition() {
        ITransitionStrategy easeOut = new EaseOutTransition();

        assertEquals("EaseOut: t=0", 0f, easeOut.interpolate(0f), DELTA);
        assertEquals("EaseOut: t=1", 1f, easeOut.interpolate(1f), DELTA);

        // Devrait décélérer, donc t=0.5 devrait donner plus que 0.5
        float midValue = easeOut.interpolate(0.5f);
        assertTrue("EaseOut: t=0.5 devrait être plus que 0.5 (décélération)",
            midValue > 0.5f);
    }

    // Vérifie que EaseInOut respecte les bornes et que t=0.5 donne exactement 0.5 (symétrie accélération/décélération).
    @Test
    public void testEaseInOutTransition() {
        ITransitionStrategy easeInOut = new EaseInOutTransition();

        assertEquals("EaseInOut: t=0", 0f, easeInOut.interpolate(0f), DELTA);
        assertEquals("EaseInOut: t=1", 1f, easeInOut.interpolate(1f), DELTA);

        // Au milieu, devrait être exactement 0.5
        assertEquals("EaseInOut: t=0.5 devrait être 0.5",
            0.5f, easeInOut.interpolate(0.5f), DELTA);
    }

    // Vérifie que LinearTransition rejette une valeur de t < 0 (validation d'entrée via exception).
    @Test(expected = IllegalArgumentException.class)
    public void testLinearTransition_InvalidLow() {
        new LinearTransition().interpolate(-0.1f);
    }

    // Vérifie que LinearTransition rejette une valeur de t > 1 (validation d'entrée via exception).
    @Test(expected = IllegalArgumentException.class)
    public void testLinearTransition_InvalidHigh() {
        new LinearTransition().interpolate(1.1f);
    }

    // Vérifie que SmoothStepTransition rejette une valeur de t < 0 (validation d'entrée via exception).
    @Test(expected = IllegalArgumentException.class)
    public void testSmoothStepTransition_InvalidLow() {
        new SmoothStepTransition().interpolate(-0.1f);
    }

    // Vérifie que EaseInTransition rejette une valeur de t > 1 (validation d'entrée via exception).
    @Test(expected = IllegalArgumentException.class)
    public void testEaseInTransition_InvalidHigh() {
        new EaseInTransition().interpolate(1.5f);
    }
}

