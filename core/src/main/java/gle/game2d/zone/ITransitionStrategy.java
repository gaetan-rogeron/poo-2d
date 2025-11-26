package gle.game2d.zone;

/**
 * Interface Strategy pour les transitions de caméra entre zones. Permet de définir différents types d'interpolation pour les transitions. Applique le patron Strategy.
 */
public interface ITransitionStrategy {
    /** Calcule la valeur interpolée en fonction du temps de progression. Cette fonction transforme une progression linéaire en une courbe d'interpolation. */
    float interpolate(float t);

    /** Obtient le nom de la stratégie de transition. */
    String getName();
}