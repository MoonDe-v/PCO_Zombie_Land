//package pco.project.game.zombiePack;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.graphics.g2d.TextureAtlas;
//import com.badlogic.gdx.graphics.g2d.TextureRegion;
//import com.badlogic.gdx.math.MathUtils;
//import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.physics.box2d.*;
//import com.badlogic.gdx.graphics.g2d.Sprite;
//import com.badlogic.gdx.utils.Array;
//import com.badlogic.gdx.graphics.g2d.Animation;
//import com.badlogic.gdx.utils.TimeUtils;
//import pco.project.game.ConstantVar;
//import pco.project.game.playerPack.Player;
//import com.badlogic.gdx.audio.Sound;
//
//
//public class Zombie extends Sprite {
//
//    private Sprite zombieSprite;
//    public World world;
//    public Body body;
//    protected TextureAtlas atlas;
//    private TextureRegion zombieStand;
//    private Animation<TextureRegion> walkingAnim, attackingAnim, deadAnim;
//    private float wlkTime;
//    private TextureRegion currentFrame;
//    private float rotation;
//    private Vector2 targetPos, patrolCenter; // Store the target position for patrolling
//    private float patrolAreaRadius = 5f; // Radius for patrolling around the center
//    private ZombieState zmbStateEnum;
//    Vector2 playerPos;
//    Player player;
//    float attackRange = 2f, chaseRange = 3f;
//    float lastAttackTime = 0f, speed = 2f; // Zombie movement speed
//    int  zombieHealth;
//    private int score;
//    private Sound zombieGroan;
//
//    public Zombie(World world, Player player) {
//        this.world = world;
//        this.player = player;
//        zmbStateEnum = ZombieState.PATROLLING;
//        atlas = new TextureAtlas("map/Atls_zombie/Zombie_1.atlas");
//        zombieStand = new TextureRegion(atlas.findRegion("walk", 1));
//        zombieSprite = new Sprite(zombieStand);
//        currentFrame = zombieStand;
//        wlkTime = 0f;
//        playerPos = player.body.getPosition();
//        zombieHealth = 2; //each zombie die within 2 stabs
//
//        this.setBounds(0, 0, ConstantVar.ZombieWidth, ConstantVar.ZombieHeight);
//        initAnim();
//
//        //init of the sound
//        //zombieGroan = Gdx.audio.newSound(Gdx.files.internal("Zsounds/zombie_groan.ogg"));
//        int a = 1;
//    }
//
//    public void defineZombie(Vector2 position) {
//        BodyDef bdef = new BodyDef();
//        bdef.position.set(position.x, position.y);
//        bdef.type = BodyDef.BodyType.DynamicBody;
//        body = world.createBody(bdef);
//
//        FixtureDef fdef = new FixtureDef();
//        CircleShape shape = new CircleShape();
//        //shape.setAsBox((ConstantVar.ZombieWidth - 0.1f) / 2f, (ConstantVar.ZombieHeight + 0.03f) / 2f);
//        shape.setRadius(0.1f);
//        fdef.shape = shape;
//        fdef.isSensor = false;
//        fdef.filter.categoryBits = ConstantVar.CATEGORY_ZOMBIE; // Category for zombie
//        fdef.filter.maskBits = ConstantVar.CATEGORY_OBJECT | ConstantVar.CATEGORY_PLAYER;
//
//        body.createFixture(fdef);
//        body.setAngularDamping(10f);
//
//        shape.dispose();
//        zombieSprite.setSize(ConstantVar.ZombieWidth, ConstantVar.ZombieHeight);
//
//        // Set an initial random target position within the patrol area
//        patrolCenter = position;
//        targetPos = generateRandomTarget(patrolCenter, patrolAreaRadius);
//    }
//
//    public void initAnim() {
//        //walking with a knife anime
//        Array<TextureRegion> walking = new Array<>();
//        for (int i = 1; i < 7; i++) {
//            walking.add(atlas.findRegion("walk", i));
//        }
//        walkingAnim = new Animation<>(0.2f, walking);
//        walkingAnim.setPlayMode(Animation.PlayMode.LOOP);
//
//        //stabbing with a knif anim
//        Array<TextureRegion> attacking = new Array<TextureRegion>();
//
//        for (int i = 1; i < 7; i++) { // 19: thz size of regions of the player
//            attacking.add(atlas.findRegion("attack", i));
//        }
//        attackingAnim = new Animation<>(0.2f, attacking);
//        attackingAnim.setPlayMode(Animation.PlayMode.LOOP);
//
//        //Walking with shootgun anim
//        Array<TextureRegion> dead = new Array<TextureRegion>();
//
//        for (int i = 1; i < 5; i++) { // 19: thz size of regions of the player
//            dead.add(atlas.findRegion("dead", i));
//        }
//        deadAnim = new Animation<>(0.2f, dead);
//        deadAnim.setPlayMode(Animation.PlayMode.NORMAL);
//    }
//
//    public void zTakeDamage() {
//        zombieHealth -= 1;
//        if (zombieHealth <= 0) {
//            die();
//        }
//    }
//
//    public void die() {
//        zmbStateEnum = ZombieState.DEAD;
//        currentFrame = deadAnim.getKeyFrame(wlkTime, true);
//        body.setActive(false);
//    }
//
//    public void patrolling(float deltaTime) {
//        Vector2 currentPos = body.getPosition();
//        RandomPos pos = new RandomPos(world);
//        //targetPos = generateRandomTarget(patrolCenter, patrolAreaRadius);
//
//        float distanceToTarget = currentPos.dst(targetPos);
//        if (!pos.isValidPos(targetPos) || currentPos.dst(targetPos) < 0.1f || distanceToTarget > 5f) {
//
//            targetPos = generateRandomTarget(patrolCenter, patrolAreaRadius);
//        }
//        else { //the zombie should move toward the target position
//            Vector2 direction = targetPos.cpy().sub(currentPos).nor(); // Direction vector
//
//            Vector2 velocity = direction.scl(speed); // Scaled velocity
//
//            // Update of the zombie's position
//            //zombie.body.setTransform(currentPos.add(velocity), zombie.body.getAngle());
//            body.setLinearVelocity(velocity);
//
//        }
//    }
//
//    private void attackPlayer(Vector2 playerPos) {
//        speed = 3f; // Zombie movement speed
//        Vector2 currentPos = body.getPosition();
//        Vector2 direction = playerPos.cpy().sub(currentPos).nor(); // Direction to player
//        float attackSpeed = 2f; // Speed when chasing player
//        Vector2 velocity = direction.scl(attackSpeed);
//
//        body.setLinearVelocity(velocity); // Move toward the player
//        currentFrame = attackingAnim.getKeyFrame(wlkTime, true); // Set attack animation
//
//    }
//
//    private void returnToPatrolCenter() {
//        speed = 2f;
//        Vector2 currentPos = body.getPosition();
//        Vector2 direction = patrolCenter.cpy().sub(currentPos).nor(); // Direction to patrol center
//        float returnSpeed = 1f; // Speed when returning
//        Vector2 velocity = direction.scl(returnSpeed);
//
//        body.setLinearVelocity(velocity); // Move back to patrol center
//    }
//
//    private Vector2 generateRandomTarget(Vector2 patrolCenter, float patrolAreaRadius) {
////        Vector2 targetPos;
////        float angle;
////        float distance;
////
////        do {
////            angle = MathUtils.random(0, 360); // Generate a random angle
////            distance = MathUtils.random(0, patrolAreaRadius); // Generate a random distance within radius
////
////            // Calculate the random position using polar coordinates
////            targetPos = pos.randomPos(
////                patrolCenter.x,
////                patrolCenter.y,
////                patrolCenter.x + MathUtils.cosDeg(angle) * distance,
////                patrolCenter.y + MathUtils.sinDeg(angle) * distance
////            );
////        } while (!pos.isValidPos(targetPos)); // Repeat until a valid position is found
////
////        return targetPos; // Return the valid target position
//        float angle = MathUtils.random(0, 360); // Random angle
//        float distance = MathUtils.random(0, patrolAreaRadius); // Random distance within radius
//        float x = patrolCenter.x + MathUtils.cosDeg(angle) * distance;
//        float y = patrolCenter.y + MathUtils.sinDeg(angle) * distance;
//        return new Vector2(x, y);
//    }
//
//    private int getScore() {
//        return getScore();
//    }
//
//    //handling sounds
////    public void playGroan() {
////        zombieGroan.play(1f); //1f the volume
////    }
//
//    public void render(SpriteBatch batch) {
//        Vector2 velocity = body.getLinearVelocity();
//        if (velocity.len2() > 0) { // to check if he is moving
//            rotation = velocity.angleDeg();
//        }
//        try {
//            batch.draw(currentFrame, body.getPosition().x - zombieSprite.getWidth() / 2f,
//                body.getPosition().y - zombieSprite.getHeight() / 2f,
//                zombieSprite.getWidth()/2f, zombieSprite.getHeight()/2f,
//                zombieSprite.getWidth(), zombieSprite.getHeight(),
//                1.3f, 1.3f, rotation);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        // player.healthRender(batch);
//    }
//
//    public void update(float dt) {
//        wlkTime += dt;
//        float distToPlayer = body.getPosition().dst(playerPos);
//
//        //handling the sound
////        if (distToPlayer < 0.5f) {
////            playGroan();
////        }
//        if (zmbStateEnum == ZombieState.DEAD) {
//            // No updates for dead zombie
//            currentFrame = deadAnim.getKeyFrame(wlkTime, false);
//            score += 1;
//        }
//        else {
//            currentFrame = walkingAnim.getKeyFrame(wlkTime, true);
//            switch (zmbStateEnum) {
//                case PATROLLING:
//                    if (distToPlayer <= attackRange) {
//                        zmbStateEnum = ZombieState.CHASING;
//                    } else {
//                        patrolling(dt);
//                        currentFrame = walkingAnim.getKeyFrame(wlkTime, true);
//                    }
//                    break;
//                case CHASING:
//                    if (distToPlayer > chaseRange || player.isDead()) {
//                        zmbStateEnum = ZombieState.RETURNING;
//                    } else {
//                            attackPlayer(playerPos);
//                            System.out.println("zombie attacks the player");
////                        if (body.getPosition().dst(player.body.getPosition()) < 0.1f) {
////                            player.takeDamage(batch);
////                        }
//                    }
//                    break;
//                case RETURNING:
//                    float distToPatrolCenter = body.getPosition().dst(patrolCenter);
//                    if (distToPatrolCenter <= 0.1f) {
//                        zmbStateEnum = ZombieState.PATROLLING;
//                    } else {
//                        returnToPatrolCenter();
//                    }
//                    if (distToPlayer <= attackRange) {
//                        zmbStateEnum = ZombieState.CHASING;
//                    }
//                    break;
//                default:
//                    currentFrame = zombieStand;
//                    break;
//
//            }
//        }
//    }
//
//    public void dispose() {
//        atlas.dispose();
//    }
//}



