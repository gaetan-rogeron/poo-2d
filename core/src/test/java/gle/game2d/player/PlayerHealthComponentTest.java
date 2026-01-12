package gle.game2d.player;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests unitaires pour PlayerHealthComponent.
 * Vérifie la gestion de la santé, l'invincibilité et les mécaniques de dégâts.
 */

public class PlayerHealthComponentTest {

    private PlayerHealthComponent healthComponent;
    private static final int INITIAL_HEALTH = 100;

    @Before
    public void setUp() {
        healthComponent = new PlayerHealthComponent(INITIAL_HEALTH);
    }

    // Vérifie l'état initial : santé courante = santé max, vivant, et non invincible.
    @Test
    public void testInitialState() {
        assertEquals("La santé initiale devrait être égale à la santé maximale",
            INITIAL_HEALTH, healthComponent.getCurrentHealth());
        assertEquals("La santé maximale devrait être correcte",
            INITIAL_HEALTH, healthComponent.getMaxHealth());
        assertTrue("Le joueur devrait être vivant", healthComponent.isAlive());
        assertFalse("Le joueur ne devrait pas être invincible", healthComponent.isInvincible());
    }

    // Vérifie que prendre des dégâts réduit la santé, active l'invincibilité, et que le joueur reste vivant.
    @Test
    public void testTakeDamage() {
        healthComponent.takeDamage(30);

        assertEquals("La santé devrait diminuer", 70, healthComponent.getCurrentHealth());
        assertTrue("Le joueur devrait être invincible après avoir pris des dégâts",
            healthComponent.isInvincible());
        assertTrue("Le joueur devrait être vivant", healthComponent.isAlive());
    }

    // Vérifie que les dégâts sont ignorés si le joueur est actuellement invincible.
    @Test
    public void testTakeDamageWhileInvincible() {
        healthComponent.takeDamage(30);
        int healthAfterFirstHit = healthComponent.getCurrentHealth();

        healthComponent.takeDamage(30); // Devrait être ignoré

        assertEquals("Les dégâts pendant l'invincibilité devraient être ignorés",
            healthAfterFirstHit, healthComponent.getCurrentHealth());
    }

    // Vérifie qu'un gros dégât tue le joueur et que la santé est clampée à 0 (pas négative).
    @Test
    public void testDeath() {
        healthComponent.takeDamage(150); // Plus que la santé totale

        assertEquals("La santé ne devrait pas être négative", 0, healthComponent.getCurrentHealth());
        assertFalse("Le joueur devrait être mort", healthComponent.isAlive());
    }

    // Vérifie que soigner après avoir pris des dégâts augmente correctement la santé.
    @Test
    public void testHeal() {
        healthComponent.takeDamage(50);
        healthComponent.heal(30);

        assertEquals("La santé devrait augmenter", 80, healthComponent.getCurrentHealth());
    }

    // Vérifie que la guérison ne peut pas faire dépasser la santé maximale (clamp au max).
    @Test
    public void testHealAboveMaxHealth() {
        healthComponent.takeDamage(20);
        healthComponent.heal(50); // Plus que nécessaire

        assertEquals("La santé ne devrait pas dépasser le maximum",
            INITIAL_HEALTH, healthComponent.getCurrentHealth());
    }

    // Vérifie que soigner avec une valeur négative déclenche une exception (validation d'argument).
    @Test(expected = IllegalArgumentException.class)
    public void testHealNegativeAmount() {
        healthComponent.heal(-10);
    }

    // Vérifie que l'invincibilité s'active après dégâts puis expire après un certain temps via update().
    @Test
    public void testInvincibilityExpires() {
        healthComponent.takeDamage(10);
        assertTrue("Le joueur devrait être invincible", healthComponent.isInvincible());

        // Simuler l'écoulement du temps
        for (int i = 0; i < 12; i++) {
            healthComponent.update(0.1f); // 1.2 secondes au total
        }

        assertFalse("L'invincibilité devrait avoir expiré", healthComponent.isInvincible());
    }

    // Vérifie le calcul du pourcentage de santé : 1.0 à pleine santé, 0.5 après 50 dégâts sur 100.
    @Test
    public void testHealthPercentage() {
        assertEquals("Le pourcentage de santé devrait être 1.0",
            1.0f, healthComponent.getHealthPercentage(), 0.001f);

        healthComponent.takeDamage(50);
        assertEquals("Le pourcentage de santé devrait être 0.5",
            0.5f, healthComponent.getHealthPercentage(), 0.001f);
    }

    // Vérifie que changer la santé maximale met à jour le max sans modifier la santé actuelle si elle reste valide.
    @Test
    public void testSetMaxHealth() {
        healthComponent.setMaxHealth(150);

        assertEquals("La santé maximale devrait être mise à jour",
            150, healthComponent.getMaxHealth());
        assertEquals("La santé actuelle devrait rester inchangée",
            INITIAL_HEALTH, healthComponent.getCurrentHealth());
    }

    // Vérifie que fixer une santé maximale invalide (<= 0) déclenche une exception.
    @Test(expected = IllegalArgumentException.class)
    public void testSetMaxHealthInvalid() {
        healthComponent.setMaxHealth(0);
    }

    // Vérifie que le clignotement est actif pendant l'invincibilité (shouldBlink renvoie true à un moment donné).
    @Test
    public void testBlinkingDuringInvincibility() {
        healthComponent.takeDamage(10);

        // Pendant l'invincibilité, le clignotement devrait alterner
        boolean firstCheck = healthComponent.shouldBlink();

        // Avancer légèrement dans le temps
        healthComponent.update(0.05f);
        boolean secondCheck = healthComponent.shouldBlink();

        // Les deux valeurs ne devraient pas nécessairement être identiques
        // car le clignotement alterne
        assertTrue("Le clignotement devrait être actif pendant l'invincibilité",
            firstCheck || secondCheck);
    }
}

