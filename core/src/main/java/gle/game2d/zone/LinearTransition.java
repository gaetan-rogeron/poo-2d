package gle.game2d.zone;

/**
 * Transition linéaire simple. La progression est uniforme du début à la fin. Stratégie concrète du patron Strategy.
 */
public class LinearTransition implements ITransitionStrategy {

    /** Calcule l'interpolation linéaire. La fonction retourne simplement la valeur d'entrée. */
    @Override
    public float interpolate(float t) {
        validateParameter(t);
        return t;
    }

    /** Valide que le paramètre t est dans l'intervalle [0, 1]. */
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