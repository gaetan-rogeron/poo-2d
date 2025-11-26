package gle.game2d.zone;

/**
 * Interface Observer pour les changements de zones. Permet aux objets intéressés d'être notifiés des transitions de zones. Applique le patron Observer.
 */
public interface IZoneObserver {
    /** Appelé lorsqu'une transition de zone commence. */
    void onTransitionStart(int fromZoneX, int fromZoneY, int toZoneX, int toZoneY);

    /** Appelé pendant la progression d'une transition de zone. */
    void onTransitionProgress(int fromZoneX, int fromZoneY, int toZoneX, int toZoneY, float progress);

    /** Appelé lorsqu'une transition de zone se termine. */
    void onTransitionEnd(int toZoneX, int toZoneY);

    /** Appelé lorsque le joueur entre dans une nouvelle zone sans transition. */
    void onZoneEnter(int zoneX, int zoneY);
}
