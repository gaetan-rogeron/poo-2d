package gle.game2d.enemy;

// Toute classe qui implémente IBossDeathListener devra avoir la méthode onBossDeath() (le but d'une interface en fait)
// On va l'utiliser pour arreter le jeu

public interface IBossDeathListener {
        void onBossDeath();
}


