package gle.game2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import gle.game2d.player.Player;

public class HealthBar {
    private ShapeRenderer shapeRenderer;
    private float x, y;
    private float width, height;
    private float borderThickness = 2f;

    public HealthBar() {
        shapeRenderer = new ShapeRenderer();
        this.width = 100f;
        this.height = 10f;
    }

    public void draw(OrthographicCamera camera, Player player) {
        // Position en haut à gauche de l'écran
        float screenLeft = camera.position.x - camera.viewportWidth / 2f;
        float screenTop = camera.position.y + camera.viewportHeight / 2f;

        x = screenLeft + 10f;
        y = screenTop - 20f;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Fond noir (bordure)
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(x - borderThickness, y - borderThickness,
            width + borderThickness * 2, height + borderThickness * 2);

        // Fond gris foncé
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1f);
        shapeRenderer.rect(x, y, width, height);

        // Barre de vie (rouge -> jaune -> vert selon le pourcentage)
        float healthPercent = (float) player.getCurrentHealth() / player.getMaxHealth();
        float healthWidth = width * healthPercent;

        // Couleur selon le pourcentage de vie
        if (healthPercent > 0.5f) {
            shapeRenderer.setColor(Color.GREEN);
        } else if (healthPercent > 0.25f) {
            shapeRenderer.setColor(Color.YELLOW);
        } else {
            shapeRenderer.setColor(Color.RED);
        }

        shapeRenderer.rect(x, y, healthWidth, height);

        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
