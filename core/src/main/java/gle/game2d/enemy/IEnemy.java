package gle.game2d.enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import gle.game2d.player.Player;

/**
 * Interface définissant le comportement d'un ennemi.
 * Applique le principe Dependency Inversion (dépendance sur abstraction).
 *
 * @author Votre Nom
 * @version 1.0
 */
public interface IEnemy {
    /**
     * Met à jour l'état de l'ennemi.
     *
     * @param deltaTime temps écoulé depuis la dernière frame
     * @param player référence au joueur
     */
    void update(float deltaTime, Player player);

    /**
     * Dessine l'ennemi à l'écran.
     *
     * @param batch SpriteBatch pour le rendu
     */
    void draw(SpriteBatch batch);

    /**
     * Dessine la barre de vie de l'ennemi.
     *
     * @param shapeRenderer renderer pour les formes
     */
    void drawHealthBar(ShapeRenderer shapeRenderer);

    /**
     * Inflige des dégâts à l'ennemi.
     *
     * @param damage montant des dégâts
     */
    void takeDamage(int damage);

    /**
     * Vérifie si l'ennemi est vivant.
     *
     * @return true si vivant
     */
    boolean isAlive();

    /**
     * Obtient les limites rectangulaires de l'ennemi pour les collisions.
     *
     * @return rectangle de collision
     */
    Rectangle getBounds();

    /**
     * Obtient les dégâts que l'ennemi inflige.
     *
     * @return montant des dégâts
     */
    int getDamage();

    /**
     * Libère les ressources utilisées par l'ennemi.
     */
    void dispose();
}
