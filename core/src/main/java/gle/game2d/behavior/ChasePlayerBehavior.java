package gle.game2d.behavior;

import gle.game2d.enemy.EnemyBase;
import gle.game2d.player.Player;

/**
 * Comportement : Suivre le joueur. L'ennemi se déplace toujours vers le joueur. Stratégie concrète du patron Strategy.
 */
public class ChasePlayerBehavior implements IEnemyBehavior {

    /** Exécute le comportement de poursuite. L'ennemi se déplace en direction du joueur. */
    @Override
    public void execute(EnemyBase enemy, float deltaTime, Player player) {
        float px = player.getCenterX();
        float py = player.getCenterY();
        float cx = enemy.getCenterX();
        float cy = enemy.getCenterY();

        // Calculer direction vers le joueur
        float dx = px - cx;
        float dy = py - cy;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            // Normaliser le vecteur de direction
            dx /= distance;
            dy /= distance;

            // Calculer déplacement
            float moveX = dx * enemy.getSpeed() * deltaTime;
            float moveY = dy * enemy.getSpeed() * deltaTime;

            // Déplacer l'ennemi
            enemy.tryMove(moveX, moveY);
        }
    }

    @Override
    public String toString() {
        return "ChasePlayerBehavior{suivre le joueur}";
    }
}
