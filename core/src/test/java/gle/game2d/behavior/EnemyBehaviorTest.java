package gle.game2d.behavior;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests unitaires pour les comportements d'ennemis.
 * Vérifie la logique des différentes stratégies de comportement.
 *
 * Chemin: core/src/test/java/gle/game2d/behavior/EnemyBehaviorTest.java
 */
public class EnemyBehaviorTest {

    // Vérifie que ChasePlayerBehavior a un toString() non nul et contenant le nom de la classe (utile pour debug/log).
    @Test
    public void testChasePlayerBehavior_ToString() {
        IEnemyBehavior behavior = new ChasePlayerBehavior();
        String str = behavior.toString();

        assertNotNull("ToString ne devrait pas être null", str);
        assertTrue("ToString devrait contenir 'ChasePlayerBehavior'",
            str.contains("ChasePlayerBehavior"));
    }

    // Vérifie que AttackOnProximityBehavior est correctement construit avec une portée et un cooldown donnés.
    @Test
    public void testAttackOnProximityBehavior_Creation() {
        float attackRange = 30f;
        float cooldownTime = 2.0f;

        AttackOnProximityBehavior behavior =
            new AttackOnProximityBehavior(attackRange, cooldownTime);

        assertEquals("La portée d'attaque devrait être correcte",
            attackRange, behavior.getAttackRange(), 0.001f);
        assertEquals("Le temps de cooldown devrait être correct",
            cooldownTime, behavior.getCooldownTime(), 0.001f);
    }

    // Vérifie que la portée d'attaque invalide (0) est refusée (validation via exception).
    @Test(expected = IllegalArgumentException.class)
    public void testAttackOnProximityBehavior_InvalidRange() {
        new AttackOnProximityBehavior(0f, 2.0f);
    }

    // Vérifie qu'une portée négative est refusée (validation via exception).
    @Test(expected = IllegalArgumentException.class)
    public void testAttackOnProximityBehavior_NegativeRange() {
        new AttackOnProximityBehavior(-10f, 2.0f);
    }

    // Vérifie qu'un cooldown négatif est refusé (validation via exception).
    @Test(expected = IllegalArgumentException.class)
    public void testAttackOnProximityBehavior_NegativeCooldown() {
        new AttackOnProximityBehavior(30f, -1.0f);
    }

    // Vérifie qu'un cooldown à 0 est autorisé et bien stocké.
    @Test
    public void testAttackOnProximityBehavior_ZeroCooldown() {
        AttackOnProximityBehavior behavior =
            new AttackOnProximityBehavior(30f, 0f);

        assertEquals("Un cooldown de 0 devrait être valide",
            0f, behavior.getCooldownTime(), 0.001f);
    }

    // Vérifie l'état initial : peut attaquer immédiatement et cooldown restant à 0.
    @Test
    public void testAttackOnProximityBehavior_InitialState() {
        AttackOnProximityBehavior behavior =
            new AttackOnProximityBehavior(30f, 2.0f);

        assertTrue("Devrait pouvoir attaquer initialement", behavior.canAttack());
        assertEquals("Le cooldown restant devrait être 0",
            0f, behavior.getRemainingCooldown(), 0.001f);
    }

    // Vérifie que toString() de AttackOnProximityBehavior est non nul et contient des infos utiles (nom, range, cooldown).
    @Test
    public void testAttackOnProximityBehavior_ToString() {
        AttackOnProximityBehavior behavior =
            new AttackOnProximityBehavior(30f, 2.0f);
        String str = behavior.toString();

        assertNotNull("ToString ne devrait pas être null", str);
        assertTrue("ToString devrait contenir 'AttackOnProximityBehavior'",
            str.contains("AttackOnProximityBehavior"));
        assertTrue("ToString devrait contenir 'range'", str.contains("range"));
        assertTrue("ToString devrait contenir 'cooldown'", str.contains("cooldown"));
    }
}
