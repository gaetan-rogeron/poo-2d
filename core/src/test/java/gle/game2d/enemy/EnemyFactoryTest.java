package gle.game2d.enemy;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import gle.game2d.collision.CollisionMap;


public class EnemyFactoryTest {

    private CollisionMap mockCollisionMap;

    @Before
    public void setUp() {
        // Créer une CollisionMap pour les tests
    }

    // On utilise les annotations du cours a partir de la

        @Test
        public void testCreateEnemy_Slime() {
            IEnemy enemy = EnemyFactory.createEnemy("Slime", 100f, 200f, mockCollisionMap);

            // Je precise, assert..., c'est junit, pas java de base

            assertNotNull("L'ennemi ne devrait pas être null", enemy);
            assertTrue("L'ennemi devrait être vivant", enemy.isAlive());
    }
}
