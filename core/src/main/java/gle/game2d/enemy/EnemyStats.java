package gle.game2d.enemy;

/** Statistiques d'un ennemi. Classe immutable construite avec le patron Builder. */
class EnemyStats {
    private final int width;
    private final int height;
    private final float speed;
    private final int maxHealth;
    private final int damage;

    /** Constructeur privé (utilisé par le Builder). */
    private EnemyStats(Builder builder) {
        this.width = builder.width;
        this.height = builder.height;
        this.speed = builder.speed;
        this.maxHealth = builder.maxHealth;
        this.damage = builder.damage;
    }

    //Accesseurs (lecture seule)

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getSpeed() {
        return speed;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getDamage() {
        return damage;
    }

    /** Builder pour construire des EnemyStats. Applique le patron Builder. */
    public static class Builder {
        private int width = 32;
        private int height = 32;
        private float speed = 50f;
        private int maxHealth = 50;
        private int damage = 10;

        /** Définit les dimensions. */
        public Builder withDimensions(int width, int height) {
            if (width <= 0 || height <= 0) {
                throw new IllegalArgumentException("Dimensions doivent être positives");
            }
            this.width = width;
            this.height = height;
            return this;
        }

        /** Définit la vitesse. */
        public Builder withSpeed(float speed) {
            if (speed < 0) {
                throw new IllegalArgumentException("Vitesse ne peut pas être négative");
            }
            this.speed = speed;
            return this;
        }

        /** Définit la vie. */
        public Builder withHealth(int maxHealth) {
            if (maxHealth <= 0) {
                throw new IllegalArgumentException("Vie doit être positive");
            }
            this.maxHealth = maxHealth;
            return this;
        }

        /** Définit les dégâts. */
        public Builder withDamage(int damage) {
            if (damage < 0) {
                throw new IllegalArgumentException("Dégâts ne peuvent pas être négatifs");
            }
            this.damage = damage;
            return this;
        }

        /** Construit l'objet EnemyStats. */
        public EnemyStats build() {
            return new EnemyStats(this);
        }
    }

    @Override
    public String toString() {
        return "EnemyStats{" +
            "dimensions=" + width + "x" + height +
            ", speed=" + speed +
            ", maxHealth=" + maxHealth +
            ", damage=" + damage +
            '}';
    }
}
