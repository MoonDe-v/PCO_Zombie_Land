package pco.project.game.playerPack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.Input;
import lombok.Getter;
import pco.project.game.GlobalConstant;

public class Player extends Sprite {

    private Sprite playerSprite;
    public World world;
    public Body body;
    private TextureAtlas atlas;
    private TextureRegion playerStand;
    private Animation<TextureRegion> walkingAnim, stabbing, shooting, recharging, jumping, running;
    private float wlkTime;
    private TextureRegion currentFrame, healthCurrentFrame;
    private float rotation, scale;
    private int maxHealth = 8, currentHealth = 8;
    @Getter
    private boolean isStabbing = false,  isDead = false;
    private float stabTime = 0f; // Time elapsed for stabbing animation
    private PlayerEnum currentState;


    public Player(World world) {
        this.world = world;
        // [survivor-move_knife, survivor-move_knife, survivor-move_knife, survivor-move_knife, survivor-move_knife, survivor-move_knife, survivor-move_knife, survivor-move_knife, survivor-move_knife, survivor-move_knife, survivor-move_knife, survivor-move_knife, survivor-move_knife, survivor-move_knife, survivor-move_knife, survivor-move_knife, survivor-move_knife, survivor-move_knife, survivor-move_knife, survivor-move_knife]
        atlas = new TextureAtlas("map/Atls_player/Player_mov.atlas");
        playerStand = new TextureRegion(atlas.findRegion("survivor-move_knife", 0));
        playerSprite = new Sprite(playerStand);
        currentFrame = playerStand;
        healthCurrentFrame = new TextureRegion(atlas.findRegion("health", 1));
        wlkTime = 0f;
        definePlayer();
        currentState = PlayerEnum.IDLE;
        this.setBounds(0, 0, GlobalConstant.PlayerWidth, GlobalConstant.PlayerHeight);
        initAnim();
    }

    public void definePlayer(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(GlobalConstant.PosInitX, GlobalConstant.PosInitY);
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.bullet = true;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();

        CircleShape shape = new CircleShape();
        //shape.setAsBox((ConstantVar.PlayerWidth-0.35f)/2f, (ConstantVar.PlayerHeight-0.1f)/2f);
        shape.setRadius(0.15f);
        fdef.shape = shape;
        fdef.isSensor = false;
        fdef.filter.categoryBits = GlobalConstant.CATEGORY_PLAYER; // Category for player
        fdef.filter.maskBits = GlobalConstant.CATEGORY_OBJECT | GlobalConstant.CATEGORY_ZOMBIE;

        body.createFixture(fdef);
        body.setAngularDamping(10f);


        shape.dispose();

        playerSprite.setSize(GlobalConstant.PlayerWidth, GlobalConstant.PlayerHeight);
    }


    public void takeDamage(SpriteBatch batch) {
        currentHealth -= 1;
        if (currentHealth > 1) {
            healthCurrentFrame = atlas.findRegion("health", maxHealth - (currentHealth - 1));
            //healthRender(batch);
        } else {
            isDead = true;
            die();
        }
    }

    public void increaseHealth() {
        currentHealth += 1;
        if (currentHealth > maxHealth) {
            currentHealth = maxHealth; // Cap health to the maximum value
        } else {
            healthCurrentFrame = atlas.findRegion("health", maxHealth - currentHealth + 1);
        }
    }

