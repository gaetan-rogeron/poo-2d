package gle.game2d.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import gle.game2d.collision.CollisionMap;
import gle.game2d.enemy.IEnemyBehavior;

/**
 * Ennemi de type Skeleton
 */
class SkeletonEnemy extends EnemyBase {
    private static final String SPRITE_PATH = "maps/skeleton.png";
    private static final int SPRITE_SIZE = 32;
    private static final int ANIMATION_FRAMES = 6;
    private static final float FRAME_DURATION = 0.12f;

    /**
     * Constructeur
     */
    public SkeletonEnemy(float x, float y, EnemyStats stats, IEnemyBehavior behavior,
                         CollisionMap collisionMap) {
        super(x, y, stats, behavior, collisionMap);
    }

    @Override
    protected void initializeAnimations() {
        spriteSheet = new Texture(Gdx.files.internal(SPRITE_PATH));
        TextureRegion[][] grid = TextureRegion.split(spriteSheet, SPRITE_SIZE, SPRITE_SIZE);

        // Animation idle (première ligne)
        TextureRegion[] idleFrames = new TextureRegion[ANIMATION_FRAMES];
        for (int i = 0; i < ANIMATION_FRAMES; i++) {
            idleFrames[i] = grid[0][i];
        }

        currentAnimation = new Animation<>(FRAME_DURATION, idleFrames);
        currentAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    @Override
    protected void onDeath() {
        super.onDeath();
        // On pourrait ajouter un son ou une animation de mort spécifique
    }
}
