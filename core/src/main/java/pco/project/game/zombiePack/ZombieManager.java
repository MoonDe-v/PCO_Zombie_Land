package pco.project.game.zombiePack;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import lombok.Getter;
import pco.project.game.ConstantVar;
import pco.project.game.playerPack.Player;

public class ZombieManager {
    private Array<Zombie> zombieList;
    private World world;
    private SpriteBatch batch;
    private Player player;
    @Getter
    private boolean callGameOver = false;
    private float lastAttackTime = 0f;

    public ZombieManager(World world, int nbrDeZomb, Player player, SpriteBatch batch) {
        this.player = player;
        this.world = world;
        zombieList = new Array<>();
        createZombies(nbrDeZomb, world);

    }

    public Array<Vector2> arrayOfPos(int zombieNbr, float mapWidth, float mapHeight) { //a methode tho have an array with different position within the map and not colliding with the objects
        Array<Vector2> tab = new com.badlogic.gdx.utils.Array<>();
        RandomPos pos = new RandomPos(this.world);
        for (int i = 0; i < zombieNbr; i++) {
            Vector2 position = pos.randomPos(0f,0f,ConstantVar.MapWidth/128, ConstantVar.MapHeight/128);

            while (tab.contains(position, false) || !pos.isValidPos(position)) {
                position = pos.randomPos(0f,0f,ConstantVar.MapWidth/128, ConstantVar.MapHeight/128);
            }

            tab.add(position);
        }
        return tab;
    }

    public void createZombies(int nbrDeZomb, World world) {
        com.badlogic.gdx.utils.Array<Vector2> tabDePosValide = arrayOfPos(nbrDeZomb, ConstantVar.MapWidth, ConstantVar.MapHeight);
        for (int i = 0; i < nbrDeZomb; i++) {
            lastAttackTime = 0f;
            Zombie zombie = new Zombie(world, player);// Create a new zombie instance
            zombie.defineZombie(tabDePosValide.get(i));
            zombie.patrolling(ConstantVar.DELTA_TIME);

            //ZombiesState zmbState = new ZombiesState(world, tabDePosValide.get(i), 10f);// Define its body and position
            zombieList.add(zombie);  // Add the initialized zombie to the list

        }

    }

//    public void update(float dt) {
//        for (Zombie zombie : zombieList) {
//            zombie.update(dt);
//
//            if(zombie.body.getPosition().dst(player.body.getPosition()) < 0.5f) {
//
//                System.out.println("here");
//                //player.takeDamage(batch);
//                if (player.getIsStabbing() /* && player.body.getLinearVelocity().dot(zombie.body.getPosition().sub(player.body.getPosition())) > 0*/) {
//                    zombie.zTakeDamage();
//                    System.out.println("i am here");
//                }
//            }
//        }
//    }

    public void update(float dt) {
        for (Zombie zombie : zombieList) {
            zombie.update(dt);

            // Check if the zombie is close to the player
            if (zombie.body.getPosition().dst(player.body.getPosition()) < 0.4f) {
                //System.out.println("Zombie is attacking the player");
                float currentTime = TimeUtils.millis() / 1000f;
                // Apply damage to the player
                if (!player.isDead()) {
                    if (currentTime - zombie.lastAttackTime >= 0.5f) { // 1-second cooldown
                        player.takeDamage(batch); // Attack the player
                        zombie.lastAttackTime = currentTime; // Update the last attack time
                        float a = 0f;
                    }
                }
                else {
                    callGameOver = true;
                }

                // Check if the player is attacking the zombie
                if (player.isStabbing()) {
                    System.out.println("Player is stabbing the zombie");
                    zombie.zTakeDamage();
                }
            }
        }
    }


    public void render(SpriteBatch batch) {
        for (Zombie zombie : zombieList) {
            zombie.render(batch);
        }
       // player.healthRender(batch);
    }

    public void dispose() {
        for (Zombie zombie : zombieList) {
            zombie.dispose();
        }
    }

//    public Array<Zombie> getZombieList() {
//        return zombieList;
//    }


}

