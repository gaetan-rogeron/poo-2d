package gle.game2d.player;

/** Représente l'état d'animation du joueur. Combine le type d'animation (idle, walk, attack) avec la direction. */
public class PlayerAnimationState {
    private final String animationType; // "idle", "walk", "attack"
    private final PlayerDirection direction;

    /** Constructeur privé. */
    private PlayerAnimationState(String animationType, PlayerDirection direction) {
        this.animationType = animationType;
        this.direction = direction;
    }

    /** Factory Method pour créer un état Idle. */
    public static PlayerAnimationState createIdle(PlayerDirection direction) {
        return new PlayerAnimationState("idle", direction);
    }

    /** Factory Method pour créer un état Walk. */
    public static PlayerAnimationState createWalk(PlayerDirection direction) {
        return new PlayerAnimationState("walk", direction);
    }

    /** Factory Method pour créer un état Attack. */
    public static PlayerAnimationState createAttack(PlayerDirection direction) {
        return new PlayerAnimationState("attack", direction);
    }

    /** Génère la clé d'animation pour récupérer l'animation appropriée. */
    public String getAnimationKey() {
        return animationType + "_" + direction.toAnimationKey();
    }

    @Override
    public String toString() {
        return "AnimationState{" + animationType + "_" + direction.toString() + "}";
    }
}
