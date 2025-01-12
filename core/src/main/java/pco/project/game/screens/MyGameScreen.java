package pco.project.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import pco.project.game.GlobalConstant;
import pco.project.game.MyGame;
import pco.project.game.playerPack.Player;
import pco.project.game.zombiePack.Zombie;
import pco.project.game.zombiePack.ZombieManager;


public class MyGameScreen implements Screen {
    private final MyGame game;
    private final OrthographicCamera gamecam;
    private final FitViewport viewport; //to ensure the correct viewport
    private final TiledMap map;

    private final OrthogonalTiledMapRenderer renderer;

    private final World world;
    protected Box2DDebugRenderer b2dr;//to give graph repr
    private final BodyDef bdef;
    private final FixtureDef fdef;
    private final Player player;
    float mapHeight, mapWidth;

    private final ZombieManager listeDeZombie;

    public MyGameScreen(MyGame game) {
        this.game = game;

        //set in up the camera to zoom on the map
        gamecam = new OrthographicCamera();

        TmxMapLoader mapLoader = new TmxMapLoader();
        map = mapLoader.load("map/Tilesets/MyMap.tmx");
        mapHeight = map.getProperties().get("height", Integer.class) * 128;
        mapWidth = map.getProperties().get("width", Integer.class) * 128;
        viewport = new FitViewport(Gdx.graphics.getWidth() / 128f, Gdx.graphics.getHeight() / 128f, gamecam);

        renderer = new OrthogonalTiledMapRenderer(map, 1 / 128f);

        //box2D
        world = new World(new Vector2(0, 0)/*for gravity*/, true);
        b2dr = new Box2DDebugRenderer(); //for the bodies
        bdef = new BodyDef();
        fdef = new FixtureDef();
        player = new Player(world);
        gamecam.position.set(player.body.getPosition().x, player.body.getPosition().y, 0f);

        listeDeZombie = new ZombieManager(world, 20, player, game.batch);
        Gdx.app.log("FPS", "Current FPS: " + Gdx.graphics.getFramesPerSecond());
//        initializeFoodobj();
//        cacheTextureObjects();
    }

    @Override
    public void show() {

    }

    public void handleInput(float dt){
        float distToMove = 32f * dt;

        //handling the zoom
        if (Gdx.input.isKeyPressed(Input.Keys.Z)) // Zoom in
            gamecam.zoom -= 0.02f;
        if (Gdx.input.isKeyPressed(Input.Keys.X)) // Zoom out
            gamecam.zoom += 0.02f;

        player.handleInput(dt);
        gamecam.zoom = MathUtils.clamp(gamecam.zoom, 0f, 4.0f);
        clampCameraPosition(mapWidth, mapHeight);
    }

    public void update(float dt){
        handleInput(dt);
        handlePickableInteractions(dt);
        world.step(1/60f, 6, 2);
        player.update(dt);

        // Mise à jour de la position de la caméra en fonction du joueur
        gamecam.position.x = player.body.getPosition().x;
        gamecam.position.y = player.body.getPosition().y;
        gamecam.update();

        renderer.setView(gamecam);
        listeDeZombie.update(dt);

        // Clamp pour empêcher la caméra de sortir des limites de la carte
        clampCameraPosition(mapWidth, mapHeight);

        if (listeDeZombie.isCallGameOver()) {
            int playerScore = 0;
            for (Zombie zombie : listeDeZombie.getZombieList()) {
                if (zombie.isDead()) {
                    playerScore++;
                }
            }
            game.setScreen(new GameOverScreen(game, playerScore));
        }
    }

