package gle.game2d.behavior;

import gle.game2d.enemy.EnemyBase;
import gle.game2d.player.Player;

/**
 * Interface Strategy pour les comportements d'ennemis.
 * Permet de définir différents comportements interchangeables.
 * Applique le patron Strategy.
 *
 * @author Votre Nom
 * @version 1.0
 */
public interface IEnemyBehavior {
    /**
     * Exécute le comportement de l'ennemi.
     *
     * @param enemy l'ennemi exécutant le comportement
     * @param deltaTime temps écoulé depuis la dernière frame
     * @param player référence au joueur
     */
    void execute(EnemyBase enemy, float deltaTime, Player player);
}
