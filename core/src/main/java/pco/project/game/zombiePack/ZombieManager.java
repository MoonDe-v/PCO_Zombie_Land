package pco.project.game.zombiePack;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import lombok.Getter;
import pco.project.game.GlobalConstant;
import pco.project.game.playerPack.Player;

import java.util.HashSet;

public class ZombieManager {
    @Getter
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
        this.batch = batch;
        zombieList = new Array<>();
        createZombies(nbrDeZomb, world);
    }

    public Array<Vector2> arrayOfPos(int zombieNbr, float mapWidth, float mapHeight) {
        HashSet<Vector2> positionsSet = new HashSet<>(); // Utilisation d'un Set pour vérifier les duplications
        Array<Vector2> positionsList = new Array<>();
        RandomPos pos = new RandomPos(this.world);

        for (int i = 0; i < zombieNbr; i++) {
            Vector2 position = pos.randomPos(0f, 0f, mapWidth / 128, mapHeight / 128);

            // Générer une nouvelle position tant que celle-ci est invalide ou déjà dans le Set
            while (positionsSet.contains(position) || !pos.isValidPos(position)) {
                position = pos.randomPos(0f, 0f, mapWidth / 128, mapHeight / 128);
            }

            // Ajouter la position au Set et à la liste
            positionsSet.add(position);
            positionsList.add(position);
        }

        return positionsList;
    }


    // Use the FlyWeight pattern for zombie creation
    public void createZombies(int nbrDeZomb, World world) {
        com.badlogic.gdx.utils.Array<Vector2> tabDePosValide = arrayOfPos(nbrDeZomb, GlobalConstant.MapWidth, GlobalConstant.MapHeight);
        ZombieFlyWeight zombieFlyWeight = ZombieFlyWeight.getInstance(); // Get the shared zombie resources

        for (int i = 0; i < nbrDeZomb; i++) {
            lastAttackTime = 0f;

            // Create new zombie, using the shared resources from the FlyWeight
            Zombie zombie = new Zombie(world, player, zombieFlyWeight);
            zombie.defineZombie(tabDePosValide.get(i));
            zombie.patrolling(GlobalConstant.DELTA_TIME);

            zombieList.add(zombie);  // Add the initialized zombie to the list
        }
    }

    // Update zombies' behavior
    public void update(float dt) {
        Vector2 playerPos = player.body.getPosition();
        Vector2 zombiePos;
        for (Zombie zombie : zombieList) {
            zombie.update(dt);
            float distToPlayer = zombie.body.getPosition().dst(playerPos);
            zombiePos = zombie.body.getPosition();

            // Check if the zombie is close to the player
            if (distToPlayer < 0.4f) {
                float currentTime = TimeUtils.millis() / 1000f;
                if (!player.isDead()) {
                    if (currentTime - zombie.lastAttackTime >= GlobalConstant.ZOMBIE_ATTACK_COOLDOWN) { // 1-second cooldown
                        player.takeDamage(batch); // Attack the player
                        zombie.lastAttackTime = currentTime; // Update the last attack time
                    }
                } else {
                    callGameOver = true;
                }

                // Check if the player is attacking the zombie
                if (player.isStabbing()) {
                    zombie.zTakeDamage(1);
                }
            }
           // System.out.println(zombie.isFacingZombie(zombiePos));
            if (zombie.isFacingZombie(zombiePos)) {
                //verifying if the player is facing the zombie
                if (player.isShooting() && (distToPlayer < 1f)) {
                    System.out.println("player shooting and facing");
                    zombie.zTakeDamage(2);
                }
            }
        }
    }

    // Render zombies
    public void render(SpriteBatch batch) {
        for (Zombie zombie : zombieList) {
            zombie.render(batch);
        }
    }

    // Dispose zombies
    public void dispose() {
        for (Zombie zombie : zombieList) {
            zombie.dispose();
        }
    }
}

