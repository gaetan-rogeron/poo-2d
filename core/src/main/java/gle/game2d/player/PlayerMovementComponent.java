package gle.game2d.player;

import gle.game2d.collision.CollisionMap;
import com.badlogic.gdx.math.Vector2;

/** Gère le déplacement du joueur et la détection de collision. Single Responsibility Principle: cette classe s'occupe uniquement du mouvement. */
public class PlayerMovementComponent {
    private final float speed;
    private final int width;
    private final int height;
    private final Vector2 position;
    private boolean moving;

    /** Constructeur. */
    public PlayerMovementComponent(float speed, int width, int height) {
        this.speed = speed;
        this.width = width;
        this.height = height;
        this.position = new Vector2(240 - width/2f, 160 - height/2f);
        this.moving = false;
    }

    /** Déplace le joueur selon une direction. */
    public void move(Vector2 direction, float deltaTime, CollisionMap collisionMap) {
        if (direction.len2() > 0) {
            direction.nor();
        }

        float dx = direction.x * speed * deltaTime;
        float dy = direction.y * speed * deltaTime;

        if (dx != 0) {
            moveX(dx, collisionMap);
        }

        if (dy != 0) {
            moveY(dy, collisionMap);
        }
    }

    private void moveX(float dx, CollisionMap collisionMap) {
        float newX = position.x + dx;

        if (!collisionMap.collides(newX, position.y, width, height)) {
            position.x = newX;
        } else {
            int tileSize = collisionMap.getTileSize();
            if (dx > 0) {
                int rightTile = (int) Math.floor((position.x + dx + width - 1) / tileSize);
                position.x = rightTile * tileSize - width;
            } else {
                int leftTile = (int) Math.floor((position.x + dx) / tileSize);
                position.x = (leftTile + 1) * tileSize;
            }
        }
    }

    private void moveY(float dy, CollisionMap collisionMap) {
        float newY = position.y + dy;

        if (!collisionMap.collides(position.x, newY, width, height)) {
            position.y = newY;
        } else {
            int tileSize = collisionMap.getTileSize();
            if (dy > 0) {
                int topTile = (int) Math.floor((position.y + dy + height - 1) / tileSize);
                position.y = topTile * tileSize - height;
            } else {
                int bottomTile = (int) Math.floor((position.y + dy) / tileSize);
                position.y = (bottomTile + 1) * tileSize;
            }
        }
    }

    //Getters et Setters

    public Vector2 getPosition() {
        return position.cpy();
    }

    public float getCenterX() {
        return position.x + width / 2f;
    }

    public float getCenterY() {
        return position.y + height / 2f;
    }

    public void setCenter(float x, float y) {
        position.x = x - width / 2f;
        position.y = y - height / 2f;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