    public void healthRender(SpriteBatch batch) {
        batch.draw(healthCurrentFrame,
            body.getPosition().x - 0.3f,
            body.getPosition().y + 0.3f,
            0.5f, 0.1f); //

    }
    public void initAnim() {
        //walking with a knife anime
        Array<TextureRegion> walkingLeftKnifeFrames = new Array<TextureRegion>();
        for (int i = 0; i < 20; i++) { // 19: thz size of regions of the player
            walkingLeftKnifeFrames.add(atlas.findRegion("survivor-move_knife", i));
        }
        walkingAnim = new Animation<>(1f, walkingLeftKnifeFrames);
        walkingAnim.setPlayMode(Animation.PlayMode.LOOP);

        //stabbing with a knif anim
        Array<TextureRegion> stabbKnifeFrames = new Array<TextureRegion>();
        for (int i = 0; i < 15; i++) { // 14: thz size of regions of the player
            stabbKnifeFrames.add(atlas.findRegion("survivor-meleeattack_knife", i));
        }
        stabbing = new Animation<>(0.15f, stabbKnifeFrames);
        stabbing.setPlayMode(Animation.PlayMode.NORMAL);

        //Walking with shootgun anim
        Array<TextureRegion> walkingGun = new Array<TextureRegion>();
        for (int i = 0; i < 20; i++) { // 19: thz size of regions of the player
            walkingGun.add(atlas.findRegion("survivor-move_shotgun", i));
        }
        // TODO: create animation for player
    }


//    public void handleInput(float dt) {
//        float moveSpeed = 10f;
//
//        float velX = 0;
//        float velY = 0;
//        scale = 0.8f;
//
//        if (Gdx.input.isKeyJustPressed(Input.Keys.D) && !isStabbing) {
//            System.out.println(isStabbing);
//            isStabbing = true;
//            stabTime = 1.4f;
//            currentState = PlayerEnum.STABBING;
//            body.setLinearVelocity(0, 0);// Stop movement during stabbing
//            scale = 1f;
//
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
//            rotation = 0;
//            velX = moveSpeed;
//            //currentFrame = walkingAnim.getKeyFrame(wlkTime, true);
//            body.setLinearVelocity(moveSpeed, 0);
//            currentState = PlayerEnum.WALKING;
//        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
//            rotation = 180;
//            velX = -moveSpeed;
//            //currentFrame = walkingAnim.getKeyFrame(wlkTime, true);
//            body.setLinearVelocity(-moveSpeed, 0);
//            currentState = PlayerEnum.WALKING;
//        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
//            rotation = 270;
//            velY = -moveSpeed;
//            //currentFrame = walkingAnim.getKeyFrame(wlkTime, true);
//            currentState = PlayerEnum.WALKING;
//            body.setLinearVelocity(0, -moveSpeed);
//        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
//            rotation = 90;
//            velY = moveSpeed;
//            currentState = PlayerEnum.WALKING;
//            //currentFrame = walkingAnim.getKeyFrame(wlkTime, true);
//            body.setLinearVelocity(0, moveSpeed);
//        }else {
//            body.setLinearVelocity(velX, velY);
//        }
//    }

    public void handleInput(float dt) {
        float moveSpeed = 10f;
        float velX = 0;
        float velY = 0;
        scale = 0.8f;

        // Input for stabbing action (no change)
        if (Gdx.input.isKeyJustPressed(Input.Keys.D) && !isStabbing) {
            //System.out.println(isStabbing);
            isStabbing = true;
            //stabTime = 1.4f;
            currentState = PlayerEnum.STABBING;
            body.setLinearVelocity(0, 0); // Stop movement during stabbing
            scale = 1f;
        }

        if (!isStabbing) {// Movement based on arrow keys
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                velX = moveSpeed;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                velX = -moveSpeed;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                velY = moveSpeed;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                velY = -moveSpeed;
            }

            // Calculate the angle for rotation based on velocity
            if (velX != 0 || velY != 0) {
                rotation = (float) Math.toDegrees(Math.atan2(velY, velX));
            }

            // Set linear velocity to move the player in the direction of the input
            body.setLinearVelocity(velX, velY);
            currentState = (velX != 0 || velY != 0) ? PlayerEnum.WALKING : PlayerEnum.IDLE;
        }
    }

    public void die() {
        body.setActive(false);

    }

    public void update(float dt) {
        wlkTime += dt;
        if (isDead) return;
        if (isStabbing) {
            stabTime += dt;
        }
        if (isStabbing && stabbing.isAnimationFinished(stabTime)) {
            isStabbing = false; // Reset stabbing state after animation finishes
            currentState = PlayerEnum.IDLE;
            stabTime = 0f;
        }
        handleInput(dt);
        this.setPosition(body.getPosition().x, body.getPosition().y);
        //takeDamage();
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = switch (currentState) {
            case WALKING -> walkingAnim.getKeyFrame(wlkTime, true);
            case STABBING -> stabbing.getKeyFrame(stabTime, true);
            default -> playerStand;
        };

        batch.draw(currentFrame, body.getPosition().x - playerSprite.getWidth() / 2f,
            body.getPosition().y - playerSprite.getHeight() / 2f,
            playerSprite.getWidth() / 2f, playerSprite.getHeight() / 2f,
            playerSprite.getWidth(), playerSprite.getHeight(),
            scale, scale, rotation);

        healthRender(batch);
    }

    public void dispose() {
        if (atlas != null)
            atlas.dispose();
    }
}
