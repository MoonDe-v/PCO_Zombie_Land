package pco.project.game.playerPack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
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
    private TextureRegion playerStand, playerStandKnife, playerStandGun;
    private Animation<TextureRegion> walkingAnim, stabbing, shootingAnim, walkingGun;
    private float wlkTime;
    private TextureRegion currentFrame, healthCurrentFrame;
    private float rotation, scale;
    private final int maxHealth = 8;
    private int currentHealth = GlobalConstant.PlayerCurrentHealth;
    @Getter
    private boolean isStabbing = false,  isDead = false, isShooting = false, isUsingKnife = true, isUsignGun = false;
    private float stabTime = 0f, shootTime = 0f; // Time elapsed for stabbing animation
    private PlayerEnum currentState;
    private PlayerDirections facingDirection;

    public Player(World world) {
        this.world = world;
        atlas = new TextureAtlas("map/Atls_player/Player_atls.atlas");
        playerStandKnife = new TextureRegion(atlas.findRegion("survivor-move_knife", 0));
        playerStandGun = new TextureRegion(atlas.findRegion("survivor-move_handgun", 0));
        playerStand = playerStandKnife;
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

    //the player direction

    public void updateFacingDirection() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                facingDirection = PlayerDirections.UP_LEFT;
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                facingDirection = PlayerDirections.UP_RIGHT;
            } else {
                facingDirection = PlayerDirections.UP;
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                facingDirection = PlayerDirections.DOWN_LEFT;
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                facingDirection = PlayerDirections.DOWN_RIGHT;
            } else {
                facingDirection = PlayerDirections.DOWN;
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            facingDirection = PlayerDirections.LEFT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            facingDirection = PlayerDirections.RIGHT;
        }
    }

    public Vector2 getFacingDirection() {
        return facingDirection.getDirection();
    }


    public void initAnim() {
        //walking with a knife anime
        Array<TextureRegion> walkingKnifeFrames = new Array<TextureRegion>();
        for (int i = 0; i < 20; i++) { // 19: thz size of regions of the player
            walkingKnifeFrames.add(atlas.findRegion("survivor-move_knife", i));
        }
        walkingAnim = new Animation<>(1f, walkingKnifeFrames);
        walkingAnim.setPlayMode(Animation.PlayMode.LOOP);

        //stabbing with a knif anim
        Array<TextureRegion> stabbKnifeFrames = new Array<TextureRegion>();
        for (int i = 0; i < 15; i++) { // 14: thz size of regions of the player
            stabbKnifeFrames.add(atlas.findRegion("survivor-meleeattack_knife", i));
        }
        stabbing = new Animation<>(0.1f, stabbKnifeFrames);
        stabbing.setPlayMode(Animation.PlayMode.NORMAL);

        //Walking with shootgun anim
        Array<TextureRegion> walkingGunFrames = new Array<TextureRegion>();
        for (int i = 0; i < 20; i++) { // 19: thz size of regions of the player
            walkingGunFrames.add(atlas.findRegion("survivor-move_handgun", i));
        }
        walkingGun = new Animation<>(1.4f, walkingGunFrames);
        walkingGun.setPlayMode(Animation.PlayMode.LOOP);

        // TODO: create gun animation for player
        //Shooting with a gun
        Array<TextureRegion> shootGunFrames = new Array<TextureRegion>();
        for (int i = 0; i < 2; i++) { // 19: thz size of regions of the player
            shootGunFrames.add(atlas.findRegion("survivor-shoot_handgun", i));
        }
        shootingAnim = new Animation<>(0.4f, shootGunFrames);
        shootingAnim.setPlayMode(Animation.PlayMode.NORMAL);

    }

    public void handleInput(float dt) {
        float moveSpeed = 10f;
        float velX = 0;
        float velY = 0;
        scale = 0.8f;

        // Input for player attacking actions
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            //System.out.println("D pressed"); // Debug
            if (isUsingKnife && !isStabbing) {
                //System.out.println("isStabbing: " + isStabbing);  // Debug
                isStabbing = true;
                //stabTime = 1.4f;
                currentState = PlayerEnum.STABBING;
                body.setLinearVelocity(0, 0); // Stop movement during stabbing
                scale = 3f;
            } else if (isUsignGun && !isShooting) {
                isShooting = true;
                //stabTime = 1.4f;
                currentState = PlayerEnum.SHOOTING;
                body.setLinearVelocity(0, 0);
                scale = 0.3f;
            }
        }

        // Handling the used weapon
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            //System.out.println("P pressed"); // Debug:
            isUsingKnife = !isUsingKnife;
            isUsignGun = !isUsignGun;
            if (isUsignGun) {
                currentFrame = walkingGun.getKeyFrame(wlkTime, true);
                playerStand = playerStandGun;
            } else {
                currentFrame = walkingAnim.getKeyFrame(wlkTime, true);
                playerStand = playerStandKnife;
            }
        }

        // Movement based on arrow keys
        if (!isStabbing && !isShooting) {
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

        // Stabbing animation
        if (isStabbing) {
            stabTime += dt;
            if (stabbing.isAnimationFinished(stabTime)) {
                isStabbing = false; // Reset stabbing state after animation finishes
                currentState = PlayerEnum.IDLE;
                stabTime = 0f;
            }
        }

        // Shooting animation
        if (isShooting) {
            shootTime += dt;
            if (shootingAnim.isAnimationFinished(shootTime)) {
                isShooting = false; // Reset shooting state after animation finishes
                currentState = PlayerEnum.IDLE;
                shootTime = 0f;
            }
        }

        //handleInput(dt);
        this.setPosition(body.getPosition().x, body.getPosition().y);
        //takeDamage();
    }

    public void render(SpriteBatch batch) {
        scale = 1.1f;
        TextureRegion currentFrame = switch (currentState) {
            case WALKING -> isUsingKnife ? walkingAnim.getKeyFrame(wlkTime, true) : walkingGun.getKeyFrame(wlkTime, true);
            case STABBING -> {
                scale = 1.45f;
                yield stabbing.getKeyFrame(stabTime, true);
            }
            case SHOOTING -> {
                scale = 0.95f;
                yield shootingAnim.getKeyFrame(shootTime, true);
            }

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


