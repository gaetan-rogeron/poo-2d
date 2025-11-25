package gle.game2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Slime extends Enemy {

    public Slime(float x, float y, CollisionMap collisionMap) {
        super(x, y, collisionMap);

        this.width = 64;
        this.height = 64;
        this.speed = 30f;

        this.maxHealth = 30;
        this.currentHealth = 30;
        this.damage = 5;

        // Charger le sprite sheet des slimes
        sheet = new Texture(Gdx.files.internal("maps/Slime_Green.png"));
        grid = TextureRegion.split(sheet, 64, 64);

        // Animation idle (première ligne, 8 frames par exemple)
        TextureRegion[] idleFrames = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            idleFrames[i] = grid[0][i];
        }
        currentAnim = new Animation<>(0.15f, idleFrames);
        currentAnim.setPlayMode(Animation.PlayMode.LOOP);
    }

    @Override
    public void update(float dt, Player player) {
        if (!alive) return;

        updateInvincibility(dt);
        stateTime += dt;

        // Le slime suit le joueur lentement
        moveTowardsPlayer(player, dt);
    }

    @Override
    public void dispose() {
        sheet.dispose();
    }
}
