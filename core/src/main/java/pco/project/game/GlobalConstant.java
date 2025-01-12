package pco.project.game;

public final class GlobalConstant {

    public static final float MapWidth = 8960f;
    public static final float MapHeight = 6400f;
    public static final float PosInitX = 5000f/128f;
    public static final float PosInitY = 4000f/128f;
    public static final float PlayerWidth = 75/128f;
    public static final float PlayerHeight = 40/128f;
    public static final float ZombieWidth = 40/128f;
    public static final float ZombieHeight = 40/128f;
    public static final short CATEGORY_PLAYER = 0x0001;   // Player's category
    public static final short CATEGORY_OBJECT = 0x0002;  // Collidable object
    public static final short CATEGORY_FOOD = 0x0004;
    public static final short CATEGORY_ZOMBIE = 0x0005;
    public static final float DELTA_TIME = 1f;
    public static final float ZOMBIE_ATTACK_COOLDOWN = 50000f;
    public static final int   PlayerCurrentHealth = 8;

    private GlobalConstant(){};
}
