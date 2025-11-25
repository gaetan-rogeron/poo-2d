package gle.game2d.zone;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestionnaire des zones de caméra avec système de transitions.
 * Gère les transitions fluides entre les zones lors des déplacements du joueur.
 * Applique les patrons Observer et Strategy.
 *
 * @author Votre Nom
 * @version 2.0
 */
public class ZoneManager {
    // === Constantes ===
    private static final float DEFAULT_TRANSITION_SPEED = 3f;

    // === Configuration des zones ===
    private final float zoneWidth;
    private final float zoneHeight;
    private final int zonesX;
    private final int zonesY;

    // === État des zones ===
    private int currentZoneX = 0;
    private int currentZoneY = 0;
    private int targetZoneX = 0;
    private int targetZoneY = 0;

    // === Gestion des transitions ===
    private boolean transitioning = false;
    private float transitionProgress = 0f;
    private float transitionSpeed;
    private float cameraStartX, cameraStartY;
    private float cameraTargetX, cameraTargetY;

    // === Patrons de conception ===
    private ITransitionStrategy transitionStrategy;
    private final List<IZoneObserver> observers;

    /**
     * Constructeur du gestionnaire de zones.
     *
     * @param worldWidth largeur totale du monde
     * @param worldHeight hauteur totale du monde
     * @param zonesX nombre de zones en largeur
     * @param zonesY nombre de zones en hauteur
     * @throws IllegalArgumentException si les paramètres sont invalides
     */
    public ZoneManager(float worldWidth, float worldHeight, int zonesX, int zonesY) {
        validateConstructorParameters(worldWidth, worldHeight, zonesX, zonesY);

        this.zonesX = zonesX;
        this.zonesY = zonesY;
        this.zoneWidth = worldWidth / zonesX;
        this.zoneHeight = worldHeight / zonesY;
        this.transitionSpeed = DEFAULT_TRANSITION_SPEED;
        this.transitionStrategy = new SmoothStepTransition();
        this.observers = new ArrayList<>();
    }

    /**
     * Valide les paramètres du constructeur.
     */
    private void validateConstructorParameters(float worldWidth, float worldHeight, int zonesX, int zonesY) {
        if (worldWidth <= 0 || worldHeight <= 0) {
            throw new IllegalArgumentException("Les dimensions du monde doivent être positives");
        }
        if (zonesX <= 0 || zonesY <= 0) {
            throw new IllegalArgumentException("Le nombre de zones doit être positif");
        }
    }

    /**
     * Met à jour la position de la caméra en fonction de la position du joueur.
     *
     * @param delta temps écoulé depuis la dernière frame
     * @param playerX position X du joueur
     * @param playerY position Y du joueur
     * @param camera caméra à mettre à jour
     */
    public void update(float delta, float playerX, float playerY, OrthographicCamera camera) {
        int playerZoneX = calculatePlayerZoneX(playerX);
        int playerZoneY = calculatePlayerZoneY(playerY);

        if (shouldStartTransition(playerZoneX, playerZoneY)) {
            startTransition(playerZoneX, playerZoneY, camera);
        }

        if (transitioning) {
            updateTransition(delta, camera);
        }
    }

    /**
     * Calcule la zone X du joueur.
     */
    private int calculatePlayerZoneX(float playerX) {
        int zoneX = (int)(playerX / zoneWidth);
        return MathUtils.clamp(zoneX, 0, zonesX - 1);
    }

    /**
     * Calcule la zone Y du joueur.
     * Inverse Y car libGDX utilise Y vers le haut.
     */
    private int calculatePlayerZoneY(float playerY) {
        int zoneY = zonesY - 1 - (int)(playerY / zoneHeight);
        return MathUtils.clamp(zoneY, 0, zonesY - 1);
    }

    /**
     * Vérifie si une nouvelle transition doit démarrer.
     */
    private boolean shouldStartTransition(int playerZoneX, int playerZoneY) {
        return !transitioning && (playerZoneX != targetZoneX || playerZoneY != targetZoneY);
    }

    /**
     * Met à jour la progression de la transition.
     */
    private void updateTransition(float delta, OrthographicCamera camera) {
        transitionProgress += delta * transitionSpeed;

        if (transitionProgress >= 1.0f) {
            finishTransition(camera);
        } else {
            applyTransition(camera);
        }
    }

    /**
     * Applique la transition en cours avec la stratégie sélectionnée.
     */
    private void applyTransition(OrthographicCamera camera) {
        float t = transitionStrategy.interpolate(transitionProgress);
        float camX = MathUtils.lerp(cameraStartX, cameraTargetX, t);
        float camY = MathUtils.lerp(cameraStartY, cameraTargetY, t);
        camera.position.set(camX, camY, 0);

        notifyTransitionProgress(t);
    }

    /**
     * Termine la transition en cours.
     */
    private void finishTransition(OrthographicCamera camera) {
        transitioning = false;
        transitionProgress = 0f;
        currentZoneX = targetZoneX;
        currentZoneY = targetZoneY;

        camera.position.set(cameraTargetX, cameraTargetY, 0);
        notifyTransitionEnd();
    }