package pco.project.game.zombiePack;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import pco.project.game.GlobalConstant;
import pco.project.game.playerPack.Player;

public class Zombie extends Sprite {

    private static ZombieFlyWeight zombieFlyWeight;
    private Sprite zombieSprite;
    public World world;
    public Body body;
    private TextureRegion zombieStand;
    private float wlkTime;
    private TextureRegion currentFrame;
    private float rotation;
    private Vector2 targetPos, patrolCenter; // Store the target position for patrolling
    private final float patrolAreaRadius = 5f; // Radius for patrolling around the center
    private ZombieState zmbStateEnum;
    Vector2 playerPos;
    Player player;
    float attackRange = 2f, chaseRange = 3f;
    float lastAttackTime = 0f, speed = 2f; // Zombie movement speed
    int  zombieHealth;
    private int score;


    public Zombie(World world, Player player, ZombieFlyWeight zombieFlyWeight) {
        this.world = world;
        this.player = player;
        zmbStateEnum = ZombieState.PATROLLING;

        this.zombieFlyWeight = ZombieFlyWeight.getInstance();
        this.zombieSprite = new Sprite(zombieFlyWeight.getAtlas().findRegion("walk", 1));
        currentFrame = zombieFlyWeight.getWalkingAnim().getKeyFrame(wlkTime, true);
        wlkTime = 0f;
        playerPos = player.body.getPosition();
        zombieHealth = 2; //each zombie die within 2 stabs

        this.setBounds(0, 0, GlobalConstant.ZombieWidth, GlobalConstant.ZombieHeight);
       // initAnim();

        //init of the sound
        //zombieGroan = Gdx.audio.newSound(Gdx.files.internal("Zsounds/zombie_groan.ogg"));
        int a = 1;
    }

