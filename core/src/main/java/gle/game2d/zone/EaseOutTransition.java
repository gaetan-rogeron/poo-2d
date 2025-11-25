package gle.game2d.zone;

/**
 * Transition avec décélération à la fin (ease-out).
 * La transition démarre rapidement puis ralentit progressivement.
 * Utilise une interpolation quadratique inversée: 1 - (1-t)²
 * Stratégie concrète du patron Strategy.
 *
 * @author Votre Nom
 * @version 1.0
 */
public class EaseOutTransition implements ITransitionStrategy {

    /**
     * Calcule l'interpolation ease-out (quadratique inversée).
     * La transition démarre rapidement et ralentit progressivement.
     *
     * @param t temps de progression normalisé entre 0.0 et 1.0
     * @return valeur interpolée entre 0.0 et 1.0
     * @throws IllegalArgumentException si t n'est pas entre 0 et 1
     */
    @Override
    public float interpolate(float t) {
        validateParameter(t);
        float inverted = 1.0f - t;
        return 1.0f - (inverted * inverted);
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
        return "EaseOut";
    }

    @Override
    public String toString() {
        return "EaseOutTransition{décélération progressive}";
    }
}