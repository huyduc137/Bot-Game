package bot.navigation;

import jsclub.codefest.sdk.algorithm.PathUtils;
import jsclub.codefest.sdk.base.Node;
import sdk.Hero;


import bot.BotContext;
import bot.utils.GameUtils;
import bot.navigation.PathPlanner;

public class SafeZoneNavigator {

    Hero hero;

    public SafeZoneNavigator(Hero hero) {
        this.hero = hero;
    }

    public boolean isInSafeZone() throws Exception {
        if (!PathUtils.checkInsideSafeArea(BotContext.player, BotContext.gameMap.getSafeZone(), BotContext.gameMap.getMapSize())) {
            System.out.println("CRITICAL: Out of safe zone! Moving to safety.");
            Node centerOfMap = GameUtils.getCenterOfMap(BotContext.gameMap.getMapSize());
            // đã sửa lại đưa centerOfMap vào GameUtils
            String pathToSafety = PathUtils.getShortestPath(BotContext.gameMap, PathPlanner.getNodesToAvoid(true, true), BotContext.player, centerOfMap, false);
            // đã sửa lại đưa PathPlanner.getNodesToAvoid(true, true) vào PathPlanner
            if (pathToSafety != null && !pathToSafety.isEmpty()) {
                hero.botMove(pathToSafety);
                return true;
            }
        }
        return false;
    }
}