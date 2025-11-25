package gle.game2d.zone;

/**
 * Interface Strategy pour les transitions de caméra entre zones.
 * Permet de définir différents types d'interpolation pour les transitions.
 * Applique le patron Strategy.
 *
 * @author Votre Nom
 * @version 1.0
 */
public interface ITransitionStrategy {
    /**
     * Calcule la valeur interpolée en fonction du temps de progression.
     * Cette fonction transforme une progression linéaire en une courbe d'interpolation.
     *
     * @param t temps de progression normalisé entre 0.0 et 1.0
     * @return valeur interpolée entre 0.0 et 1.0
     * @throws IllegalArgumentException si t n'est pas entre 0 et 1
     */
    float interpolate(float t);

    /**
     * Obtient le nom de la stratégie de transition.
     *
     * @return nom de la stratégie
     */
    String getName();
}