package gle.game2d.zone;

/**
 * Transition avec accélération au début (ease-in).
 * La transition démarre lentement puis accélère progressivement.
 * Utilise une interpolation quadratique: t²
 * Stratégie concrète du patron Strategy.
 *
 * @author Votre Nom
 * @version 1.0
 */
public class EaseInTransition implements ITransitionStrategy {

    /**
     * Calcule l'interpolation ease-in (quadratique).
     * La transition démarre lentement et accélère progressivement.
     *
     * @param t temps de progression normalisé entre 0.0 et 1.0
     * @return valeur interpolée entre 0.0 et 1.0
     * @throws IllegalArgumentException si t n'est pas entre 0 et 1
     */
    @Override
    public float interpolate(float t) {
        validateParameter(t);
        return t * t;
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
        return "EaseIn";
    }

    @Override
    public String toString() {
        return "EaseInTransition{accélération progressive}";
    }
}