    public void defineZombie(Vector2 position) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(position.x, position.y);
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        //shape.setAsBox((ConstantVar.ZombieWidth - 0.1f) / 2f, (ConstantVar.ZombieHeight + 0.03f) / 2f);
        shape.setRadius(0.1f);
        fdef.shape = shape;
        fdef.isSensor = false;
        fdef.filter.categoryBits = GlobalConstant.CATEGORY_ZOMBIE; // Category for zombie
        fdef.filter.maskBits = GlobalConstant.CATEGORY_OBJECT | GlobalConstant.CATEGORY_PLAYER;

        body.createFixture(fdef);
        body.setAngularDamping(10f);

        shape.dispose();
        zombieSprite.setSize(GlobalConstant.ZombieWidth, GlobalConstant.ZombieHeight);

        // Set an initial random target position within the patrol area
        patrolCenter = position;
        targetPos = generateRandomTarget(patrolCenter, patrolAreaRadius);
    }

    public void die() {
        zmbStateEnum = ZombieState.DEAD;
        currentFrame = zombieFlyWeight.getDeadAnim().getKeyFrame(wlkTime, true);
        body.setActive(false);
    }

    public void patrolling(float deltaTime) {
        Vector2 currentPos = body.getPosition();
        RandomPos pos = new RandomPos(world);
        //targetPos = generateRandomTarget(patrolCenter, patrolAreaRadius);

        float distanceToTarget = currentPos.dst(targetPos);
        if (!pos.isValidPos(targetPos) || currentPos.dst(targetPos) < 0.1f || distanceToTarget > 5f) {

            targetPos = generateRandomTarget(patrolCenter, patrolAreaRadius);
        }
        else { //the zombie should move toward the target position
            Vector2 direction = targetPos.cpy().sub(currentPos).nor(); // Direction vector

            Vector2 velocity = direction.scl(speed); // Scaled velocity

            // Update of the zombie's position
            //zombie.body.setTransform(currentPos.add(velocity), zombie.body.getAngle());
            body.setLinearVelocity(velocity);

        }
    }

    public void zTakeDamage() {
        zombieHealth -= 1;
        if (zombieHealth <= 0) {
            die();
        }
    }

    private void attackPlayer(Vector2 playerPos) {
        speed = 3f; // Zombie movement speed
        Vector2 currentPos = body.getPosition();
        Vector2 direction = playerPos.cpy().sub(currentPos).nor(); // Direction to player
        float attackSpeed = 2f; // Speed when chasing player
        Vector2 velocity = direction.scl(attackSpeed);

        body.setLinearVelocity(velocity); // Move toward the player
        currentFrame = zombieFlyWeight.getAttackingAnim().getKeyFrame(wlkTime, true); // Set attack animation

    }

    private void returnToPatrolCenter() {
        speed = 2f;
        Vector2 currentPos = body.getPosition();
        Vector2 direction = patrolCenter.cpy().sub(currentPos).nor(); // Direction to patrol center
        float returnSpeed = 1f; // Speed when returning
        Vector2 velocity = direction.scl(returnSpeed);

        body.setLinearVelocity(velocity); // Move back to patrol center
    }

    private Vector2 generateRandomTarget(Vector2 patrolCenter, float patrolAreaRadius) {
//        Vector2 targetPos;
//        float angle;
//        float distance;
//
//        do {
//            angle = MathUtils.random(0, 360); // Generate a random angle
//            distance = MathUtils.random(0, patrolAreaRadius); // Generate a random distance within radius
//
//            // Calculate the random position using polar coordinates
//            targetPos = pos.randomPos(
//                patrolCenter.x,
//                patrolCenter.y,
//                patrolCenter.x + MathUtils.cosDeg(angle) * distance,
//                patrolCenter.y + MathUtils.sinDeg(angle) * distance
//            );
//        } while (!pos.isValidPos(targetPos)); // Repeat until a valid position is found
//
//        return targetPos; // Return the valid target position
        float angle = MathUtils.random(0, 360); // Random angle
        float distance = MathUtils.random(0, patrolAreaRadius); // Random distance within radius
        float x = patrolCenter.x + MathUtils.cosDeg(angle) * distance;
        float y = patrolCenter.y + MathUtils.sinDeg(angle) * distance;
        return new Vector2(x, y);
    }

    private int getScore() {
        return getScore();
    }

    //handling sounds
