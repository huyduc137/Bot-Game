package bot;

import java.util.List;
import java.util.ArrayList;

import jsclub.codefest.sdk.model.Inventory;
import jsclub.codefest.sdk.model.GameMap;
import jsclub.codefest.sdk.model.players.Player;

import sdk.Hero;
import bot.memory.BotMemory;

public class BotContext {
    public static GameMap gameMap;
    public static Player player;
    public static Inventory inventory;
    public static List<Player> enemyPlayers = new ArrayList<>();
    public static int actionCount = 0;

    public static boolean updateAll(Hero hero, Object... args) {
        try {
            gameMap = hero.getGameMap();
            gameMap.updateOnUpdateMap(args[0]);
            player = gameMap.getCurrentPlayer();
            inventory = hero.getInventory();
            enemyPlayers = gameMap.getOtherPlayerInfo();

            BotMemory.actionCount++;

            if (player == null || player.getHealth() == 0) {
                System.out.println("Player is dead or data not available.");
                BotMemory.actionCount = 0;
                BotMemory.lastAction.clear();
                BotMemory.previousPositions.clear();
                return false;
            }
            BotMemory.previousPositions.add(player);
            return true;
        } catch (Exception e) {
            System.err.println("Error updating game context: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
