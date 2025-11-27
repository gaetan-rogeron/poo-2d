package gle.game2d.player;

/** Gère le système d'attaque du joueur. Single Responsibility Principle: cette classe s'occupe uniquement des attaques. */
public class PlayerAttackComponent {
    private int damage; // Non-final pour permettre les upgrades
    private final float range;
    private final float duration;
    private final float cooldownTime;

    private boolean attacking;
    private boolean hitRegistered;
    private float attackTime;
    private float cooldown;

    /** Constructeur. */
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

    /** Met à jour l'état d'attaque. */
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

    /** Démarre une attaque. */
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

    /** Enregistre que l'attaque a touché (évite les multi-hits). */
    public void registerHit() {
        hitRegistered = true;
    }

    /** Vérifie si le joueur peut attaquer. */
    public boolean canAttack() {
        return !attacking && cooldown <= 0;
    }

    //Getters

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

    public void setDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Damage cannot be negative");
        }
        this.damage = damage;
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
