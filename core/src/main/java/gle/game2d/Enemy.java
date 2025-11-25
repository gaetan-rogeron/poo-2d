package gle.game2d;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class Enemy {
    protected float x, y;
    protected int width, height;
    protected float speed;

    protected int maxHealth;
    protected int currentHealth;
    protected int damage;

    protected Texture sheet;
    protected TextureRegion[][] grid;
    protected Animation<TextureRegion> currentAnim;
    protected float stateTime = 0f;

    protected boolean alive = true;
    protected boolean invincible = false;
    protected float invincibilityTimer = 0f;
    protected static final float INVINCIBILITY_DURATION = 0.3f;

    protected CollisionMap collisionMap;

    public Enemy(float x, float y, CollisionMap collisionMap) {
        this.x = x;
        this.y = y;
        this.collisionMap = collisionMap;
    }

    public abstract void update(float dt, Player player);

    public void draw(SpriteBatch batch) {
        if (!alive) return;

        // Clignotement si invincible
        if (!invincible || (int)(invincibilityTimer * 10) % 2 == 0) {
            TextureRegion frame = currentAnim.getKeyFrame(stateTime);
            batch.draw(frame, x, y, width, height);
        }
    }

    public void drawHealthBar(com.badlogic.gdx.graphics.glutils.ShapeRenderer shapeRenderer) {
        if (!alive) return;

        float barWidth = width;
        float barHeight = 4f;
        float barX = x;
        float barY = y + height + 2f; // Au-dessus de l'ennemi

        // Fond noir (bordure)
        shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.BLACK);
        shapeRenderer.rect(barX - 1, barY - 1, barWidth + 2, barHeight + 2);

        // Fond rouge foncé
        shapeRenderer.setColor(0.3f, 0.1f, 0.1f, 1f);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);

        // Barre de vie (rouge -> jaune selon HP)
        float healthPercent = (float) currentHealth / maxHealth;
        float healthWidth = barWidth * healthPercent;

        if (healthPercent > 0.5f) {
            shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.GREEN);
        } else if (healthPercent > 0.25f) {
            shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.ORANGE);
        } else {
            shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.RED);
        }

        shapeRenderer.rect(barX, barY, healthWidth, barHeight);
    }

    public void takeDamage(int dmg) {
        if (!invincible && alive) {
            currentHealth -= dmg;
            if (currentHealth <= 0) {
                currentHealth = 0;
                alive = false;
                onDeath();
            } else {
                invincible = true;
                invincibilityTimer = INVINCIBILITY_DURATION;
            }
            System.out.println(getClass().getSimpleName() + " took " + dmg + " damage. Health: " + currentHealth);
        }
    }

    protected void onDeath() {
        System.out.println(getClass().getSimpleName() + " died!");
    }

    protected void updateInvincibility(float dt) {
        if (invincible) {
            invincibilityTimer -= dt;
            if (invincibilityTimer <= 0) {
                invincible = false;
            }
        }
    }

    protected boolean wouldCollide(float testX, float testY) {
        return collisionMap.collides(testX, testY, width, height);
    }

    // Déplacement simple vers le joueur
    protected void moveTowardsPlayer(Player player, float dt) {
        float px = player.getCenterX();
        float py = player.getCenterY();
        float cx = getCenterX();
        float cy = getCenterY();

        float dx = px - cx;
        float dy = py - cy;
        float dist = (float)Math.sqrt(dx * dx + dy * dy);

        if (dist > 0) {
            dx /= dist;
            dy /= dist;

            float moveX = dx * speed * dt;
            float moveY = dy * speed * dt;

            // Essayer de bouger en X
            if (!wouldCollide(x + moveX, y)) {
                x += moveX;
            }

            // Essayer de bouger en Y
            if (!wouldCollide(x, y + moveY)) {
                y += moveY;
            }
        }
    }

    public boolean intersects(float ox, float oy, float ow, float oh) {
        return x < ox + ow && x + width > ox && y < oy + oh && y + height > oy;
    }

    public float getCenterX() { return x + width / 2f; }
    public float getCenterY() { return y + height / 2f; }
    public float getX() { return x; }
    public float getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean isAlive() { return alive; }
    public int getDamage() { return damage; }

    public abstract void dispose();
}
