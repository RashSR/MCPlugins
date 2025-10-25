package quidditch;

public enum AchievementType {
    // Basic/Progression
    WARM_UP, //Complete your first game
    GAME_50, //play 50 games
    GAME_100, //play 100 games
    GAME_200, //play 200 games
    GAME_300, //play 300 games
    GAME_500, //play 500 games
    HALF_MARATHON, //play 21 Matches in a row
    MARATHON, //play 42 matches in a row
    DEDICATED, //play at least one game every day for 7 days

    // Snitch Hits/Distance/Skill
    MID_SHOT, //Hit > 25 blocks
    LONG_SHOT, //Hit > 50 blocks
    KATNISS_EVERDEEN, //Hit > 70 blocks
    GREAT_START, //Catch the first snitch by hand
    FAST_SNATCH, //Catch < 10 seconds
    FAST_STREAK, // Achieve one fast streak 
    STREAK_MASTER, // Two fast catches in one round
    PERFECT_ACCURACY, //No missed shot
    HAND_ONLY, //Only hand catches
    ARROW_SUPREMACY, //Finish a game using only bow hits (no hand catches)
    SHARP_SHOOTER, //Have at least 5 bowhits without a missing arrow
    HAWKEYE, //Have 10 bowhits without a missing arrow
    STORMTROOPER, //Miss 10 arrows
    HIGH_GROUND, //Catch a snitch at a height difference > 10 blocks
    AIR_SHOT, //Hit the snitch in the Air
    OVER_1000, //finish a game with a score of over 1000
    OVER_1500, //finish a game with a score of over 1500
    UNDER_60, //finish a game in under 60 seconds
    UNDER_30, //finish a game in under 30 seconds

    // Special Block/Spawn/Compass
    LUCKY_SPAWN, //Snitch spawns in range of the player
    SPARKLE, //don't find a block until it sparkles
    RANDOM_MAP, //play a game on a random map
    UNECCESSARY_COMPASS, //find block up to three seconds after compass
    NO_COMPASS_REQUIRED, //Don't get a compass

    // Seasonal/Event
    PUMPKIN_SEASON, //play a game during Halloween
    CHRISTMAS_SEASON, //play a game during Christmas

    // Environment/Hazards
    LAVA_SWIMMER, //Die in lava
    EARTHBOUND, //don't jump in a game
    BOUNCER, //Jump 50 times in a game
    INVINCIBLE, //don't lose health in the game

    // Map/Location Specific
    MAP_SPECIALIST, //Win all maps
    UNLUCKY_HAUNT, //Snitch is in the Shrieking Shacks secret passage
    ALL_WATER_UNDER_THE_BRIDGE, //Snitch spawns under the bridge in the SnowMap
    BEHIND_THE_HOURGLASS, //Snitch spawn near a hourglass
    RAINBOW_HUNTER, //Catch snitches on 5 different maps in one day

    // Misc/Fun/Stats Viewing
    REBEL, //Try to drop an item
    RAVENCLAW_ZAG, //Look at your stats
    RAVENCLAW_UTZ, //Look at your individual map stats
}