//    public void playGroan() {
//        zombieGroan.play(1f); //1f the volume
//    }

    public void render(SpriteBatch batch) {
        Vector2 velocity = body.getLinearVelocity();
        if (velocity.len2() > 0) { // to check if he is moving
            rotation = velocity.angleDeg();
        }
        try {
            batch.draw(currentFrame, body.getPosition().x - zombieSprite.getWidth() / 2f,
                body.getPosition().y - zombieSprite.getHeight() / 2f,
                zombieSprite.getWidth()/2f, zombieSprite.getHeight()/2f,
                zombieSprite.getWidth(), zombieSprite.getHeight(),
                1.3f, 1.3f, rotation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // player.healthRender(batch);
    }

    public void update(float dt) {
        wlkTime += dt;
        float distToPlayer = body.getPosition().dst(playerPos);

        //handling the sound
//        if (distToPlayer < 0.5f) {
//            playGroan();
//        }
        if (zmbStateEnum == ZombieState.DEAD) {
            // No updates for dead zombie
            currentFrame = zombieFlyWeight.getDeadAnim().getKeyFrame(wlkTime, false);
            score += 1;
        }
        else {
            currentFrame = zombieFlyWeight.getWalkingAnim().getKeyFrame(wlkTime, true);
            switch (zmbStateEnum) {
                case PATROLLING:
                    if (distToPlayer <= attackRange) {
                        zmbStateEnum = ZombieState.CHASING;
                    } else {
                        patrolling(dt);
                        currentFrame = zombieFlyWeight.getWalkingAnim().getKeyFrame(wlkTime, true);
                    }
                    break;
                case CHASING:
                    if (distToPlayer > chaseRange || player.isDead()) {
                        zmbStateEnum = ZombieState.RETURNING;
                    } else {
                        // System.out.println("zombie attacks the player"); // DEBUG
                        attackPlayer(playerPos);
                        zombieFlyWeight.getZombieGroan().play(0.5f);
//                        if (body.getPosition().dst(player.body.getPosition()) < 0.1f) {
//                            player.takeDamage(batch);
//                        }
                    }
                    break;
                case RETURNING:
                    float distToPatrolCenter = body.getPosition().dst(patrolCenter);
                    if (distToPatrolCenter <= 0.1f) {
                        zmbStateEnum = ZombieState.PATROLLING;
                    } else {
                        returnToPatrolCenter();
                    }
                    if (distToPlayer <= attackRange) {
                        zmbStateEnum = ZombieState.CHASING;
                    }
                    break;
                default:
                    currentFrame = zombieStand;
                    break;

            }
        }
    }

    public void dispose() {
        zombieFlyWeight.dispose();
    }
}
