package pco.project.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import pco.project.game.MyGame;


public class GameOverScreen implements Screen {
    private Stage stage;
    private MyGame game;
    private BitmapFont font;
    private Label gameOverLabel, scoreLabel;
    private Skin skin;
    private MainMenu mainMenu;

    public GameOverScreen(MyGame game, int score) {
        this.game = game;

        // Initialize UI components
        stage = new Stage(new ScreenViewport());
        font = new BitmapFont();
        skin = new Skin(Gdx.files.internal("uiskin.json")); // A default skin for buttons, etc.

        gameOverLabel = new Label("Game Over", new Label.LabelStyle(font, Color.RED));
        scoreLabel = new Label("Score: " + score, new Label.LabelStyle(font, Color.WHITE));

        // Setup restart button (Back to Main Menu)
        TextButton restartButton = new TextButton("Restart (Main Menu)", skin);
        restartButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenu(game));  // Go back to the main menu
            }
        });

        // Setup quit button
        TextButton quitButton = new TextButton("Quit", skin);
        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();  // Quit the game
            }
        });

        // Add components to the stage
        Table table = new Table();
        table.top().padTop(50);
        table.setFillParent(true);
        table.add(gameOverLabel).padBottom(20).row();
        table.add(scoreLabel).padBottom(50).row();
        table.add(restartButton).padBottom(20).row();
        table.add(quitButton).row();

        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        // Show the screen, stage has already been set up
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        stage.dispose();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}
}
