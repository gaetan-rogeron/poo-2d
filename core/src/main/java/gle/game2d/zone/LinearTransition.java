package gle.game2d.zone;

/**
 * Transition linéaire simple.
 * La progression est uniforme du début à la fin (t = t).
 * Stratégie concrète du patron Strategy.
 *
 * @author Votre Nom
 * @version 1.0
 */
public class LinearTransition implements ITransitionStrategy {

    /**
     * Calcule l'interpolation linéaire.
     * La fonction retourne simplement la valeur d'entrée.
     *
     * @param t temps de progression normalisé entre 0.0 et 1.0
     * @return la même valeur t
     * @throws IllegalArgumentException si t n'est pas entre 0 et 1
     */
    @Override
    public float interpolate(float t) {
        validateParameter(t);
        return t;
    }

    /**
     * Valide que le paramètre t est dans l'intervalle [0, 1].
     *
     * @param t valeur à valider
     * @throws IllegalArgumentException si t n'est pas entre 0 et 1
     */
    private void validateParameter(float t) {
        if (t < 0.0f || t > 1.0f) {
            throw new IllegalArgumentException(
                "Le paramètre t doit être entre 0 et 1, reçu: " + t
            );
        }
    }

    @Override
    public String getName() {
        return "Linear";
    }

    @Override
    public String toString() {
        return "LinearTransition{progression uniforme}";
    }
}