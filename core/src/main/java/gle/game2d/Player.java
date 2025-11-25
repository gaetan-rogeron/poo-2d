package gle.game2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player {
    private final Texture sheet;
    private final TextureRegion[][] grid;

    private Animation<TextureRegion> idleDown, idleRight, idleUp, idleLeft;
    private Animation<TextureRegion> walkDown, walkRight, walkUp, walkLeft;
    private Animation<TextureRegion> attackDown, attackRight, attackUp, attackLeft;

    private Animation<TextureRegion> currentWalk;
    private Animation<TextureRegion> currentAttack;
    private Direction facing = Direction.DOWN;
    private boolean moving = false;
    private boolean attacking = false;
    private boolean attackHitRegistered = false; // Pour éviter de frapper plusieurs fois
    private float stateTime = 0f;
    private float attackTime = 0f;
    private static final float ATTACK_DURATION = 0.4f; // Durée de l'animation d'attaque

    private final CollisionMap coll;
    private final int W = 16, H = 16;

    private float x = 240 - 8, y = 160 - 8;
    private final float speed = 100f;

    // Système de vie
    private int maxHealth = 100;
    private int currentHealth = 100;
    private boolean invincible = false;
    private float invincibilityTimer = 0f;
    private static final float INVINCIBILITY_DURATION = 1.0f;

    // Système d'attaque
    private int attackDamage = 10;
    private float attackRange = 20f; // Portée de l'attaque
    private float attackCooldown = 0f;
    private static final float ATTACK_COOLDOWN_TIME = 0.5f;

    private enum Direction { DOWN, RIGHT, UP, LEFT }

    public Player(CollisionMap coll) {
        this.coll = coll;

        sheet = new Texture(Gdx.files.internal("maps/player.png"));
        grid = TextureRegion.split(sheet, 32, 32);

        // Idle animations (lignes 0-2)
        idleDown  = animFromRow(0, 0.20f, true);
        idleRight = animFromRow(1, 0.20f, true);
        idleUp    = animFromRow(2, 0.20f, true);
        idleLeft  = animFromRowFlipped(1, 0.20f);

        // Walk animations (lignes 3-5)
        walkDown  = animFromRow(3, 0.12f, false);
        walkRight = animFromRow(4, 0.12f, false);
        walkUp    = animFromRow(5, 0.12f, false);
        walkLeft  = animFromRowFlipped(4, 0.12f);

        // Attack animations (lignes 6-8 selon ton sprite sheet)
        attackDown  = animFromRow(6, 0.08f, false);
        attackRight = animFromRow(7, 0.08f, false);
        attackUp    = animFromRow(8, 0.08f, false);
        attackLeft  = animFromRowFlipped(7, 0.08f);

        currentWalk = walkDown;
        currentAttack = attackDown;
    }

    private Animation<TextureRegion> animFromRow(int row, float frameDur, boolean pingpong) {
        TextureRegion[] frames = new TextureRegion[6];
        for (int c = 0; c < 6; c++) frames[c] = grid[row][c];
        Animation<TextureRegion> a = new Animation<>(frameDur, frames);
        a.setPlayMode(pingpong ? Animation.PlayMode.LOOP_PINGPONG : Animation.PlayMode.LOOP);
        return a;
    }

    private Animation<TextureRegion> animFromRowFlipped(int row, float frameDur) {
        TextureRegion[] frames = new TextureRegion[6];
        for (int c = 0; c < 6; c++) {
            frames[c] = new TextureRegion(grid[row][c]);
            frames[c].flip(true, false);
        }
        Animation<TextureRegion> a = new Animation<>(frameDur, frames);
        a.setPlayMode(Animation.PlayMode.LOOP);
        return a;
    }

    public void update(float dt) {
        // Gestion de l'invincibilité
        if (invincible) {
            invincibilityTimer -= dt;
            if (invincibilityTimer <= 0) {
                invincible = false;
            }
        }

        // Gestion du cooldown d'attaque
        if (attackCooldown > 0) {
            attackCooldown -= dt;
        }

        // Gestion de l'attaque
        if (attacking) {
            attackTime += dt;
            if (attackTime >= ATTACK_DURATION) {
                attacking = false;
                attackTime = 0f;
                attackHitRegistered = false; // Reset pour la prochaine attaque
            }
        }

        // Si on attaque, on ne peut pas se déplacer
        if (attacking) {
            stateTime += dt;
            return;
        }

        // Détection de l'attaque (Espace ou X)
        if ((Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.X)) &&
            attackCooldown <= 0) {
            startAttack();
            return;
        }

        // Déplacement normal
        float dx = 0f, dy = 0f;
        moving = false;

        boolean left  = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean up    = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean down  = Gdx.input.isKeyPressed(Input.Keys.DOWN);

        if (left)  { dx -= speed * dt; currentWalk = walkLeft;  facing = Direction.LEFT;  moving = true; }
        if (right) { dx += speed * dt; currentWalk = walkRight; facing = Direction.RIGHT; moving = true; }
        if (up)    { dy += speed * dt; currentWalk = walkUp;    facing = Direction.UP;    moving = true; }
        if (down)  { dy -= speed * dt; currentWalk = walkDown;  facing = Direction.DOWN;  moving = true; }

        if (dx != 0) {
            float newX = x + dx;
            if (dx > 0) {
                if (wouldCollide(newX, y)) {
                    int ts = coll.getTileSize();
                    int rightTile = (int)Math.floor((x + dx + W - 1) / ts);
                    x = rightTile * ts - W;
                } else x = newX;
            } else {
                if (wouldCollide(newX, y)) {
                    int ts = coll.getTileSize();
                    int leftTile = (int)Math.floor((x + dx) / ts);
                    x = (leftTile + 1) * ts;
                } else x = newX;
            }
        }

        if (dy != 0) {
            float newY = y + dy;
            if (dy > 0) {
                if (wouldCollide(x, newY)) {
                    int ts = coll.getTileSize();
                    int topTile = (int)Math.floor((y + dy + H - 1) / ts);
                    y = topTile * ts - H;
                } else y = newY;
            } else {
                if (wouldCollide(x, newY)) {
                    int ts = coll.getTileSize();
                    int bottomTile = (int)Math.floor((y + dy) / ts);
                    y = (bottomTile + 1) * ts;
                } else y = newY;
            }
        }

        stateTime += dt;
    }

    private void startAttack() {
        attacking = true;
        attackTime = 0f;
        attackCooldown = ATTACK_COOLDOWN_TIME;

        // Sélectionner l'animation d'attaque selon la direction
        switch (facing) {
            case DOWN:  currentAttack = attackDown; break;
            case RIGHT: currentAttack = attackRight; break;
            case UP:    currentAttack = attackUp; break;
            case LEFT:  currentAttack = attackLeft; break;
        }

        System.out.println("Attack!");
        // Ici tu pourras ajouter la logique pour détecter les ennemis touchés
    }

    private boolean wouldCollide(float testX, float testY) {
        return coll.collides(testX, testY, W, H);
    }

    private TextureRegion currentFrame() {
        // Si en train d'attaquer, afficher l'animation d'attaque
        if (attacking) {
            return currentAttack.getKeyFrame(attackTime);
        }

        // Sinon animation normale
        if (moving) return currentWalk.getKeyFrame(stateTime);
        switch (facing) {
            case RIGHT: return idleRight.getKeyFrame(stateTime);
            case UP:    return idleUp.getKeyFrame(stateTime);
            case LEFT:  return idleLeft.getKeyFrame(stateTime);
            default:    return idleDown.getKeyFrame(stateTime);
        }
    }

    public void draw(SpriteBatch batch) {
        // Si invincible, faire clignoter le joueur
        if (!invincible || (int)(invincibilityTimer * 10) % 2 == 0) {
            batch.draw(currentFrame(), x, y, W, H);
        }
    }

    // Obtenir la zone d'attaque (hitbox)
    public AttackHitbox getAttackHitbox() {
        if (!attacking || attackHitRegistered) return null; // Ne retourner la hitbox qu'une fois par attaque

        float hitboxX = x;
        float hitboxY = y;
        float hitboxW = attackRange;
        float hitboxH = attackRange;

        // Ajuster la hitbox selon la direction
        switch (facing) {
            case DOWN:
                hitboxY = y - attackRange;
                hitboxW = W;
                hitboxH = attackRange;
                break;
            case UP:
                hitboxY = y + H;
                hitboxW = W;
                hitboxH = attackRange;
                break;
            case LEFT:
                hitboxX = x - attackRange;
                hitboxW = attackRange;
                hitboxH = H;
                break;
            case RIGHT:
                hitboxX = x + W;
                hitboxW = attackRange;
                hitboxH = H;
                break;
        }

        attackHitRegistered = true; // Marquer que le coup a été enregistré
        return new AttackHitbox(hitboxX, hitboxY, hitboxW, hitboxH, attackDamage);
    }

    // Méthodes pour gérer la vie
    public void takeDamage(int damage) {
        if (!invincible && currentHealth > 0) {
            currentHealth -= damage;
            if (currentHealth < 0) currentHealth = 0;

            invincible = true;
            invincibilityTimer = INVINCIBILITY_DURATION;

            System.out.println("Player hit! Health: " + currentHealth + "/" + maxHealth);

            if (currentHealth <= 0) {
                onDeath();
            }
        }
    }

    public void heal(int amount) {
        currentHealth += amount;
        if (currentHealth > maxHealth) currentHealth = maxHealth;
        System.out.println("Player healed! Health: " + currentHealth + "/" + maxHealth);
    }

    private void onDeath() {
        System.out.println("Player died!");
    }

    public boolean isAlive() { return currentHealth > 0; }
    public boolean isAttacking() { return attacking; }
    public int getCurrentHealth() { return currentHealth; }
    public int getMaxHealth() { return maxHealth; }
    public void setMaxHealth(int max) {
        maxHealth = max;
        if (currentHealth > maxHealth) currentHealth = maxHealth;
    }

    public float getCenterX() { return x + W / 2f; }
    public float getCenterY() { return y + H / 2f; }
    public void setCenter(float cx, float cy) { x = cx - W / 2f; y = cy - H / 2f; }

    public void dispose() { sheet.dispose(); }

    // Classe interne pour représenter la hitbox d'attaque
    public static class AttackHitbox {
        public final float x, y, width, height;
        public final int damage;

        public AttackHitbox(float x, float y, float width, float height, int damage) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.damage = damage;
        }

        public boolean intersects(float ex, float ey, float ew, float eh) {
            return x < ex + ew && x + width > ex && y < ey + eh && y + height > ey;
        }
    }
}
