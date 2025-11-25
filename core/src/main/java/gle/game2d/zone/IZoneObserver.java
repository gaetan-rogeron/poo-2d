package gle.game2d.zone;

/**
 * Interface Observer pour les changements de zones.
 * Permet aux objets intéressés d'être notifiés des transitions de zones.
 * Applique le patron Observer.
 *
 * @author Votre Nom
 * @version 1.0
 */
public interface IZoneObserver {
    /**
     * Appelé lorsqu'une transition de zone commence.
     *
     * @param fromZoneX coordonnée X de la zone d'origine
     * @param fromZoneY coordonnée Y de la zone d'origine
     * @param toZoneX coordonnée X de la zone de destination
     * @param toZoneY coordonnée Y de la zone de destination
     */
    void onTransitionStart(int fromZoneX, int fromZoneY, int toZoneX, int toZoneY);

    /**
     * Appelé pendant la progression d'une transition de zone.
     *
     * @param fromZoneX coordonnée X de la zone d'origine
     * @param fromZoneY coordonnée Y de la zone d'origine
     * @param toZoneX coordonnée X de la zone de destination
     * @param toZoneY coordonnée Y de la zone de destination
     * @param progress progression de la transition (0.0 à 1.0)
     */
    void onTransitionProgress(int fromZoneX, int fromZoneY, int toZoneX, int toZoneY, float progress);

    /**
     * Appelé lorsqu'une transition de zone se termine.
     *
     * @param toZoneX coordonnée X de la zone finale
     * @param toZoneY coordonnée Y de la zone finale
     */
    void onTransitionEnd(int toZoneX, int toZoneY);

    /**
     * Appelé lorsque le joueur entre dans une nouvelle zone sans transition.
     *
     * @param zoneX coordonnée X de la nouvelle zone
     * @param zoneY coordonnée Y de la nouvelle zone
     */
    void onZoneEnter(int zoneX, int zoneY);
}
