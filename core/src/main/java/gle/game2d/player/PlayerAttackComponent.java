package gle.game2d.player;

/**
 * Gère le système d'attaque du joueur.
 * Single Responsibility Principle: cette classe s'occupe uniquement des attaques.
 *
 * @author Votre Nom
 * @version 1.0
 */
public class PlayerAttackComponent {
    private final int damage;
    private final float range;
    private final float duration;
    private final float cooldownTime;

    private boolean attacking;
    private boolean hitRegistered;
    private float attackTime;
    private float cooldown;

    /**
     * Constructeur.
     *
     * @param damage Dégâts infligés
     * @param range Portée de l'attaque
     * @param duration Durée de l'animation d'attaque
     * @param cooldownTime Temps de cooldown entre attaques
     */
    public PlayerAttackComponent(int damage, float range, float duration, float cooldownTime) {
        this.damage = damage;
        this.range = range;
        this.duration = duration;
        this.cooldownTime = cooldownTime;
        this.attacking = false;
        this.hitRegistered = false;
        this.attackTime = 0f;
        this.cooldown = 0f;
    }

    /**
     * Met à jour l'état d'attaque.
     *
     * @param deltaTime Temps écoulé
     */
    public void update(float deltaTime) {
        if (cooldown > 0) {
            cooldown -= deltaTime;
        }

        if (attacking) {
            attackTime += deltaTime;

            if (attackTime >= duration) {
                endAttack();
            }
        }
    }

    /**
     * Démarre une attaque.
     */
    public void startAttack() {
        if (!canAttack()) {
            return;
        }

        attacking = true;
        hitRegistered = false;
        attackTime = 0f;
        cooldown = cooldownTime;

        System.out.println("Player attacks!");
    }

    private void endAttack() {
        attacking = false;
        attackTime = 0f;
    }

    /**
     * Enregistre que l'attaque a touché (évite les multi-hits).
     */
    public void registerHit() {
        hitRegistered = true;
    }

    /**
     * Vérifie si le joueur peut attaquer.
     *
     * @return true si une attaque est possible
     */
    public boolean canAttack() {
        return !attacking && cooldown <= 0;
    }

    // === Getters ===

    public boolean isAttacking() {
        return attacking;
    }

    public boolean isHitRegistered() {
        return hitRegistered;
    }

    public float getAttackTime() {
        return attackTime;
    }

    public int getDamage() {
        return damage;
    }

    public float getRange() {
        return range;
    }

    public float getCooldown() {
        return cooldown;
    }

    public float getCooldownPercentage() {
        return Math.max(0f, cooldown / cooldownTime);
    }
}
