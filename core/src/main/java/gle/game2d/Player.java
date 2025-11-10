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

    private Animation<TextureRegion> currentWalk;
    private Direction facing = Direction.DOWN;
    private boolean moving = false;
    private float stateTime = 0f;

    private final CollisionMap coll;
    private final int W = 16, H = 16;

    private float x = 240 - 8, y = 160 - 8;
    private final float speed = 100f;

    private enum Direction { DOWN, RIGHT, UP, LEFT }

    public Player(CollisionMap coll) {
        this.coll = coll;

        sheet = new Texture(Gdx.files.internal("maps/player.png"));
        grid = TextureRegion.split(sheet, 32, 32);

        idleDown  = animFromRow(0, 0.20f, true);
        idleRight = animFromRow(1, 0.20f, true);
        idleUp    = animFromRow(2, 0.20f, true);
        idleLeft  = animFromRowFlipped(1, 0.20f);

        walkDown  = animFromRow(3, 0.12f, false);
        walkRight = animFromRow(4, 0.12f, false);
        walkUp    = animFromRow(5, 0.12f, false);
        walkLeft  = animFromRowFlipped(4, 0.12f);

        currentWalk = walkDown;
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

    private boolean wouldCollide(float testX, float testY) {
        return coll.collides(testX, testY, W, H);
    }

    private TextureRegion currentFrame() {
        if (moving) return currentWalk.getKeyFrame(stateTime);
        switch (facing) {
            case RIGHT: return idleRight.getKeyFrame(stateTime);
            case UP:    return idleUp.getKeyFrame(stateTime);
            case LEFT:  return idleLeft.getKeyFrame(stateTime);
            default:    return idleDown.getKeyFrame(stateTime);
        }
    }

    public void draw(SpriteBatch batch) {
        batch.draw(currentFrame(), x, y, W, H);
    }

    public float getCenterX() { return x + W / 2f; }
    public float getCenterY() { return y + H / 2f; }
    public void setCenter(float cx, float cy) { x = cx - W / 2f; y = cy - H / 2f; }

    public void dispose() { sheet.dispose(); }
}
