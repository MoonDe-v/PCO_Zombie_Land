package pco.project.game.zombiePack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class ZombieFlyWeight {
    private static ZombieFlyWeight instance;
    private TextureAtlas atlas;
    private Animation<TextureRegion> walkingAnim, attackingAnim, deadAnim;
    private Sound zombieGroan;

    private ZombieFlyWeight() {
        // Load shared resources here
        atlas = new TextureAtlas("map/Atls_zombie/Zombie_1.atlas");
        zombieGroan = Gdx.audio.newSound(Gdx.files.internal("Zsounds/zombie_groan.ogg"));
        // Walking animation
        Array<TextureRegion> walking = new Array<>();
        for (int i = 1; i < 7; i++) {
            walking.add(atlas.findRegion("walk", i));
        }
        walkingAnim = new Animation<>(0.2f, walking);
        walkingAnim.setPlayMode(Animation.PlayMode.LOOP);

        // Attacking animation
        Array<TextureRegion> attacking = new Array<>();
        for (int i = 1; i < 7; i++) {
            attacking.add(atlas.findRegion("attack", i));
        }
        attackingAnim = new Animation<>(0.2f, attacking);
        attackingAnim.setPlayMode(Animation.PlayMode.LOOP);

        // Dead animation
        Array<TextureRegion> dead = new Array<>();
        for (int i = 1; i < 5; i++) {
            dead.add(atlas.findRegion("dead", i));
        }
        deadAnim = new Animation<>(0.2f, dead);
        deadAnim.setPlayMode(Animation.PlayMode.NORMAL);
    }

    public static ZombieFlyWeight getInstance() {
        if (instance == null) {
            instance = new ZombieFlyWeight();
        }
        return instance;
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public Animation<TextureRegion> getWalkingAnim() {
        return walkingAnim;
    }

    public Animation<TextureRegion> getAttackingAnim() {
        return attackingAnim;
    }

    public Animation<TextureRegion> getDeadAnim() {
        return deadAnim;
    }

    public void dispose() {
        atlas.dispose();
    }
}
