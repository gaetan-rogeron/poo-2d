package gle.game2d.zone;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;

public class ZoneManager {
    private final float zoneWidth;
    private final float zoneHeight;
    private final int zonesX; // Nombre de zones en largeur
    private final int zonesY; // Nombre de zones en hauteur

    private int currentZoneX = 0;
    private int currentZoneY = 0;
    private int targetZoneX = 0;
    private int targetZoneY = 0;

    private boolean transitioning = false;
    private float transitionProgress = 0f;
    private static final float TRANSITION_SPEED = 3f; // Vitesse de transition

    private float cameraStartX, cameraStartY;
    private float cameraTargetX, cameraTargetY;

    public ZoneManager(float worldWidth, float worldHeight, int zonesX, int zonesY) {
        this.zonesX = zonesX;
        this.zonesY = zonesY;
        this.zoneWidth = worldWidth / zonesX;
        this.zoneHeight = worldHeight / zonesY;
    }

    /**
     * Met à jour la position de la caméra en fonction de la position du joueur
     */
    public void update(float delta, float playerX, float playerY, OrthographicCamera camera) {
        // Calculer dans quelle zone se trouve le joueur
        int playerZoneX = (int)(playerX / zoneWidth);
        // Inverser Y car libGDX utilise Y vers le haut, mais logiquement zone 0 = en haut
        int playerZoneY = zonesY - 1 - (int)(playerY / zoneHeight);

        // Limiter aux zones valides
        playerZoneX = MathUtils.clamp(playerZoneX, 0, zonesX - 1);
        playerZoneY = MathUtils.clamp(playerZoneY, 0, zonesY - 1);

        // Si le joueur change de zone, démarrer une transition
        if (!transitioning && (playerZoneX != targetZoneX || playerZoneY != targetZoneY)) {
            startTransition(playerZoneX, playerZoneY, camera);
        }

        // Gérer la transition
        if (transitioning) {
            transitionProgress += delta * TRANSITION_SPEED;

            if (transitionProgress >= 1f) {
                // Transition terminée
                transitioning = false;
                transitionProgress = 0f;
                currentZoneX = targetZoneX;
                currentZoneY = targetZoneY;

                // Position finale de la caméra
                camera.position.set(cameraTargetX, cameraTargetY, 0);
            } else {
                // Interpolation fluide (easing)
                float t = smoothStep(transitionProgress);
                float camX = MathUtils.lerp(cameraStartX, cameraTargetX, t);
                float camY = MathUtils.lerp(cameraStartY, cameraTargetY, t);
                camera.position.set(camX, camY, 0);
            }
        }
    }

    private void startTransition(int newZoneX, int newZoneY, OrthographicCamera camera) {
        transitioning = true;
        transitionProgress = 0f;

        // Sauvegarder la position actuelle de la caméra
        cameraStartX = camera.position.x;
        cameraStartY = camera.position.y;

        // Calculer la position cible de la caméra (centre de la nouvelle zone)
        targetZoneX = newZoneX;
        targetZoneY = newZoneY;
        cameraTargetX = (newZoneX + 0.5f) * zoneWidth;
        // Inverser Y pour la position de la caméra
        cameraTargetY = ((zonesY - 1 - newZoneY) + 0.5f) * zoneHeight;

        System.out.println("Transition vers zone [" + newZoneX + ", " + newZoneY + "]");
    }

    /**
     * Fonction d'easing pour une transition plus fluide (smoothstep)
     */
    private float smoothStep(float t) {
        return t * t * (3f - 2f * t);
    }

    /**
     * Initialise la caméra dans la zone où se trouve le joueur au démarrage
     */
    public void initializeCamera(float playerX, float playerY, OrthographicCamera camera) {
        currentZoneX = (int)(playerX / zoneWidth);
        // Inverser Y car libGDX utilise Y vers le haut, mais logiquement zone 0 = en haut
        currentZoneY = zonesY - 1 - (int)(playerY / zoneHeight);

        currentZoneX = MathUtils.clamp(currentZoneX, 0, zonesX - 1);
        currentZoneY = MathUtils.clamp(currentZoneY, 0, zonesY - 1);

        targetZoneX = currentZoneX;
        targetZoneY = currentZoneY;

        float camX = (currentZoneX + 0.5f) * zoneWidth;
        // Inverser Y pour la position de la caméra
        float camY = ((zonesY - 1 - currentZoneY) + 0.5f) * zoneHeight;
        camera.position.set(camX, camY, 0);

        System.out.println("Caméra initialisée dans zone [" + currentZoneX + ", " + currentZoneY + "]");
        System.out.println("Position caméra: x=" + camX + ", y=" + camY);
    }

    public boolean isTransitioning() {
        return transitioning;
    }

    public int getCurrentZoneX() { return currentZoneX; }
    public int getCurrentZoneY() { return currentZoneY; }
    public float getZoneWidth() { return zoneWidth; }
    public float getZoneHeight() { return zoneHeight; }
}
