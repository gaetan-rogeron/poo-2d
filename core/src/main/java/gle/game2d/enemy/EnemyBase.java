package gle.game2d.enemy;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import gle.game2d.collision.CollisionMap;
import gle.game2d.EnemyStats;
import gle.game2d.IEnemyBehavior;
import gle.game2d.player.Player;

/**
 * Classe de base abstraite pour tous les ennemis.
 * Applique le patron Template Method pour la structure générale.
 * Applique le patron Strategy pour les comportements.
 *
 * @author Votre Nom
 * @version 1.0
 */
public abstract class EnemyBase implements IEnemy {
    // === Constantes ===
    private static final float INVINCIBILITY_DURATION = 0.3f;
    private static final float HEALTH_BAR_HEIGHT = 4f;
    private static final int BLINK_FREQUENCY = 10;

    // === Position et dimensions (privées avec accesseurs) ===
    private final Vector2 position;
    private final int width;
    private final int height;

    // === Statistiques ===
    private final float speed;
    private final int maxHealth;
    private int currentHealth;
    private final int damage;

    // === État ===
    private boolean alive;
    private boolean invincible;
    private float invincibilityTimer;

    // === Animation ===
    protected Texture spriteSheet;
    protected Animation<TextureRegion> currentAnimation;
    protected float stateTime;

    // === Comportement (Patron Strategy) ===
    private final IEnemyBehavior behavior;

    // === Référence collision ===
    protected final CollisionMap collisionMap;

    /**
     * Constructeur protégé (Template Method Pattern).
     *
     * @param x position X initiale
     * @param y position Y initiale
     * @param stats statistiques de l'ennemi
     * @param behavior comportement de l'ennemi
     * @param collisionMap carte de collision
     * @throws IllegalArgumentException si les paramètres sont invalides
     */
    protected EnemyBase(float x, float y, EnemyStats stats, IEnemyBehavior behavior,
                        CollisionMap collisionMap) {
        if (stats == null) {
            throw new IllegalArgumentException("Stats ne peuvent pas être null");
        }
        if (behavior == null) {
            throw new IllegalArgumentException("Behavior ne peut pas être null");
        }
        if (collisionMap == null) {
            throw new IllegalArgumentException("CollisionMap ne peut pas être null");
        }

        this.position = new Vector2(x, y);
        this.width = stats.getWidth();
        this.height = stats.getHeight();
        this.speed = stats.getSpeed();
        this.maxHealth = stats.getMaxHealth();
        this.currentHealth = this.maxHealth;
        this.damage = stats.getDamage();
        this.alive = true;
        this.invincible = false;
        this.invincibilityTimer = 0f;
        this.stateTime = 0f;
        this.behavior = behavior;
        this.collisionMap = collisionMap;

        initializeAnimations();
    }

    /**
     * Méthode abstraite pour initialiser les animations spécifiques.
     * Hook Method du Template Method Pattern.
     */
    protected abstract void initializeAnimations();

    /**
     * Met à jour l'ennemi.
     * Template Method : structure fixe avec étapes personnalisables.
     *
     * @param deltaTime temps écoulé
     * @param player référence au joueur
     */
    @Override
    public void update(float deltaTime, Player player) {
        if (!alive) return;

        // Étape 1 : Mise à jour de l'invincibilité
        updateInvincibility(deltaTime);

        // Étape 2 : Mise à jour de l'animation
        updateAnimation(deltaTime);

        // Étape 3 : Exécution du comportement (Strategy Pattern)
        executeBehavior(deltaTime, player);
    }

    /**
     * Étape 1 : Met à jour l'état d'invincibilité.
     *
     * @param deltaTime temps écoulé
     */
    private void updateInvincibility(float deltaTime) {
        if (invincible) {
            invincibilityTimer -= deltaTime;
            if (invincibilityTimer <= 0) {
                invincible = false;
            }
        }
    }

    /**
     * Étape 2 : Met à jour le temps d'animation.
     *
     * @param deltaTime temps écoulé
     */
    protected void updateAnimation(float deltaTime) {
        stateTime += deltaTime;
    }

    /**
     * Étape 3 : Exécute le comportement de l'ennemi.
     * Délégation au Strategy Pattern.
     *
     * @param deltaTime temps écoulé
     * @param player référence au joueur
     */
    protected void executeBehavior(float deltaTime, Player player) {
        behavior.execute(this, deltaTime, player);
    }

