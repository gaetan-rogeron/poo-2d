package gle.game2d;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import gle.game2d.player.PlayerHealthComponentTest;
import gle.game2d.player.PlayerAttackComponentTest;
import gle.game2d.player.PlayerDirectionTest;
import gle.game2d.enemy.EnemyStatsTest;
import gle.game2d.behavior.EnemyBehaviorTest;
import gle.game2d.zone.TransitionStrategyTest;

/**
 * Suite de tests complète pour le projet GLE-2dGame.
 * Fichier: core/src/test/java/gle/game2d/AllTestsSuite.java
 * Pour exécuter tous les tests :
 * - Ligne de commande : ./gradlew test
 * - IDE : Clic droit sur ce fichier -> Run 'AllTestsSuite'
 */
@RunWith(Suite.class)
@SuiteClasses({
    // Tests du joueur (32 tests)
    PlayerHealthComponentTest.class,
    PlayerAttackComponentTest.class,
    PlayerDirectionTest.class,

    // Tests des ennemis (12 tests)
    EnemyStatsTest.class,

    // Tests des comportements (8 tests)
    EnemyBehaviorTest.class,

    // Tests des zones (13 tests)
    TransitionStrategyTest.class
})
public class AllTestsSuite {
    // Cette classe reste vide
    // Elle sert uniquement comme point d'entrée pour exécuter tous les tests
}
