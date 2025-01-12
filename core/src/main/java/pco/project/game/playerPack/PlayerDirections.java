package pco.project.game.playerPack;

import com.badlogic.gdx.math.Vector2;

public enum PlayerDirections{
    UP(new Vector2(0, 1)),
    DOWN(new Vector2(0, -1)),
    LEFT(new Vector2(-1, 0)),
    RIGHT(new Vector2(1, 0)),
    UP_LEFT(new Vector2(-1, 1).nor()),
    UP_RIGHT(new Vector2(1, 1).nor()),
    DOWN_LEFT(new Vector2(-1, -1).nor()),
    DOWN_RIGHT(new Vector2(1, -1).nor());

    private Vector2 direction;

    PlayerDirections(Vector2 direction) {
        this.direction = direction;
    }

    public Vector2 getDirection() {
        return direction;
    }
}
