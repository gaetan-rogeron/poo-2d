package gle.game2d.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe PlayerHealth.
 * Démontre comment tester les composants individuellement.
 */
class PlayerHealthTest {
    private PlayerHealth health;
    private static final int MAX_HEALTH = 100;

    @BeforeEach
    void setUp() {
        health = new PlayerHealth(MAX_HEALTH);
    }

    @Test
    void testInitialHealth() {
        assertEquals(MAX_HEALTH, health.getMaxHealth());
        assertEquals(MAX_HEALTH, health.getCurrentHealth());
        assertTrue(health.isAlive());
        assertFalse(health.isInvincible());
    }

    @Test
    void testTakeDamage() {
        health.takeDamage(30);

        assertEquals(70, health.getCurrentHealth());
        assertTrue(health.isAlive());
        assertTrue(health.isInvincible());
    }

    @Test
    void testTakeFatalDamage() {
        health.takeDamage(150);

        assertEquals(0, health.getCurrentHealth());
        assertFalse(health.isAlive());
    }

    @Test
    void testInvincibilityPreventsMultipleDamage() {
        health.takeDamage(30);
        assertEquals(70, health.getCurrentHealth());

        // Pendant l'invincibilité, les dégâts ne passent pas
        health.takeDamage(20);
        assertEquals(70, health.getCurrentHealth());
    }

    @Test
    void testInvincibilityExpires() {
        health.takeDamage(30);
        assertTrue(health.isInvincible());

        // Simuler 1.1 secondes d'écoulement (plus que la durée d'invincibilité)
        for (int i = 0; i < 12; i++) {
            health.update(0.1f);
        }

        assertFalse(health.isInvincible());

        // Les dégâts peuvent maintenant passer
        health.takeDamage(20);
        assertEquals(50, health.getCurrentHealth());
    }

    @Test
    void testHeal() {
        health.takeDamage(50);
        assertEquals(50, health.getCurrentHealth());

        health.heal(30);
        assertEquals(80, health.getCurrentHealth());
    }

    @Test
    void testHealCannotExceedMax() {
        health.takeDamage(20);
        health.heal(50);

        assertEquals(MAX_HEALTH, health.getCurrentHealth());
    }

    @Test
    void testNegativeHealThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            health.heal(-10);
        });
    }

    @Test
    void testHealthPercentage() {
        assertEquals(1.0f, health.getHealthPercentage(), 0.01f);

        health.takeDamage(50);
        assertEquals(0.5f, health.getHealthPercentage(), 0.01f);

        health.takeDamage(25);
        assertEquals(0.25f, health.getHealthPercentage(), 0.01f);
    }

    @Test
    void testSetMaxHealth() {
        health.setMaxHealth(150);
        assertEquals(150, health.getMaxHealth());
        assertEquals(100, health.getCurrentHealth()); // La vie actuelle reste inchangée
    }

    @Test
    void testSetMaxHealthAdjustsCurrentHealth() {
        health.setMaxHealth(50);
        assertEquals(50, health.getCurrentHealth()); // Ajusté au nouveau max
    }

    @Test
    void testSetInvalidMaxHealthThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            health.setMaxHealth(0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            health.setMaxHealth(-10);
        });
    }

    @Test
    void testBlinkingDuringInvincibility() {
        health.takeDamage(10);

        // Pendant l'invincibilité, shouldBlink() doit alterner
        boolean hasBlinkOn = false;
        boolean hasBlinkOff = false;

        for (int i = 0; i < 20; i++) {
            if (health.shouldBlink()) hasBlinkOn = true;
            else hasBlinkOff = true;
            health.update(0.05f);
        }

        assertTrue(hasBlinkOn, "Le joueur devrait clignoter (invisible) pendant l'invincibilité");
        assertTrue(hasBlinkOff, "Le joueur devrait clignoter (visible) pendant l'invincibilité");
    }

    @Test
    void testNoBlinkingWhenNotInvincible() {
        assertFalse(health.shouldBlink());

        health.update(1.0f);
        assertFalse(health.shouldBlink());
    }

    @Test
    void testDeadPlayerCannotTakeDamage() {
        health.takeDamage(150);
        assertFalse(health.isAlive());

        health.takeDamage(50);
        assertEquals(0, health.getCurrentHealth()); // Reste à 0
    }
}
