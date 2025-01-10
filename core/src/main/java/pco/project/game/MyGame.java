package pco.project.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import pco.project.game.screens.MainMenu;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MyGame extends Game {
    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new MainMenu(this));



    }

    @Override
    public void render() {
        super.render();

    }

    @Override
    public void dispose() {
        batch.dispose();

    }


}
