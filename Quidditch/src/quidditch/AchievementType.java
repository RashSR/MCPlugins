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
    MID_SHOT("Distance: 25 Blocks"), //DONE
    LONG_SHOT("Distance: 50 Blocks"), //DONE
    KATNISS_EVERDEEN("Distance: 70 Blocks"), //DONE
    GREAT_START("Catch the first Snitch by hand"),
    FAST_CATCH("Catch in under 8 seconds"), //DONE
    FAST_STREAK("Make 3 fast catches"), //DONE
    STREAK_MASTER("Make 2 Fast catch Streaks"), //DONE
    PERFECT_ACCURACY("Miss no shot"), //DONE
    HAND_ONLY("Only hand catches"), //DONE
    ARROW_SUPREMACY("Only bow catches"), //DONE
    SHARP_SHOOTER("5 perfect Bow Catches"), //DONE
    HAWKEYE("10 perfect Bow Catches"), //DONE
    STORMTROOPER("Miss 10 arrows"), //DONE
    HIGH_GROUND("Catch a snitch with height difference"),
    AIR_JORDAN("Hit a snitch in the air"), //player in air
    OVER_1000("Score over 1000"), //DONE
    OVER_1500("Score over 1500"), //DONE
    UNDER_60("Time under 60 seconds"), //DONE
    UNDER_30("Time under 30 seconds"), //DONE

    // Special Block/Spawn/Compass
    LUCKY_SPAWN("Snitch spawns close by"),
    SPARKLE("Let the Snitch sparkle"),
    RANDOM_MAP("Play on a random map"), //DONE
    UNECCESSARY_COMPASS("Catch the snitch shortly after you got a compass"), //DONE
    NO_COMPASS_REQUIRED("Don't use a compass"), //DONE

    // Seasonal/Event
    PUMPKIN_SEASON("Play during Halloween"), //DONE
    CHRISTMAS_SEASON("Play during Christmas"), //DONE

    // Environment/Hazards
    LAVA_SWIMMER("Die in lava"), //DONE
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
    REBEL("Try to drop an item"), //DONE
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