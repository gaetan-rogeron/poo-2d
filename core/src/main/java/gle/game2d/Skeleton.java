package gle.game2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Skeleton extends Enemy {
    private float attackCooldown = 0f;
    private static final float ATTACK_RANGE = 40f;
    private static final float ATTACK_COOLDOWN_TIME = 1.5f;

    public Skeleton(float x, float y, CollisionMap collisionMap) {
        super(x, y, collisionMap);

        this.width = 32;
        this.height = 32;
        this.speed = 50f;

        this.maxHealth = 20;
        this.currentHealth = 20;
        this.damage = 10;

        // Charger le sprite sheet des squelettes
        sheet = new Texture(Gdx.files.internal("maps/skeleton.png"));
        grid = TextureRegion.split(sheet, 32, 32);

        // Animation idle (première ligne, 6 frames par exemple)
        TextureRegion[] idleFrames = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            idleFrames[i] = grid[0][i];
        }
        currentAnim = new Animation<>(0.12f, idleFrames);
        currentAnim.setPlayMode(Animation.PlayMode.LOOP);
    }

    @Override
    public void update(float dt, Player player) {
        if (!alive) return;

        updateInvincibility(dt);
        stateTime += dt;

        if (attackCooldown > 0) {
            attackCooldown -= dt;
        }

        float distToPlayer = distanceTo(player.getCenterX(), player.getCenterY());

        // Si proche du joueur, attaquer
        if (distToPlayer < ATTACK_RANGE && attackCooldown <= 0) {
            attackPlayer(player);
        } else {
            // Sinon, se déplacer vers le joueur
            moveTowardsPlayer(player, dt);
        }
    }

    private void attackPlayer(Player player) {
        attackCooldown = ATTACK_COOLDOWN_TIME;
        player.takeDamage(damage);
        System.out.println("Skeleton attacks!");
    }

    private float distanceTo(float px, float py) {
        float dx = px - getCenterX();
        float dy = py - getCenterY();
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public void dispose() {
        sheet.dispose();
    }
}

