package gle.game2d.enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import gle.game2d.player.Player;

/** Interface définissant le comportement d'un ennemi. Applique le principe Dependency Inversion (dépendance sur abstraction). */
public interface IEnemy {
    /** Met à jour l'état de l'ennemi. */
    public void update(float deltaTime, Player player);

    /** Dessine l'ennemi à l'écran. */
    public void draw(SpriteBatch batch);

    /** Dessine la barre de vie de l'ennemi. */
    public void drawHealthBar(ShapeRenderer shapeRenderer);

    /** Inflige des dégâts à l'ennemi. */
    public void takeDamage(int damage);

    /** Vérifie si l'ennemi est vivant. */
    boolean isAlive();

    /** Obtient les limites rectangulaires de l'ennemi pour les collisions. */
    Rectangle getBounds();

    /** Obtient les dégâts que l'ennemi inflige. */
    int getDamage();

    /** Libère les ressources utilisées par l'ennemi. */
    public void dispose();
}
