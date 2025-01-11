package pco.project.game.zombiePack;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

public class RandomPos {
    private final World world;

    public  RandomPos(World world) {
        this.world = world;
    }

    public Vector2 randomPos(float xStart, float yStart, float xEnd, float yEnd) {
        float x = MathUtils.random(xStart, xEnd);
        float y = MathUtils.random(yStart, yEnd);

        return new Vector2(x, y);
    }

    public boolean isValidPos(Vector2 pnt) {
        final boolean[] isBlocked = {false};
        RayCastCallback rayCaster = new RayCastCallback() {

            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                isBlocked[0] = true;
                return 0;
            }
        };


        try {
            Vector2 startPoint = new Vector2(pnt);
            Vector2 endPoint = new Vector2(pnt).add(1f, 1f);
            world.rayCast(rayCaster, startPoint, endPoint);
            return !isBlocked[0];
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
