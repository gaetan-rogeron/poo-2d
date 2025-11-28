package gle.game2d.enemy;

import gle.game2d.behavior.IEnemyBehavior;
import gle.game2d.behavior.ChasePlayerBehavior;
import gle.game2d.behavior.AttackOnProximityBehavior;
import gle.game2d.collision.CollisionMap;

/** Factory pour créer des ennemis. Applique le patron Factory Method. */
class EnemyFactory {

    /** Énumération des types d'ennemis disponibles. */
    public enum EnemyType {
        SLIME,
        SKELETON,
        KING_SLIME
    }

    /** Crée un ennemi selon son type. Factory Method Pattern. */
    public static IEnemy createEnemy(EnemyType type, float x, float y,
                                     CollisionMap collisionMap) {
        if (type == null) {
            throw new IllegalArgumentException("Type d'ennemi ne peut pas être null");
        }

        switch (type) {
            case SLIME:
                return createSlime(x, y, collisionMap);
            case SKELETON:
                return createSkeleton(x, y, collisionMap);
            case KING_SLIME:
                return createKingSlime(x, y, collisionMap);
            default:
                throw new IllegalArgumentException("Type d'ennemi inconnu: " + type);
        }
    }

    /** Crée un ennemi selon son type (version String pour compatibilité). Factory Method Pattern. */
    public static IEnemy createEnemy(String enemyType, float x, float y,
                                     CollisionMap collisionMap) {
        if (enemyType == null || enemyType.isEmpty()) {
            throw new IllegalArgumentException("Type d'ennemi ne peut pas être vide");
        }

        if (enemyType.equals("Slime")) {
            return createSlime(x, y, collisionMap);
        } else if (enemyType.equals("Skeleton")) {
            return createSkeleton(x, y, collisionMap);
        } else if (enemyType.equals("KingSlime")) {
            return createKingSlime(x, y, collisionMap);
        }

        throw new IllegalArgumentException("Type d'ennemi inconnu: " + enemyType);
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

        // Comportement d'attaque avec une portée plus grande
        IEnemyBehavior behavior = new AttackOnProximityBehavior(50f, 2.0f);

        return new KingSlimeEnemy(x, y, stats, behavior, collisionMap);
    }
}
