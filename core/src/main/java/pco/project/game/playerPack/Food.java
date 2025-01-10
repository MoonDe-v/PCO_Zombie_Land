package pco.project.game.playerPack;

import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.utils.Timer;

public class Food {
    private final TextureMapObject textureObject;
    private boolean isAvailable;
    private float respawnTime;

    public Food(TextureMapObject textureObject, float respawnTime) {
        this.textureObject = textureObject;
        this.isAvailable = true;
        this.respawnTime = respawnTime;
    }

    public TextureMapObject getTextureObject() {
        return textureObject;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setUnavailable() {
        isAvailable = false;

        // Set a timer to make the object available again
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                isAvailable = true;
            }
        }, respawnTime);
    }
}

