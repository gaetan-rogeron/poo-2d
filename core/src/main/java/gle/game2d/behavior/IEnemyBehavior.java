package gle.game2d.behavior;

import gle.game2d.enemy.EnemyBase;
import gle.game2d.player.Player;

/**
 * Interface Strategy pour les comportements d'ennemis. Permet de définir différents comportements interchangeables. Applique le patron Strategy.
 */
public interface IEnemyBehavior {
    /** Exécute le comportement de l'ennemi. */
    public void execute(EnemyBase enemy, float deltaTime, Player player);
}