    /**
     * Démarre une nouvelle transition vers une zone.
     */
    private void startTransition(int newZoneX, int newZoneY, OrthographicCamera camera) {
        transitioning = true;
        transitionProgress = 0f;

        cameraStartX = camera.position.x;
        cameraStartY = camera.position.y;

        targetZoneX = newZoneX;
        targetZoneY = newZoneY;
        cameraTargetX = calculateZoneCenterX(newZoneX);
        cameraTargetY = calculateZoneCenterY(newZoneY);

        notifyTransitionStart();
        System.out.println("Transition vers zone [" + newZoneX + ", " + newZoneY +
            "] avec stratégie: " + transitionStrategy.getName());
    }

    /**
     * Calcule la position X du centre d'une zone.
     */
    private float calculateZoneCenterX(int zoneX) {
        return (zoneX + 0.5f) * zoneWidth;
    }

    /**
     * Calcule la position Y du centre d'une zone.
     * Inverse Y pour la position de la caméra.
     */
    private float calculateZoneCenterY(int zoneY) {
        return ((zonesY - 1 - zoneY) + 0.5f) * zoneHeight;
    }

    /**
     * Initialise la caméra dans la zone où se trouve le joueur au démarrage.
     *
     * @param playerX position X du joueur
     * @param playerY position Y du joueur
     * @param camera caméra à initialiser
     */
    public void initializeCamera(float playerX, float playerY, OrthographicCamera camera) {
        currentZoneX = calculatePlayerZoneX(playerX);
        currentZoneY = calculatePlayerZoneY(playerY);

        targetZoneX = currentZoneX;
        targetZoneY = currentZoneY;

        float camX = calculateZoneCenterX(currentZoneX);
        float camY = calculateZoneCenterY(currentZoneY);
        camera.position.set(camX, camY, 0);

        notifyZoneEnter();
        System.out.println("Caméra initialisée dans zone [" + currentZoneX + ", " + currentZoneY + "]");
        System.out.println("Position caméra: x=" + camX + ", y=" + camY);
    }

    // === Gestion des observers (Patron Observer) ===

    /**
     * Ajoute un observateur de changements de zones.
     *
     * @param observer l'observateur à ajouter
     * @throws IllegalArgumentException si l'observateur est null
     */
    public void addObserver(IZoneObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("L'observateur ne peut pas être null");
        }
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Retire un observateur de changements de zones.
     *
     * @param observer l'observateur à retirer
     */
    public void removeObserver(IZoneObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifie tous les observateurs du début d'une transition.
     */
    private void notifyTransitionStart() {
        for (IZoneObserver observer : observers) {
            observer.onTransitionStart(currentZoneX, currentZoneY, targetZoneX, targetZoneY);
        }
    }

    /**
     * Notifie tous les observateurs de la progression d'une transition.
     */
    private void notifyTransitionProgress(float progress) {
        for (IZoneObserver observer : observers) {
            observer.onTransitionProgress(currentZoneX, currentZoneY, targetZoneX, targetZoneY, progress);
        }
    }

    /**
     * Notifie tous les observateurs de la fin d'une transition.
     */
    private void notifyTransitionEnd() {
        for (IZoneObserver observer : observers) {
            observer.onTransitionEnd(currentZoneX, currentZoneY);
        }
    }

    /**
     * Notifie tous les observateurs de l'entrée dans une zone.
     */
    private void notifyZoneEnter() {
        for (IZoneObserver observer : observers) {
            observer.onZoneEnter(currentZoneX, currentZoneY);
        }
    }

    // === Gestion de la stratégie (Patron Strategy) ===

    /**
     * Définit la stratégie de transition à utiliser.
     *
     * @param strategy la nouvelle stratégie
     * @throws IllegalArgumentException si la stratégie est null
     */
    public void setTransitionStrategy(ITransitionStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("La stratégie ne peut pas être null");
        }
        this.transitionStrategy = strategy;
        System.out.println("Stratégie de transition changée: " + strategy.getName());
    }

    /**
     * Obtient la stratégie de transition actuelle.
     *
     * @return la stratégie actuelle
     */
    public ITransitionStrategy getTransitionStrategy() {
        return transitionStrategy;
    }

    /**
     * Définit la vitesse de transition.
     *
     * @param speed vitesse de transition (doit être positive)
     * @throws IllegalArgumentException si la vitesse est négative ou nulle
     */
    public void setTransitionSpeed(float speed) {
        if (speed <= 0) {
            throw new IllegalArgumentException("La vitesse doit être positive, reçu: " + speed);
        }
        this.transitionSpeed = speed;
    }

    /**
     * Obtient la vitesse de transition actuelle.
     *
     * @return vitesse de transition
     */
    public float getTransitionSpeed() {
        return transitionSpeed;
    }

    // === Accesseurs ===

    public boolean isTransitioning() {
        return transitioning;
    }

    public int getCurrentZoneX() {
        return currentZoneX;
    }

    public int getCurrentZoneY() {
        return currentZoneY;
    }

    public int getTargetZoneX() {
        return targetZoneX;
    }

    public int getTargetZoneY() {
        return targetZoneY;
    }

    public float getZoneWidth() {
        return zoneWidth;
    }

    public float getZoneHeight() {
        return zoneHeight;
    }

    public int getZonesX() {
        return zonesX;
    }

    public int getZonesY() {
        return zonesY;
    }

    public float getTransitionProgress() {
        return transitionProgress;
    }

    @Override
    public String toString() {
        return "ZoneManager{" +
            "zonesX=" + zonesX +
            ", zonesY=" + zonesY +
            ", currentZone=[" + currentZoneX + ", " + currentZoneY + "]" +
            ", transitioning=" + transitioning +
            ", strategy=" + transitionStrategy.getName() +
            '}';
    }
}
