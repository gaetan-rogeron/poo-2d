package gle.game2d.object;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import gle.game2d.player.Player;

/** Interface pour les objets collectables du jeu. */
public interface ICollectable {
    /**
     * Met à jour l'objet.
     * @param deltaTime Temps écoulé depuis la dernière frame
     */
    public void update(float deltaTime);

    /**
     * Dessine l'objet.
     * @param batch SpriteBatch pour le rendu
     */
    public void draw(SpriteBatch batch);

    /**
     * Appelé quand le joueur collecte cet objet.
     * @param player Le joueur qui collecte l'objet
     */
    public void onCollect(Player player);

    /**
     * Retourne les limites de l'objet pour la détection de collision.
     * @return Rectangle représentant la hitbox de l'objet
     */
    Rectangle getBounds();

    /**
     * Vérifie si l'objet a été collecté.
     * @return true si l'objet a été collecté
     */
    boolean isCollected();

    /**
     * Libère les ressources.
     */
    public void dispose();
}
