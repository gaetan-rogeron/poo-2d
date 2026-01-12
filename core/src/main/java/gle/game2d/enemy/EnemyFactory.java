package gle.game2d.enemy;

import gle.game2d.behavior.IEnemyBehavior;
import gle.game2d.behavior.ChasePlayerBehavior;
import gle.game2d.behavior.AttackOnProximityBehavior;
import gle.game2d.collision.CollisionMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory pour créer des ennemis. Applique le patron Factory Method avec Registry Pattern.
 * Utilise une approche orientée objet extensible sans enum ni switch.
 */
class EnemyFactory {

    /** Interface fonctionnelle pour créer un ennemi. Permet l'extensibilité. */
    @FunctionalInterface
    private interface EnemyCreator {
        IEnemy create(float x, float y, CollisionMap collisionMap);
    }

    /** Registre des créateurs d'ennemis. Map nom -> fonction de création. */
    private static final Map<String, EnemyCreator> ENEMY_CREATORS = new HashMap<>();

    // Initialisation statique du registre avec les types d'ennemis disponibles
    static {
        registerEnemyType("Slime", EnemyFactory::createSlime);
        registerEnemyType("Skeleton", EnemyFactory::createSkeleton);
        registerEnemyType("KingSlime", EnemyFactory::createKingSlime);
    }

    /**
     * Enregistre un nouveau type d'ennemi dans le registre.
     * Permet l'ajout de nouveaux types sans modifier le code existant (Open/Closed Principle).
     *
     * @param typeName Nom du type d'ennemi
     * @param creator Fonction de création de l'ennemi
     */
    public static void registerEnemyType(String typeName, EnemyCreator creator) {
        if (typeName == null || typeName.isEmpty()) {
            throw new IllegalArgumentException("Le nom du type ne peut pas être vide");
        }
        if (creator == null) {
            throw new IllegalArgumentException("Le créateur ne peut pas être null");
        }
        ENEMY_CREATORS.put(typeName, creator);
    }

    /**
     * Crée un ennemi selon son type. Factory Method Pattern avec Registry.
     *
     * @param enemyType Nom du type d'ennemi
     * @param x Position X
     * @param y Position Y
     * @param collisionMap Carte de collision
     * @return Une instance d'ennemi
     * @throws IllegalArgumentException Si le type est inconnu ou invalide
     */
    public static IEnemy createEnemy(String enemyType, float x, float y,
                                     CollisionMap collisionMap) {
        if (enemyType == null || enemyType.isEmpty()) {
            throw new IllegalArgumentException("Type d'ennemi ne peut pas être vide");
        }

        EnemyCreator creator = ENEMY_CREATORS.get(enemyType);
        if (creator == null) {
            throw new IllegalArgumentException("Type d'ennemi inconnu: " + enemyType
                + ". Types disponibles: " + ENEMY_CREATORS.keySet());
        }

        return creator.create(x, y, collisionMap);
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
