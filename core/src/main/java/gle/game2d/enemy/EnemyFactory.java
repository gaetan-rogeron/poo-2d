package gle.game2d.enemy;

import gle.game2d.behavior.IEnemyBehavior;
import gle.game2d.behavior.ChasePlayerBehavior;
import gle.game2d.behavior.AttackOnProximityBehavior;
import gle.game2d.collision.CollisionMap;

/** Factory pour créer des ennemis. Applique le patron Factory Method. */
class EnemyFactory {

    /** Enum orientée objet : énumération des types d'ennemis disponibles */
    public enum EnemyType {

        SLIME {
            @Override
            public IEnemy create(float x, float y, CollisionMap map) {
                return EnemyFactory.createSlime(x, y, map);
            }
        },
        SKELETON {
            @Override
            public IEnemy create(float x, float y, CollisionMap map) {
                return EnemyFactory.createSkeleton(x, y, map);
            }
        },
        KING_SLIME {
            @Override
            public IEnemy create(float x, float y, CollisionMap map) {
                return EnemyFactory.createKingSlime(x, y, map);
            }
        };

        public abstract IEnemy create(float x, float y, CollisionMap map);
    }

    /** Crée un ennemi selon son type. */
    public static IEnemy createEnemy(EnemyType type, float x, float y, CollisionMap map) {
        if (type == null) {
            throw new IllegalArgumentException("Type d'ennemi ne peut pas être null");
        }
        return type.create(x, y, map);
    }

    /** Crée un Slime. */
    private static IEnemy createSlime(float x, float y, CollisionMap collisionMap) {
        EnemyStats stats = new EnemyStats.Builder()
            .withDimensions(64, 64)
            .withSpeed(30f)
            .withHealth(30)
            .withDamage(5)
            .build();

        IEnemyBehavior behavior = new AttackOnProximityBehavior(30f, 2.0f);

        return new SlimeEnemy(x, y, stats, behavior, collisionMap);
    }

    /** Crée un Skeleton. */
    private static IEnemy createSkeleton(float x, float y, CollisionMap collisionMap) {
        EnemyStats stats = new EnemyStats.Builder()
            .withDimensions(32, 32)
            .withSpeed(50f)
            .withHealth(20)
            .withDamage(10)
            .build();

        IEnemyBehavior behavior = new AttackOnProximityBehavior(40f, 1.5f);

        return new SkeletonEnemy(x, y, stats, behavior, collisionMap);
    }

    /** Crée un King Slime (boss). */
    private static IEnemy createKingSlime(float x, float y, CollisionMap collisionMap) {
        EnemyStats stats = new EnemyStats.Builder()
            .withDimensions(64, 64)
            .withSpeed(25f)
            .withHealth(300)
            .withDamage(10)
            .build();

        IEnemyBehavior behavior = new AttackOnProximityBehavior(50f, 2.0f);

        return new KingSlimeEnemy(x, y, stats, behavior, collisionMap);
    }
}
