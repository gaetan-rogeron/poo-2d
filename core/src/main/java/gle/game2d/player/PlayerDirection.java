package gle.game2d.player;

/**
 * Représente une direction du joueur.
 * Alternative aux énumérations selon les recommandations du cours PCOO.
 * Utilise le patron Singleton pour chaque direction.
 *
 * @author Votre Nom
 * @version 1.0
 */
public final class PlayerDirection {
    private static final int DIR_DOWN = 0;
    private static final int DIR_RIGHT = 1;
    private static final int DIR_UP = 2;
    private static final int DIR_LEFT = 3;

    private static volatile PlayerDirection instanceDown = null;
    private static volatile PlayerDirection instanceRight = null;
    private static volatile PlayerDirection instanceUp = null;
    private static volatile PlayerDirection instanceLeft = null;

    private final int directionCode;

    /**
     * Constructeur privé (Patron Singleton).
     *
     * @param directionCode code de la direction
     */
    private PlayerDirection(int directionCode) {
        this.directionCode = directionCode;
    }

    /**
     * Factory Method pour créer/récupérer la direction DOWN.
     *
     * @return instance de direction DOWN
     */
    public static PlayerDirection createDown() {
        if (instanceDown == null) {
            synchronized (PlayerDirection.class) {
                if (instanceDown == null) {
                    instanceDown = new PlayerDirection(DIR_DOWN);
                }
            }
        }
        return instanceDown;
    }

    /**
     * Factory Method pour créer/récupérer la direction RIGHT.
     *
     * @return instance de direction RIGHT
     */
    public static PlayerDirection createRight() {
        if (instanceRight == null) {
            synchronized (PlayerDirection.class) {
                if (instanceRight == null) {
                    instanceRight = new PlayerDirection(DIR_RIGHT);
                }
            }
        }
        return instanceRight;
    }

    /**
     * Factory Method pour créer/récupérer la direction UP.
     *
     * @return instance de direction UP
     */
    public static PlayerDirection createUp() {
        if (instanceUp == null) {
            synchronized (PlayerDirection.class) {
                if (instanceUp == null) {
                    instanceUp = new PlayerDirection(DIR_UP);
                }
            }
        }
        return instanceUp;
    }

    /**
     * Factory Method pour créer/récupérer la direction LEFT.
     *
     * @return instance de direction LEFT
     */
    public static PlayerDirection createLeft() {
        if (instanceLeft == null) {
            synchronized (PlayerDirection.class) {
                if (instanceLeft == null) {
                    instanceLeft = new PlayerDirection(DIR_LEFT);
                }
            }
        }
        return instanceLeft;
    }

    public boolean isDown() {
        return this.directionCode == DIR_DOWN;
    }

    public boolean isRight() {
        return this.directionCode == DIR_RIGHT;
    }

    public boolean isUp() {
        return this.directionCode == DIR_UP;
    }

    public boolean isLeft() {
        return this.directionCode == DIR_LEFT;
    }

    public int getDirectionCode() {
        return this.directionCode;
    }

    /**
     * Retourne la clé pour l'animation.
     *
     * @return clé de direction pour animations
     */
    public String toAnimationKey() {
        switch (directionCode) {
            case DIR_DOWN: return "down";
            case DIR_RIGHT: return "right";
            case DIR_UP: return "up";
            case DIR_LEFT: return "left";
            default: return "down";
        }
    }

    @Override
    public String toString() {
        switch (directionCode) {
            case DIR_DOWN: return "DOWN";
            case DIR_RIGHT: return "RIGHT";
            case DIR_UP: return "UP";
            case DIR_LEFT: return "LEFT";
            default: return "UNKNOWN";
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PlayerDirection other = (PlayerDirection) obj;
        return this.directionCode == other.directionCode;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(directionCode);
    }
}
