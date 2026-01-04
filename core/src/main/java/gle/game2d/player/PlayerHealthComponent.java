package gle.game2d.player;

/** Gère la santé du joueur et l'invincibilité temporaire. Single Responsibility Principle : cette classe s'occupe uniquement de la santé. */
public class PlayerHealthComponent {
    private static final float INVINCIBILITY_DURATION = 1.0f;
    private static final int BLINK_FREQUENCY = 10;

    private int maxHealth;
    private int currentHealth;
    private boolean invincible;
    private float invincibilityTimer;
    private IPlayerDeathListener deathListener; //ca stock qui doit etre notifié

    /** Constructeur. */
    public PlayerHealthComponent(int maxHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.invincible = false;
        this.invincibilityTimer = 0f;
    }

    public void setDeathListener(IPlayerDeathListener listener){ //le setter se fait appellé a la mort du perso
        this.deathListener = deathListener;
    }

    /** Met à jour l'état de santé (invincibilité). */
    public void update(float deltaTime) {
        if (invincible) {
            invincibilityTimer -= deltaTime;
            if (invincibilityTimer <= 0) {
                invincible = false;
            }
        }
    }

    /** Inflige des dégâts au joueur. */
    public void takeDamage(int damage) {
        if (invincible || !isAlive()) {
            return;
        }

        currentHealth -= damage;
        if (currentHealth < 0) {
            currentHealth = 0;
        }

        invincible = true;
        invincibilityTimer = INVINCIBILITY_DURATION;

        System.out.println("Player took " + damage + " damage. Health: " + currentHealth + "/" + maxHealth);

        if (!isAlive()) {
            onDeath();
        }
    }

    /** Soigne le joueur. */
    public void heal(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Heal amount cannot be negative");
        }

        currentHealth += amount;
        if (currentHealth > maxHealth) {
            currentHealth = maxHealth;
        }

        System.out.println("Player healed " + amount + ". Health: " + currentHealth + "/" + maxHealth);
    }

    /** Appelé quand le joueur meurt. */
    private void onDeath() {
        System.out.println("Player died!");

        if (deathListener != null){ //quand le personnage meurt, onDeath est appelé
            deathListener.onPlayerDeath();
        }
    }

    /** Détermine si le joueur doit clignoter (pendant l'invincibilité). */
    public boolean shouldBlink() {
        if (!invincible) {
            return false;
        }

        int blinkCycle = (int)(invincibilityTimer * BLINK_FREQUENCY);
        return blinkCycle % 2 == 1;
    }

    //Getters

    public boolean isAlive() {
        return currentHealth > 0;
    }

    public boolean isInvincible() {
        return invincible;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        if (maxHealth <= 0) {
            throw new IllegalArgumentException("Max health must be positive");
        }

        this.maxHealth = maxHealth;
        if (currentHealth > maxHealth) {
            currentHealth = maxHealth;
        }
    }

    /** Calcule le pourcentage de vie. */
    public float getHealthPercentage() {
        return (float) currentHealth / maxHealth;
    }
}
