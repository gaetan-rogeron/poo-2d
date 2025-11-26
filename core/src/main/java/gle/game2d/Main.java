package gle.game2d;

import com.badlogic.gdx.Game;
import gle.game2d.ui.FirstScreen;

/** Classe principale du jeu. Point d'entrée de l'application LibGDX. Gère l'initialisation et le cycle de vie du jeu. */
public class Main extends Game {
    /** Appelé lors de la création du jeu. Initialise l'écran principal. */
    @Override
    public void create() {
        System.out.println("Démarrage du jeu");
        System.out.println("Framework: LibGDX");
        System.out.println();
        System.out.println("Commandes:");
        System.out.println("  - Flèches directionnelles: Déplacer le joueur");
        System.out.println("  - Espace/X: Attaquer");
        System.out.println("  - T: Changer la stratégie de transition de zone");
        System.out.println("  - +/-: Ajuster la vitesse de transition");
        System.out.println();

        setScreen(new FirstScreen(this));
    }
}
