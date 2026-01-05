package gle.game2d.player;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests unitaires pour PlayerAttackComponent.
 */
public class PlayerAttackComponentTest {

    private PlayerAttackComponent attackComponent;
    private static final int INITIAL_DAMAGE = 10;
    private static final float RANGE = 5f;
    private static final float DURATION = 0.4f;
    private static final float COOLDOWN = 0.5f;

    @Before
    public void setUp() {
        attackComponent = new PlayerAttackComponent(INITIAL_DAMAGE, RANGE, DURATION, COOLDOWN);
    }

    // Vérifie l'état initial : pas en attaque, peut attaquer, dégâts et portée correctement initialisés.
    @Test
    public void testInitialState() {
        assertFalse("Ne devrait pas être en train d'attaquer", attackComponent.isAttacking());
        assertTrue("Devrait pouvoir attaquer", attackComponent.canAttack());
        assertEquals("Les dégâts devraient être corrects", INITIAL_DAMAGE, attackComponent.getDamage());
        assertEquals("La portée devrait être correcte", RANGE, attackComponent.getRange(), 0.001f);
    }

    // Vérifie que startAttack() démarre une attaque, empêche de réattaquer (cooldown), et qu'aucun coup n'est encore enregistré.
    @Test
    public void testStartAttack() {
        attackComponent.startAttack();

        assertTrue("Devrait être en train d'attaquer", attackComponent.isAttacking());
        assertFalse("Ne devrait pas pouvoir attaquer pendant le cooldown", attackComponent.canAttack());
        assertFalse("Le coup ne devrait pas encore être enregistré", attackComponent.isHitRegistered());
    }

    // Vérifie que l'attaque se termine après la durée (DURATION) via update().
    @Test
    public void testAttackDuration() {
        attackComponent.startAttack();

        // Avancer dans le temps jusqu'à la fin de l'attaque
        attackComponent.update(DURATION + 0.1f);

        assertFalse("L'attaque devrait être terminée", attackComponent.isAttacking());
    }

    // Vérifie que le cooldown empêche d'attaquer juste après l'attaque, puis expire après COOLDOWN.
    @Test
    public void testCooldown() {
        attackComponent.startAttack();
        attackComponent.update(DURATION + 0.1f); // Terminer l'attaque

        assertFalse("Devrait être en cooldown", attackComponent.canAttack());

        // Attendre que le cooldown expire
        attackComponent.update(COOLDOWN);

        assertTrue("Le cooldown devrait être terminé", attackComponent.canAttack());
    }

    // Vérifie que registerHit() marque correctement un coup comme enregistré pendant une attaque.
    @Test
    public void testRegisterHit() {
        attackComponent.startAttack();
        attackComponent.registerHit();

        assertTrue("Le coup devrait être enregistré", attackComponent.isHitRegistered());
    }

    // Vérifie qu'on ne peut pas attaquer de nouveau tant que l'attaque en cours n'est pas terminée.
    @Test
    public void testCannotAttackWhileAttacking() {
        attackComponent.startAttack();

        assertFalse("Ne devrait pas pouvoir attaquer pendant une attaque",
            attackComponent.canAttack());
    }

    // Vérifie que le temps d'attaque commence à 0 puis progresse avec update(dt).
    @Test
    public void testAttackTime() {
        attackComponent.startAttack();

        assertEquals("Le temps d'attaque devrait être 0 au début",
            0f, attackComponent.getAttackTime(), 0.001f);

        attackComponent.update(0.2f);

        assertEquals("Le temps d'attaque devrait progresser",
            0.2f, attackComponent.getAttackTime(), 0.001f);
    }

    // Vérifie que setDamage() met à jour la valeur des dégâts.
    @Test
    public void testSetDamage() {
        attackComponent.setDamage(50);

        assertEquals("Les dégâts devraient être mis à jour", 50, attackComponent.getDamage());
    }

    // Vérifie que setDamage() avec une valeur négative déclenche une exception (validation d'argument).
    @Test(expected = IllegalArgumentException.class)
    public void testSetNegativeDamage() {
        attackComponent.setDamage(-10);
    }

    // Vérifie que le pourcentage de cooldown est > 0 pendant le cooldown, puis retombe à 0 après expiration.
    @Test
    public void testCooldownPercentage() {
        attackComponent.startAttack();

        assertTrue("Le pourcentage de cooldown devrait être positif",
            attackComponent.getCooldownPercentage() > 0);

        attackComponent.update(COOLDOWN + DURATION);

        assertEquals("Le pourcentage de cooldown devrait être 0 après expiration",
            0f, attackComponent.getCooldownPercentage(), 0.001f);
    }

    // Vérifie qu'après une attaque + fin (durée + cooldown), une nouvelle attaque est possible et démarre correctement.
    @Test
    public void testMultipleAttacks() {
        // Première attaque
        attackComponent.startAttack();
        attackComponent.update(DURATION + COOLDOWN + 0.1f);

        assertTrue("Devrait pouvoir attaquer à nouveau", attackComponent.canAttack());

        // Deuxième attaque
        attackComponent.startAttack();
        assertTrue("Devrait être en train d'attaquer", attackComponent.isAttacking());
    }

    // Vérifie que getCooldown() renvoie une valeur cohérente pendant le cooldown (0 < cooldown <= COOLDOWN).
    @Test
    public void testGetCooldown() {
        attackComponent.startAttack();

        float cooldown = attackComponent.getCooldown();
        assertTrue("Le cooldown devrait être supérieur à 0", cooldown > 0);
        assertTrue("Le cooldown devrait être inférieur ou égal au max", cooldown <= COOLDOWN);
    }
}
