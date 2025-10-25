package quidditch;

public enum AchievementType {
    // Basic/Progression
    WARM_UP("Complete your first game"),
    GAME_50("Play 50 Games"),
    GAME_100("Play 100 Games"),
    GAME_200("Play 200 Games"),
    GAME_300("Play 300 Games"),
    GAME_500("Play 500 Games"),
    HALF_MARATHON("Play 21 Matches in a row"),
    MARATHON("Play 42 Matches in a row"),
    DEDICATED("Play 7 days in a row"),

    // Snitch Hits/Distance/Skill
    MID_SHOT("Distance: 25 Blocks"),
    LONG_SHOT("Distance: 50 Blocks"),
    KATNISS_EVERDEEN("Distance: 70 Blocks"),
    GREAT_START("Catch the first Snitch by hand"),
    FAST_CATCH("Catch a Snitch in under 8 seconds"),
    FAST_STREAK("Make 3 fast catches"),
    STREAK_MASTER("Make 2 Fast catch Streaks in one game"),
    PERFECT_ACCURACY("Miss no shot"),
    HAND_ONLY("Only hand catches"),
    ARROW_SUPREMACY("Only bow catches"),
    SHARP_SHOOTER("5 Bow Catches without missing an arrow"),
    HAWKEYE("10 Bow Catches without missing an arrow"),
    STORMTROOPER("Miss 10 arrows"),
    HIGH_GROUND("Catch a snitch with height difference"),
    AIR_JORDAN("Hit a snitch in the air"), //player in air
    OVER_1000("Finish a game with a score over 1000"),
    OVER_1500("Finish a game with a score over 1500"),
    UNDER_60("Finish a game in under 60 seconds"),
    UNDER_30("Finish a game in under 30 seconds"),

    // Special Block/Spawn/Compass
    LUCKY_SPAWN("Snitch spawns close by"),
    SPARKLE("Let the Snitch sparkle"),
    RANDOM_MAP("Play on a random map"),
    UNECCESSARY_COMPASS("Catch the snitch shortly after you got a compass"),
    NO_COMPASS_REQUIRED("Don't use a compass"),

    // Seasonal/Event
    PUMPKIN_SEASON("Play a game during Halloween"),
    CHRISTMAS_SEASON("Play a game during Christmas"),

    // Environment/Hazards
    LAVA_SWIMMER("Die in lava"),
    EARTHBOUND("Dont jump in one game"),
    BOUNCER("Jump 50 times in one game"),
    INVINCIBLE("Dont take damage"),

    // Map/Location Specific
    MAP_SPECIALIST("Win on all maps"),
    UNLUCKY_HAUNT("Snitch spawned in the secret tunnel"),
    ALL_WATER_UNDER_THE_BRIDGE("Snitch spawned under the bridge"),
    BEHIND_THE_HOURGLASS("Snitch spawned near the hourglass"),
    CRISPY("Snitch spawned above lava"),
    MIX_UP("Snitch spawned adjacent to gold"),
    RAINBOW_HUNTER("Catch snitches on 5 differnt maps in one day"),

    // Misc/Fun/Stats Viewing
    REBEL("Try to drop an item"),
    RAVENCLAW_ZAG("Look at your stats"), //DONE
    RAVENCLAW_UTZ("Look at your map stats"); //DONE

    private String description;

    AchievementType(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }
}