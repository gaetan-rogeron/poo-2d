package gle.game2d.behavior;

import gle.game2d.enemy.EnemyBase;
import gle.game2d.player.Player;

/**
 * Comportement : Attaquer si proche, sinon suivre.
 * L'ennemi attaque le joueur s'il est à portée, sinon le suit.
 * Stratégie concrète du patron Strategy.
 *
 * @author Votre Nom
 * @version 1.0
 */
public class AttackOnProximityBehavior implements IEnemyBehavior {
    private final float attackRange;
    private final float cooldownTime;
    private float cooldown;

    /**
     * Constructeur du comportement d'attaque.
     *
     * @param attackRange portée d'attaque (en pixels)
     * @param cooldownTime temps de cooldown entre attaques (en secondes)
     * @throws IllegalArgumentException si les paramètres sont invalides
     */
    public AttackOnProximityBehavior(float attackRange, float cooldownTime) {
        if (attackRange <= 0) {
            throw new IllegalArgumentException("La portée d'attaque doit être positive");
        }
        if (cooldownTime < 0) {
            throw new IllegalArgumentException("Le cooldown ne peut pas être négatif");
        }

        this.attackRange = attackRange;
        this.cooldownTime = cooldownTime;
        this.cooldown = 0f;
    }

    /**
     * Exécute le comportement d'attaque conditionnelle.
     * Attaque si à portée et cooldown terminé, sinon suit le joueur.
     *
     * @param enemy l'ennemi exécutant le comportement
     * @param deltaTime temps écoulé
     * @param player référence au joueur
     */
    @Override
    public void execute(EnemyBase enemy, float deltaTime, Player player) {
        // Mise à jour du cooldown
        cooldown -= deltaTime;

        float distance = enemy.distanceTo(player.getCenterX(), player.getCenterY());

        // Si à portée et cooldown terminé, attaquer
        if (distance < attackRange && cooldown <= 0) {
            attackPlayer(enemy, player);
        } else {
            // Sinon, suivre le joueur
            new ChasePlayerBehavior().execute(enemy, deltaTime, player);
        }
    }

    /**
     * Attaque le joueur.
     *
     * @param enemy l'ennemi qui attaque
     * @param player le joueur cible
     */
    private void attackPlayer(EnemyBase enemy, Player player) {
        player.takeDamage(enemy.getDamage());
        cooldown = cooldownTime;
        System.out.println(enemy.getClass().getSimpleName() + " attaque! (Cooldown: "
            + cooldownTime + "s)");
    }

    /**
     * Obtient la portée d'attaque.
     *
     * @return portée d'attaque
     */
    public float getAttackRange() {
        return attackRange;
    }

    /**
     * Obtient le temps de cooldown.
     *
     * @return temps de cooldown
     */
    public float getCooldownTime() {
        return cooldownTime;
    }

    /**
     * Obtient le cooldown restant.
     *
     * @return cooldown restant
     */
    public float getRemainingCooldown() {
        return Math.max(0, cooldown);
    }

    /**
     * Vérifie si l'ennemi peut attaquer.
     *
     * @return true si le cooldown est terminé
     */
    public boolean canAttack() {
        return cooldown <= 0;
    }

    @Override
    public String toString() {
        return "AttackOnProximityBehavior{" +
            "range=" + attackRange +
            ", cooldown=" + cooldownTime +
            '}';
    }
}
