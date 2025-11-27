package gle.game2d.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import gle.game2d.player.Player;

/** Épée qui augmente l'attaque du joueur à 50 PV. */
public class Sword implements ICollectable {
    private static final int ATTACK_BOOST = 50;
    private static final float SIZE = 16f; // Taille de l'épée en pixels

    private final float x;
    private final float y;
    private final Rectangle bounds;
    private boolean collected;
    private Texture texture;

    /**
     * Constructeur de l'épée.
     * @param x Position X de l'épée
     * @param y Position Y de l'épée
     */
    public Sword(float x, float y) {
        this.x = x;
        this.y = y;
        this.bounds = new Rectangle(x, y, SIZE, SIZE);
        this.collected = false;
        loadTexture();
    }

    /** Charge la texture de l'épée. */
    private void loadTexture() {
        try {
            texture = new Texture(Gdx.files.internal("maps/Tile_393.png"));
            System.out.println("Sword texture loaded successfully");
        } catch (Exception e) {
            System.out.println("Sword texture not found, creating gray placeholder");
            // Créer une texture placeholder grise
            Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
            pixmap.setColor(0.5f, 0.5f, 0.5f, 1); // Gris
            pixmap.fill();
            texture = new Texture(pixmap);
            pixmap.dispose();
        }
    }

    @Override
    public void update(float deltaTime) {
        // Les épées sont statiques, pas besoin de mise à jour
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
            // Augmenter l'attaque du joueur
            player.setAttackDamage(ATTACK_BOOST);
            collected = true;

            System.out.println("Épée collectée !");
            System.out.println("Attaque augmentée à " + ATTACK_BOOST + " PV");
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
