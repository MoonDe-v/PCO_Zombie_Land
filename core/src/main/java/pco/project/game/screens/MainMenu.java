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
import pco.project.game.MyGame;

public class MainMenu implements Screen{
    private final MyGame game;
    private Stage stage; //pour gerer les actors
    private Texture image;
    private TextButton startButton;
    private Skin skin;

    public MainMenu(MyGame game) {
        this.game = game;
        image = new Texture("temp.png");

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);



        //System.out.println("Dimensions : " + screenWidth + "x" + screenHeight);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f)); // update actors in the stage
       // stage.draw(); // this is required to render UI elements like buttons

        skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));
        startButton = new TextButton("Start the game", skin);
        startButton.setSize(450, 100);
        startButton.setPosition(280, 200);

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MyGameScreen(game)); //to tell mygame to change to the gamescreen
            }
        });
        stage.addActor(startButton);

    }

    @Override
    public void show (){
        Gdx.input.setInputProcessor(stage);
    };

    @Override
    public void render (float delta){
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        game.batch.begin();
        game.batch.draw(image, 0, 0);
        game.batch.end();

        stage.act(delta); //update actors
        stage.draw(); // this is required to render UI elements like buttons

    };


    @Override
    public void resize (int width, int height){};

    @Override
    public void pause (){};

    @Override
    public void resume (){};


    public void hide (){
        Gdx.input.setInputProcessor(null);
        dispose(); //dispose the screen when it is hidden
    };


    public void dispose (){
        image.dispose();
        stage.dispose(); //clean up the stage
        skin.dispose();
    };
}

