package gle.game2d;

import com.badlogic.gdx.Game;
import gle.game2d.ui.FirstScreen;

public class Main extends Game {
    @Override
    public void create() {
        setScreen(new FirstScreen(this));
    }
}
