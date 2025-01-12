package pco.project.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.graphics.Pixmap;
import pco.project.game.MyGame;

public class MainMenu implements Screen {
    private final MyGame game;
    private Stage stage;
    private Texture image;
    private TextButton startButton;
    private TextButton howToPlayButton;
    private Skin skin;
    private Table howToPlayPanel;

    public MainMenu(MyGame game) {
        this.game = game;
        image = new Texture("background.png");

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

        // Button: Start Game
        startButton = new TextButton("Start the game", skin);
        startButton.setSize(450, 100);
        startButton.setPosition(280, 200);

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MyGameScreen(game));
            }
        });

        // Button: How to Play
        howToPlayButton = new TextButton("How to play", skin);
        howToPlayButton.setSize(450, 100);
        howToPlayButton.setPosition(280, 80);

        howToPlayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showHowToPlayPanel();
            }
        });

        // Add buttons to the stage
        stage.addActor(startButton);
        stage.addActor(howToPlayButton);

        // Create the "How to Play" panel (hidden by default)
        createHowToPlayPanel();
    }

    private void createHowToPlayPanel() {
        // Créer un fond gris (rectangle)
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0, 0, 0, 0.8f));
        pixmap.fill();
        Image background = new Image(new Texture(pixmap));
        pixmap.dispose();

        // Créer le contenu du label
        Label instructions = new Label(
            "Objective: Kill all Zombies to survive.\n\n" +
                "Weapons:\n" +
                " - Knife or Firearm:\n" +
                "   Press 'P' to switch between Knife and Firearm\n" +
                "   Press 'D' to attack with the Knife or shoot with the Firearm\n\n" +
                "Health:\n" +
                " - Press 'E' to pick up blood objects and restore HP\\n\n\n" +
                "Movement:\n" +
                " - Use the arrow keys to move\n" +
                " - Move diagonally by combining two directions (e.g. down + right)\n\n" +
                "Zoom:\n" +
                " - Zoom in with 'W'\n" +
                " - Zoom out with 'X'\n\n" +
                "Interact:\n" +
                " - Press 'E' to pick up items\n", skin);
        instructions.setWrap(true);

        // Créer le bouton de fermeture
        TextButton closeButton = new TextButton("Close", skin, "default");
        closeButton.setColor(Color.RED);
        closeButton.getLabel().setFontScale(0.5f);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                howToPlayPanel.setVisible(false); // Masquer le panneau
            }
        });

        // Créer une table pour contenir tout
        howToPlayPanel = new Table();
        howToPlayPanel.setBackground(background.getDrawable());
        howToPlayPanel.add(instructions).pad(10).width(500).row(); // Augmenter la largeur du label
        howToPlayPanel.add(closeButton).padTop(10).center().size(120, 50); // Augmenter la taille du bouton
        howToPlayPanel.setSize(800, 600); // Augmenter la taille du panneau
        howToPlayPanel.setPosition(
            (Gdx.graphics.getWidth() - howToPlayPanel.getWidth()) / 2,
            (Gdx.graphics.getHeight() - howToPlayPanel.getHeight()) / 2
        );

        howToPlayPanel.setVisible(false); // Commence masqué
        stage.addActor(howToPlayPanel);
    }


    private void showHowToPlayPanel() {
        howToPlayPanel.setVisible(true); // Show the panel
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        game.batch.begin();
        game.batch.draw(image, 0, 0);
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    public void hide() {
        Gdx.input.setInputProcessor(null);
        dispose();
    }

    public void dispose() {
        image.dispose();
        stage.dispose();
        skin.dispose();
    }
}
