package gle.game2d.zone;

/**
 * Transition avec accélération au début et décélération à la fin (ease-in-out). La transition démarre lentement, accélère au milieu, puis ralentit à la fin. Combine ease-in pour la première moitié et ease-out pour la seconde. Stratégie concrète du patron Strategy.
 */
public class EaseInOutTransition implements ITransitionStrategy {

    /** Calcule l'interpolation ease-in-out. Première moitié: accélération (ease-in). Seconde moitié: décélération (ease-out). */
    @Override
    public float interpolate(float t) {
        validateParameter(t);

        // Première moitié: ease-in (accélération)
        if (t < 0.5f) {
            return 2.0f * t * t;
        }

        // Seconde moitié: ease-out (décélération)
        float inverted = 1.0f - t;
        return 1.0f - (2.0f * inverted * inverted);
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
        return "EaseInOut";
    }

    @Override
    public String toString() {
        return "EaseInOutTransition{accélération puis décélération}";
    }
}