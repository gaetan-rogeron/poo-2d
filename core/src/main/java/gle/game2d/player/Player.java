package gle.game2d.player;

import gle.game2d.collision.CollisionMap;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/** Représente le personnage joueur dans le jeu. Utilise le patron Strategy pour les comportements et Composite pour les composants. */
public class Player {
    //Constantes
    private static final int PLAYER_WIDTH = 24;
    private static final int PLAYER_HEIGHT = 24;
    private static final float PLAYER_SPEED = 100f;

    //Composants (Composition)
    private final PlayerHealthComponent healthComponent;
    private final PlayerMovementComponent movementComponent;
    private final PlayerAttackComponent attackComponent;
    private final PlayerAnimationComponent animationComponent;

    //État
    private PlayerDirection currentDirection;
    private float stateTime;

    //Référence à la carte de collision
    private final CollisionMap collisionMap;

    /** Constructeur du joueur. */
    public Player(CollisionMap collisionMap, IPlayerDeathListener deathListener) {
        this.collisionMap = collisionMap;
        this.healthComponent = new PlayerHealthComponent(100);

        this.healthComponent.setDeathListener(deathListener); //Enregistrement du listener pour être averti de la mort du joueur

        this.movementComponent = new PlayerMovementComponent(
            PLAYER_SPEED, PLAYER_WIDTH, PLAYER_HEIGHT
        );
        this.attackComponent = new PlayerAttackComponent(10, 5f, 0.4f, 0.5f);
        this.animationComponent = new PlayerAnimationComponent();
        this.currentDirection = PlayerDirection.createDown();
        this.stateTime = 0f;
    }

    public Player(CollisionMap collisionMap){ //Permet de reagir à la mort du joueur sans coupler cette classe au reste du jeu
        this(collisionMap, null); // lisez : https://stackoverflow.com/questions/226977/what-is-loose-coupling-please-provide-examples pour comprendre un peu mieux
    }

    /** Met à jour l'état du joueur. Applique le patron Template Method. */
    public void update(float deltaTime) {
        updateStateTime(deltaTime);
        updateComponents(deltaTime);
        handleInput(deltaTime);
    }

    private void updateStateTime(float deltaTime) {
        this.stateTime += deltaTime;
    }

    private void updateComponents(float deltaTime) {
        healthComponent.update(deltaTime);
        attackComponent.update(deltaTime);
    }

    private void handleInput(float deltaTime) {
        if (attackComponent.isAttacking()) {
            return;
        }

        if (isAttackKeyPressed() && attackComponent.canAttack()) {
            attackComponent.startAttack();
            return;
        }

        handleMovement(deltaTime);
    }

    private void handleMovement(float deltaTime) {
        Vector2 direction = getMovementInput();

        if (direction.len2() > 0) {
            updateDirection(direction);
            movementComponent.move(direction, deltaTime, collisionMap);
        }

        movementComponent.setMoving(direction.len2() > 0);
    }

    private Vector2 getMovementInput() {
        Vector2 direction = new Vector2();

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) direction.x -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) direction.x += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) direction.y += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) direction.y -= 1;

        return direction;
    }

    private void updateDirection(Vector2 direction) {
        if (direction.x < 0) {
            currentDirection = PlayerDirection.createLeft();
        } else if (direction.x > 0) {
            currentDirection = PlayerDirection.createRight();
        } else if (direction.y > 0) {
            currentDirection = PlayerDirection.createUp();
        } else if (direction.y < 0) {
            currentDirection = PlayerDirection.createDown();
        }
    }

    private boolean isAttackKeyPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.X);
    }

    /** Dessine le joueur à l'écran. */
    public void draw(SpriteBatch batch) {
        if (healthComponent.shouldBlink()) {
            return;
        }

        animationComponent.draw(
            batch,
            getCurrentAnimationState(),
            getAnimationTime(),
            movementComponent.getPosition(),
            PLAYER_WIDTH,
            PLAYER_HEIGHT
        );
    }

    private PlayerAnimationState getCurrentAnimationState() {
        if (attackComponent.isAttacking()) {
            return PlayerAnimationState.createAttack(currentDirection);
        }

        if (movementComponent.isMoving()) {
            return PlayerAnimationState.createWalk(currentDirection);
        }

        return PlayerAnimationState.createIdle(currentDirection);
    }

    private float getAnimationTime() {
        return attackComponent.isAttacking()
            ? attackComponent.getAttackTime()
            : stateTime;
    }

    /** Récupère la hitbox d'attaque si le joueur attaque. */
    public Rectangle getAttackHitbox() {
        if (!attackComponent.isAttacking() || attackComponent.isHitRegistered()) {
            return null;
        }

        attackComponent.registerHit();
        return createAttackHitbox();
    }

    private Rectangle createAttackHitbox() {
        Vector2 pos = movementComponent.getPosition();
        Rectangle hitbox = new Rectangle();
        float range = attackComponent.getRange();

        if (currentDirection.isDown()) {
            hitbox.set(pos.x, pos.y - range, PLAYER_WIDTH, range);
        } else if (currentDirection.isUp()) {
            hitbox.set(pos.x, pos.y + PLAYER_HEIGHT, PLAYER_WIDTH, range);
        } else if (currentDirection.isLeft()) {
            hitbox.set(pos.x - range, pos.y, range, PLAYER_HEIGHT);
        } else if (currentDirection.isRight()) {
            hitbox.set(pos.x + PLAYER_WIDTH, pos.y, range, PLAYER_HEIGHT);
        }

        return hitbox;
    }

    //Accesseurs publics

    public void takeDamage(int damage) {
        healthComponent.takeDamage(damage);
    }

    public void heal(int amount) {
        healthComponent.heal(amount);
    }

    public boolean isAlive() {
        return healthComponent.isAlive();
    }

    public int getCurrentHealth() {
        return healthComponent.getCurrentHealth();
    }

    public int getMaxHealth() {
        return healthComponent.getMaxHealth();
    }

    public void setMaxHealth(int maxHealth) {
        healthComponent.setMaxHealth(maxHealth);
    }

    public float getCenterX() {
        return movementComponent.getCenterX();
    }

    public float getCenterY() {
        return movementComponent.getCenterY();
    }

    public void setCenter(float x, float y) {
        movementComponent.setCenter(x, y);
    }

    public int getAttackDamage() {
        return attackComponent.getDamage();
    }

    public void setAttackDamage(int damage) {
        attackComponent.setDamage(damage);
    }

    public void dispose() {
        animationComponent.dispose();
    }
}