    /**
     * Dessine l'ennemi à l'écran.
     *
     * @param batch SpriteBatch pour le rendu
     */
    @Override
    public void draw(SpriteBatch batch) {
        if (!alive) return;

        // Clignotement si invincible
        if (invincible && shouldBlink()) {
            return;
        }

        if (currentAnimation != null) {
            TextureRegion frame = currentAnimation.getKeyFrame(stateTime);
            batch.draw(frame, position.x, position.y, width, height);
        }
    }

    /**
     * Détermine si l'ennemi doit être invisible (clignotement).
     *
     * @return true si invisible
     */
    private boolean shouldBlink() {
        int blinkCycle = (int) (invincibilityTimer * BLINK_FREQUENCY);
        return blinkCycle % 2 == 1;
    }

    /**
     * Dessine la barre de vie de l'ennemi.
     *
     * @param shapeRenderer renderer pour les formes
     */
    @Override
    public void drawHealthBar(ShapeRenderer shapeRenderer) {
        if (!alive) return;

        float barWidth = width;
        float barX = position.x;
        float barY = position.y + height + 2f;

        // Bordure noire
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(barX - 1, barY - 1, barWidth + 2, HEALTH_BAR_HEIGHT + 2);

        // Fond rouge foncé
        shapeRenderer.setColor(0.3f, 0.1f, 0.1f, 1f);
        shapeRenderer.rect(barX, barY, barWidth, HEALTH_BAR_HEIGHT);

        // Barre de vie colorée
        float healthPercent = (float) currentHealth / maxHealth;
        float healthWidth = barWidth * healthPercent;

        shapeRenderer.setColor(getHealthColor(healthPercent));
        shapeRenderer.rect(barX, barY, healthWidth, HEALTH_BAR_HEIGHT);
    }

    /**
     * Détermine la couleur de la barre de vie selon le pourcentage.
     *
     * @param healthPercent pourcentage de vie (0.0 à 1.0)
     * @return couleur appropriée
     */
    private Color getHealthColor(float healthPercent) {
        if (healthPercent > 0.5f) return Color.GREEN;
        if (healthPercent > 0.25f) return Color.ORANGE;
        return Color.RED;
    }

    /**
     * Inflige des dégâts à l'ennemi.
     *
     * @param dmg montant des dégâts
     */
    @Override
    public void takeDamage(int dmg) {
        if (invincible || !alive) return;

        currentHealth -= dmg;

        if (currentHealth <= 0) {
            currentHealth = 0;
            alive = false;
            onDeath();
        } else {
            invincible = true;
            invincibilityTimer = INVINCIBILITY_DURATION;
        }

        System.out.println(getClass().getSimpleName() + " a pris " + dmg
            + " dégâts. Vie: " + currentHealth + "/" + maxHealth);
    }

    /**
     * Hook method : appelé quand l'ennemi meurt.
     * Peut être overridé dans les sous-classes.
     */
    protected void onDeath() {
        System.out.println(getClass().getSimpleName() + " est mort!");
    }

    /**
     * Tente de déplacer l'ennemi avec détection de collision.
     *
     * @param dx déplacement en X
     * @param dy déplacement en Y
     */
    public void tryMove(float dx, float dy) {
        // Essayer mouvement en X
        if (dx != 0 && !collisionMap.collides(position.x + dx, position.y, width, height)) {
            position.x += dx;
        }

        // Essayer mouvement en Y
        if (dy != 0 && !collisionMap.collides(position.x, position.y + dy, width, height)) {
            position.y += dy;
        }
    }

    /**
     * Calcule la distance jusqu'à un point.
     *
     * @param x coordonnée X
     * @param y coordonnée Y
     * @return distance en pixels
     */
    public float distanceTo(float x, float y) {
        float dx = x - getCenterX();
        float dy = y - getCenterY();
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Obtient les limites rectangulaires de l'ennemi.
     *
     * @return rectangle de collision
     */
    @Override
    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, width, height);
    }

    // === Accesseurs publics ===

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public int getDamage() {
        return damage;
    }

    public float getCenterX() {
        return position.x + width / 2f;
    }

    public float getCenterY() {
        return position.y + height / 2f;
    }

    public Vector2 getPosition() {
        return position.cpy();
    }

    public float getSpeed() {
        return speed;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public boolean isInvincible() {
        return invincible;
    }

    /**
     * Libère les ressources.
     */
    @Override
    public void dispose() {
        if (spriteSheet != null) {
            spriteSheet.dispose();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
            "position=" + position +
            ", health=" + currentHealth + "/" + maxHealth +
            ", alive=" + alive +
            '}';
    }
}
