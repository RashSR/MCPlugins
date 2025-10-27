package quidditch;

public enum AchievementType {
    // Basic/Progression
    WARM_UP("Complete your first game"),
    GAME_50("Play 50 Games"),
    GAME_100("Play 100 Games"),
    GAME_200("Play 200 Games"),
    GAME_350("Play 350 Games"),
    GAME_500("Play 500 Games"),
    HALF_MARATHON("Play 21 Matches in a row"),
    MARATHON("Play 42 Matches in a row"),
    DEDICATED("Play 7 days in a row"),

    // Snitch Hits/Distance/Skill
    MID_SHOT("Distance: 25 Blocks"),
    LONG_SHOT("Distance: 50 Blocks"),
    KATNISS_EVERDEEN("Distance: 70 Blocks"),
    GREAT_START("Catch the first Snitch by hand"),
    FAST_CATCH("Catch in under 8 seconds"),
    FAST_STREAK("Make 3 fast catches"),
    STREAK_MASTER("Make 2 Fast catch Streaks"),
    PERFECT_ACCURACY("Miss no shot"),
    HAND_ONLY("Only hand catches"),
    ARROW_SUPREMACY("Only bow catches"),
    SHARP_SHOOTER("5 perfect Bow Catches"),
    HAWKEYE("10 perfect Bow Catches"),
    STORMTROOPER("Miss 10 arrows"),
    HIGH_GROUND("Catch a snitch with height difference"),
    AIR_JORDAN("Hit a snitch in the air"),
    MERMAID("Hit a snitch in the water"),
    OVER_1000("Score over 1000"),
    OVER_1500("Score over 1500"),
    UNDER_60("Time under 60 seconds"),
    UNDER_30("Time under 30 seconds"),

    // Special Block/Spawn/Compass
    LUCKY_SPAWN("Snitch spawns close by"),
    SPARKLE("Let the Snitch sparkle"),
    RANDOM_MAP("Play on a random map"),
    UNECCESSARY_COMPASS("Catch the snitch shortly after you got a compass"),
    NO_COMPASS_REQUIRED("Don't use a compass"),

    // Seasonal/Event
    PRIDE_SEASON("Play during Pride Month"),
    PUMPKIN_SEASON("Play during Halloween"),
    CHRISTMAS_SEASON("Play during Christmas"),
    //TODO: GLITCH_EVENT

    // Environment/Hazards
    LAVA_SWIMMER("Die in lava"),
    EARTHBOUND("Stay one the ground!"),
    INVINCIBLE("Dont take damage"),

    // Map/Location Specific
    MAP_SPECIALIST("Win on all maps"),
    UNLUCKY_HAUNT("Snitch spawned in the secret tunnel"),
    ALL_WATER_UNDER_THE_BRIDGE("Snitch spawned under the bridge"),
    GOAL("Snitch spawned inside a ring"),
    TOWER("Snitch spawned in one Tower"),
    CRISPY("Snitch spawned above lava"),
    ON_EDGE("Snitch spawned on map border"),
    MIX_UP("Snitch spawned adjacent to gold"),
    GARDENER("Snitch spawned in a tree"),
    RAINBOW("Play 5 maps in 1 day"),

    // Misc/Fun/Stats Viewing
    REBEL("Drop an item"),
    RAVENCLAW_ZAG("Look at your stats"),
    RAVENCLAW_UTZ("Look at your map stats");

    private String description;

    AchievementType(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }
}