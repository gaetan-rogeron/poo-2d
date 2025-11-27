package gle.game2d.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import gle.game2d.player.Player;

/** Potion de soin qui restaure 25 HP au joueur. */
public class Potion implements ICollectable {
    private static final int HEALTH_RESTORE = 25;
    private static final float SIZE = 16f; // Taille de la potion en pixels

    private final float x;
    private final float y;
    private final Rectangle bounds;
    private boolean collected;
    private Texture texture;

    /**
     * Constructeur de la potion.
     * @param x Position X de la potion
     * @param y Position Y de la potion
     */
    public Potion(float x, float y) {
        this.x = x;
        this.y = y;
        this.bounds = new Rectangle(x, y, SIZE, SIZE);
        this.collected = false;
        loadTexture();
    }

    /** Charge la texture de la potion. */
    private void loadTexture() {
        try {
            texture = new Texture(Gdx.files.internal("maps/Potion1.png"));
            System.out.println("Potion texture loaded successfully");
        } catch (Exception e) {
            System.out.println("Potion texture not found, creating red placeholder");
            // Créer une texture placeholder rouge
            Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
            pixmap.setColor(1, 0, 0, 1); // Rouge
            pixmap.fill();
            texture = new Texture(pixmap);
            pixmap.dispose();
        }
    }

    @Override
    public void update(float deltaTime) {
        // Les potions sont statiques, pas besoin de mise à jour
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!collected) {
            batch.draw(texture, x, y, SIZE, SIZE);
        }
    }

    @Override
    public void onCollect(Player player) {
        if (!collected) {
            // Soigner le joueur
            player.heal(HEALTH_RESTORE);
            collected = true;

            System.out.println("Potion collectée ! +" + HEALTH_RESTORE + " HP");
            System.out.println("Vie actuelle: " + player.getCurrentHealth() + "/" + player.getMaxHealth());
        }
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public boolean isCollected() {
        return collected;
    }

    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
