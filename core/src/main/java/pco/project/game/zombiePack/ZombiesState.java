//package pco.project.game.zombiePack;
//
//import com.badlogic.gdx.math.MathUtils;
//import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.physics.box2d.World;
//import com.google.errorprone.annotations.Var;
//
//public class ZombiesState {
//
//    private final World world;
//    private final float patrolArea;
//    private Vector2 patrolCenter;
//    private float detection;
//    private Vector2 targetPos, currentPos;
//    private Zombie zombie;
//    private RandomPos pos;
//
//    public ZombiesState(World world, Vector2 patrolCenter, float patrolArea) {
//        this.world = world;
//        this.patrolArea = patrolArea;
//        this.patrolCenter = patrolCenter;
//        targetPos = patrolCenter;
//        currentPos = targetPos;
//    }
//
//    public void setZombie(Zombie zombie) {
//        this.zombie = zombie;
//        this.targetPos = zombie.body.getPosition();
//        this.currentPos = targetPos;
//    }
//
//
//    public void patrolling(float deltaTime, Vector2 patrolCenter, float patrolArea) {
//
//        if (currentPos.dst(targetPos) < 0.1f) {
//            float angle = MathUtils.random(0, 360); // Random angle
//            float distance = MathUtils.random(0, patrolArea); // Random distance within radius
//
//            targetPos = pos.randomPos(patrolCenter.x, patrolCenter.y, patrolCenter.x + MathUtils.cosDeg(angle) * distance, patrolCenter.y + MathUtils.sinDeg(angle) * distance);
//            while (!pos.isValidPos(targetPos)) {
//                angle = MathUtils.random(0, 360); // Random angle
//                distance = MathUtils.random(0, patrolArea);
//                targetPos = pos.randomPos(patrolCenter.x, patrolCenter.y, patrolCenter.x + MathUtils.cosDeg(angle) * distance, patrolCenter.y + MathUtils.sinDeg(angle) * distance);
//            }
//        }
//        else { //the zombie should move toward the target position
//            Vector2 direction = new Vector2(targetPos).sub(currentPos).nor(); // Direction vector
//            float speed = 2f; // Zombie movement speed (adjust as needed)
//            Vector2 velocity = direction.scl(speed * deltaTime); // Scaled velocity
//
//            // Update of the zombie's position
//            //zombie.body.setTransform(currentPos.add(velocity), zombie.body.getAngle());
//            zombie.body.setLinearVelocity(velocity);
//            currentPos = zombie.body.getPosition();
//            float cc = 0f;
//        }
//    }
//}