    private void clampCameraPosition(float mapWidth, float mapHeight) {
        float halfViewportWidth = viewport.getWorldWidth() / 2;
        float halfViewportHeight = viewport.getWorldHeight() / 2;

        if (gamecam.position.x - halfViewportWidth < 0) {
            gamecam.position.x = halfViewportWidth; // Ensure camera doesn't move past the left
        }
        if (gamecam.position.x + halfViewportWidth > mapWidth) {
            gamecam.position.x = mapWidth - halfViewportWidth; // Ensure camera doesn't move past the right
        }

        // Clamping the Y position (top and bottom boundaries)
        if (gamecam.position.y - halfViewportHeight < 0) {
            gamecam.position.y = halfViewportHeight; // Ensure camera doesn't move past the bottom
        }
        if (gamecam.position.y + halfViewportHeight > mapHeight) {
            gamecam.position.y = mapHeight - halfViewportHeight; // Ensure camera doesn't move past the top
        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();

        gamecam.position.x = player.body.getPosition().x;
        gamecam.position.y = player.body.getPosition().y;

        renderer.setView(gamecam);
        renderer.render();
        listeDeZombie.render(game.batch);

        for (MapLayer layer : map.getLayers()) {
            if (layer.getName().startsWith("obj-")) {
                MapObjects objects = layer.getObjects();
                for (MapObject object : objects) {
                    //each layer with collidable objects has a personalised priority collidable in tiled and each collidable obj is defined as one
                    if (object instanceof TextureMapObject) {
                        TextureMapObject textureObject = (TextureMapObject) object;

                        // Get texture region
                        TextureRegion region = textureObject.getTextureRegion();
                        float unitScale = 1 / 128f;

                        // Get properties for size and position
                        MapProperties properties = object.getProperties();
                        float objectX = properties.get("x", Float.class) * unitScale;
                        float objectY = properties.get("y", Float.class) * unitScale;
                        float objectWidth = properties.get("width", Float.class) * unitScale;
                        float objectHeight = properties.get("height", Float.class) * unitScale;
                        float rotation = textureObject.getRotation(); // Rotation in degrees
                        //System.out.println(objectWidth + "Et height is " +objectHeight+ "et rotation est "+ rotation);

                        //Handling collision
                        //System.out.println(properties.get("Collidable", Boolean.class));
                        if (properties.get("Collidable", Boolean.class) != null && properties.get("Collidable", Boolean.class)) {
                            //System.out.println("i am here ");
                            createBox2dCollision(objectX, objectY, objectWidth, objectHeight, rotation);
                            fdef.filter.categoryBits = GlobalConstant.CATEGORY_OBJECT; // Category for objects
                            fdef.filter.maskBits = GlobalConstant.CATEGORY_PLAYER;
                        }
                        else if (properties.get("Pickable", Boolean.class) != null && properties.get("Pickable", Boolean.class)) {
                            createBox2dCollision(objectX, objectY, objectWidth, objectHeight, rotation);
                            fdef.filter.categoryBits = GlobalConstant.CATEGORY_FOOD; // Category for objects
                            fdef.filter.maskBits = GlobalConstant.CATEGORY_PLAYER;
                        }

                        game.batch.draw(region, objectX, objectY, // Adjusted position for rotation origin
                            0, 0,                   // Origin for rotation
                            objectWidth, objectHeight,          // Size (scaled by unitScale)
                            1, 1,                               // Scaling factors
                            -rotation /* Rotation in degrees*/);
                    }
                }
            }
            else if(layer.getName().equals("Food")) { // Only process the food layer
                MapObjects objects = layer.getObjects();
                for (MapObject object : objects) {
                    if (object instanceof TextureMapObject) {
                        TextureMapObject textureObject = (TextureMapObject) object;

                        // Only render available objects
                        MapProperties properties = textureObject.getProperties();
                        if (properties.get("Available", Boolean.class) == null || properties.get("Available", Boolean.class)) {
                            // Render the object
                            TextureRegion region = textureObject.getTextureRegion();
                            float unitScale = renderer.getUnitScale();
                            float objectX = properties.get("x", Float.class) * unitScale;
                            float objectY = properties.get("y", Float.class) * unitScale;
                            float objectWidth = properties.get("width", Float.class) * unitScale;
                            float objectHeight = properties.get("height", Float.class) * unitScale;

                            game.batch.draw(region, objectX, objectY, 0, 0, objectWidth, objectHeight, 1, 1, 0f);
                        }
                    }
                }
            }
        }

        //food rendering
        //game.batch.draw(player.getTexture(), player.getX(), player.getY(), player.getWidth(), player.getHeight());
        //System.out.println(rotList);
        player.render(game.batch);
        game.batch.end();
        //b2dr.render(world, gamecam.combined);
    }

    public void createBox2dCollision(float x, float y, float objWidth, float objHeight, float rotation) {
        // Convert rotation to radians
        float rotationRad = MathUtils.degreesToRadians * rotation;

        // Translate back to the original position, now with the rotated center
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(objWidth / 2f, objHeight / 2f);

        //creation of the fixtures
        fdef.shape = shape;
        fdef.isSensor = false;

        /** since tiled make the rotation according to the top left corner and box2d makes it according to the center
        I made "la translation du centre " to the where should it be after the rotation according to the top left corner and then i rotated the shape**/
        //creating the body
        bdef.type = BodyDef.BodyType.StaticBody;
        Vector2 center = rotatePoint(new Vector2(x+objWidth/2, y+objHeight/2), new Vector2(x, y), -rotationRad);
        bdef.position.set(center);
        bdef.angle = -rotationRad;
        Body body = world.createBody(bdef);
        // to attach body with the fixture
        body.createFixture(fdef);

        shape.dispose();
    }

    private static Vector2 rotatePoint(Vector2 point, Vector2 origin, float angleRad) {
        float translatedX = point.x - origin.x;
        float translatedY = point.y - origin.y;

        float rotatedX = translatedX * MathUtils.cos(angleRad) - translatedY * MathUtils.sin(angleRad);
        float rotatedY = translatedX * MathUtils.sin(angleRad) + translatedY * MathUtils.cos(angleRad);

        return new Vector2(rotatedX + origin.x, rotatedY + origin.y);
    }

    //handling the food objects
    private void handlePickableInteractions(float dt) {
        // Iterate over all objects in the food layer
        MapLayer foodLayer = map.getLayers().get("Food");
        MapObjects objects = foodLayer.getObjects();

        for (MapObject object : objects) {
            if (object instanceof TextureMapObject) {
                TextureMapObject textureObject = (TextureMapObject) object;

                // Get properties
                MapProperties properties = textureObject.getProperties();
                float unitScale = renderer.getUnitScale(); // Ensure consistent scaling
                float objectX = properties.get("x", Float.class) * unitScale;
                float objectY = properties.get("y", Float.class) * unitScale;
                float objectWidth = properties.get("width", Float.class) * unitScale;
                float objectHeight = properties.get("height", Float.class) * unitScale;

                // Check proximity to the player
                if (player.body.getPosition().dst(objectX + objectWidth / 2, objectY + objectHeight / 2) < 1f) { // Adjust range
                    if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                        // Pick the object: Remove it temporarily and increase health
                        properties.put("Available", false); // Mark as unavailable
                        player.increaseHealth(); // Adjust health increment

                        // Schedule reappearance after a delay 5 seconds
                        scheduleReappearance(object, 5f);
                    }
                }
            }
        }
    }

    //reappearance of the food
    private void scheduleReappearance(MapObject object, float delay) {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                object.getProperties().put("Available", true); // Mark as available again
            }
        }, delay);
    }

    @Override
    public void resize(int width,int height) {
        viewport.setWorldSize(width / 128f, height / 128f);
        viewport.update(width, height);
        gamecam.position.set(player.body.getPosition().x, player.body.getPosition().y, 0);
        gamecam.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        game.batch.dispose();
        listeDeZombie.dispose();
    }

    //public void setScreen(Screen screen);
}
