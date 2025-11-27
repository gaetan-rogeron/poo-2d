package gle.game2d.enemy;

/** Données de spawn pour un ennemi. Stocke simplement le type et la position. */
class EnemySpawnData {
    private final EnemyFactory.EnemyType type;
    private final float x;
    private final float y;

    /**
     * Constructeur.
     * @param type Type de l'ennemi
     * @param x Position X
     * @param y Position Y
     */
    public EnemySpawnData(EnemyFactory.EnemyType type, float x, float y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    // Getters
    public EnemyFactory.EnemyType getType() {
        return type;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}