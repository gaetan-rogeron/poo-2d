package gle.game2d.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.Map;

/**
 * Gère toutes les animations du joueur.
 * Single Responsibility Principle: cette classe s'occupe uniquement des animations.
 *
 * @author Votre Nom
 * @version 1.0
 */
public class PlayerAnimationComponent {
    private static final int SPRITE_WIDTH = 32;
    private static final int SPRITE_HEIGHT = 32;
    private static final int COLUMNS = 6;

    private static final float IDLE_FRAME_DURATION = 0.20f;
    private static final float WALK_FRAME_DURATION = 0.12f;
    private static final float ATTACK_FRAME_DURATION = 0.08f;

    private final Texture spriteSheet;
    private final TextureRegion[][] grid;

    private final Map<String, Animation<TextureRegion>> animations;

    /**
     * Constructeur - charge et initialise toutes les animations.
     */
    public PlayerAnimationComponent() {
        this.spriteSheet = new Texture(Gdx.files.internal("maps/player.png"));
        this.grid = TextureRegion.split(spriteSheet, SPRITE_WIDTH, SPRITE_HEIGHT);
        this.animations = new HashMap<>();

        initializeAnimations();
    }

    private void initializeAnimations() {
        // Idle animations (lignes 0-2)
        animations.put("idle_down", createAnimation(0, IDLE_FRAME_DURATION, true, false));
        animations.put("idle_right", createAnimation(1, IDLE_FRAME_DURATION, true, false));
        animations.put("idle_up", createAnimation(2, IDLE_FRAME_DURATION, true, false));
        animations.put("idle_left", createAnimation(1, IDLE_FRAME_DURATION, true, true));

        // Walk animations (lignes 3-5)
        animations.put("walk_down", createAnimation(3, WALK_FRAME_DURATION, false, false));
        animations.put("walk_right", createAnimation(4, WALK_FRAME_DURATION, false, false));
        animations.put("walk_up", createAnimation(5, WALK_FRAME_DURATION, false, false));
        animations.put("walk_left", createAnimation(4, WALK_FRAME_DURATION, false, true));

        // Attack animations (lignes 6-8)
        animations.put("attack_down", createAnimation(6, ATTACK_FRAME_DURATION, false, false));
        animations.put("attack_right", createAnimation(7, ATTACK_FRAME_DURATION, false, false));
        animations.put("attack_up", createAnimation(8, ATTACK_FRAME_DURATION, false, false));
        animations.put("attack_left", createAnimation(7, ATTACK_FRAME_DURATION, false, true));
    }

    private Animation<TextureRegion> createAnimation(int row, float frameDuration,
                                                     boolean pingPong, boolean flipHorizontal) {
        TextureRegion[] frames = new TextureRegion[COLUMNS];

        for (int col = 0; col < COLUMNS; col++) {
            if (flipHorizontal) {
                frames[col] = new TextureRegion(grid[row][col]);
                frames[col].flip(true, false);
            } else {
                frames[col] = grid[row][col];
            }
        }

        Animation<TextureRegion> animation = new Animation<>(frameDuration, frames);
        animation.setPlayMode(pingPong ? Animation.PlayMode.LOOP_PINGPONG : Animation.PlayMode.LOOP);

        return animation;
    }

    /**
     * Dessine l'animation appropriée.
     *
     * @param batch SpriteBatch
     * @param state état d'animation
     * @param stateTime temps d'animation
     * @param position position du joueur
     * @param width largeur
     * @param height hauteur
     */
    public void draw(SpriteBatch batch, PlayerAnimationState state, float stateTime,
                     Vector2 position, int width, int height) {
        String key = state.getAnimationKey();
        Animation<TextureRegion> anim = animations.get(key);

        if (anim != null) {
            TextureRegion frame = anim.getKeyFrame(stateTime);
            batch.draw(frame, position.x, position.y, width, height);
        }
    }

    /**
     * Libère les ressources.
     */
    public void dispose() {
        spriteSheet.dispose();
    }
}
