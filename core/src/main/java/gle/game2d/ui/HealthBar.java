package gle.game2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import gle.game2d.player.Player;

/** Barre de vie du joueur affichée en haut à gauche de l'écran. La couleur change selon le pourcentage de vie restant. */
public class HealthBar {
    //Constantes
    private static final float DEFAULT_WIDTH = 100f;
    private static final float DEFAULT_HEIGHT = 10f;
    private static final float BORDER_THICKNESS = 2f;
    private static final float MARGIN_X = 10f;
    private static final float MARGIN_Y = 20f;

    private static final float HIGH_HEALTH_THRESHOLD = 0.5f;
    private static final float LOW_HEALTH_THRESHOLD = 0.25f;

    //Attributs
    private final ShapeRenderer shapeRenderer;
    private final float width;
    private final float height;
    private float x, y;

    /** Constructeur de la barre de vie. Initialise avec les dimensions par défaut. */
    public HealthBar() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /** Constructeur de la barre de vie avec dimensions personnalisées. */
    public HealthBar(float width, float height) {
        this.shapeRenderer = new ShapeRenderer();
        this.width = width;
        this.height = height;
    }

    /** Dessine la barre de vie à l'écran. */
    public void draw(OrthographicCamera camera, Player player) {
        setProjectionMatrix(camera);
        calculatePosition(camera);
        renderHealthBar(player);
    }

    /** Calcule la position de la barre en haut à gauche de l'écran. */
    private void calculatePosition(OrthographicCamera camera) {
        float screenLeft = camera.position.x - camera.viewportWidth / 2f;
        float screenTop = camera.position.y + camera.viewportHeight / 2f;

        x = screenLeft + MARGIN_X;
        y = screenTop - MARGIN_Y;
    }

    /** Rend la barre de vie avec bordure et couleur appropriée. */
    private void renderHealthBar(Player player) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        drawBorder();
        drawBackground();
        drawHealthFill(player);

        shapeRenderer.end();
    }

    /** Définit la matrice de projection pour le rendu. Doit être appelé avant draw() si la caméra a changé. */
    public void setProjectionMatrix(OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
    }

    /** Dessine la bordure noire de la barre. */
    private void drawBorder() {
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(
            x - BORDER_THICKNESS,
            y - BORDER_THICKNESS,
            width + BORDER_THICKNESS * 2,
            height + BORDER_THICKNESS * 2
        );
    }

    /** Dessine le fond gris foncé de la barre. */
    private void drawBackground() {
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1f);
        shapeRenderer.rect(x, y, width, height);
    }

    /** Dessine le remplissage de la barre selon la vie du joueur. */
    private void drawHealthFill(Player player) {
        float healthPercent = calculateHealthPercent(player);
        float healthWidth = width * healthPercent;

        shapeRenderer.setColor(getHealthColor(healthPercent));
        shapeRenderer.rect(x, y, healthWidth, height);
    }

    /** Calcule le pourcentage de vie du joueur. */
    private float calculateHealthPercent(Player player) {
        return (float) player.getCurrentHealth() / player.getMaxHealth();
    }

    /** Obtient la couleur de la barre selon le pourcentage de vie. */
    private Color getHealthColor(float healthPercent) {
        if (healthPercent > HIGH_HEALTH_THRESHOLD) {
            return Color.GREEN;
        } else if (healthPercent > LOW_HEALTH_THRESHOLD) {
            return Color.YELLOW;
        } else {
            return Color.RED;
        }
    }

    /** Libère les ressources utilisées. */
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
