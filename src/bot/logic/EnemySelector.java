package bot.logic;

import jsclub.codefest.sdk.model.npcs.Enemy;
import jsclub.codefest.sdk.model.players.Player;
import jsclub.codefest.sdk.base.Node;
import jsclub.codefest.sdk.model.Element;
import jsclub.codefest.sdk.model.ElementType;
import jsclub.codefest.sdk.algorithm.PathUtils;
import jsclub.codefest.sdk.model.weapon.Weapon;
import jsclub.codefest.sdk.model.Inventory;

import java.io.IOException;
import java.util.List;


import bot.BotContext;
import bot.navigation.PathPlanner;

public class EnemySelector {
    private static final int MAX_DISTANCE = 500; // Maximum distance to consider an enemy

    private static boolean isPathClear(Node nodeStart, Node nodeEnd) {
        String path = PathUtils.getShortestPath(BotContext.gameMap, PathPlanner.getNodesToAvoid(false, false), nodeStart, nodeEnd, false);
        if (path == null) {
            return false;
        }
        for (int i = 1; i < path.length(); i++) {
            if (path.charAt(i) != path.charAt(i - 1)) {
                return false; // Path is not clear if there are turns
            }
        }
        return true; // Path is clear
    }

    public static Player findEnemyInAttackRange() throws IOException {

        Player bestEnemy = null;
        int maxRangeDamage = 0;

        Node player = BotContext.player;
        int maxDistance = BotContext.inventory.getGun() != null ? BotContext.inventory.getGun().getRange()[1] : 1;
        int minDistance = 1; // Minimum distance for melee attack

        Weapon gun = BotContext.inventory.getGun();
        Weapon special = BotContext.inventory.getSpecial();
        Weapon throwable = BotContext.inventory.getThrowable();

        for (Player enemy : BotContext.enemyPlayers) {
            if (enemy.getHealth() <= 0) {
                continue; // Skip dead enemies
            }
            int vx = enemy.getX() - player.getX();
            int vy = enemy.getY() - player.getY();
            if (vx != 0 && vy != 0) {
                continue;
            }

            int distance = Math.abs(vx) + Math.abs(vy);

            if (distance > maxDistance || distance < minDistance || !isPathClear(player, enemy)) {
                continue;
            }

            if (distance == 1) {
                return null; // If the enemy is adjacent, return null to avoid melee attack
            }

            if (gun != null && gun.getRange()[1] >= distance) {
                // If the player has a gun and the enemy is within range
                if (maxRangeDamage < gun.getDamage()) {
                    maxRangeDamage = gun.getDamage();
                    bestEnemy = enemy;
                }
            } 
            if (special != null && special.getRange()[1] - 1 <= distance && distance <= special.getRange()[1] + 1) {
                // If the player has a special weapon and the enemy is within range
                if (maxRangeDamage < special.getDamage()) {
                    maxRangeDamage = special.getDamage();
                    bestEnemy = enemy;
                }
            } 
            if (throwable != null && throwable.getRange()[1] >= distance) {
                // If the player has a throwable weapon and the enemy is within range
                if (maxRangeDamage < throwable.getDamage()) {
                    maxRangeDamage = throwable.getDamage();
                    bestEnemy = enemy;
                }
            }
            if (bestEnemy != null && enemy.getHealth() <= maxRangeDamage) {
                // If the enemy's health is less than or equal to the maximum damage of the weapon
                return enemy; // Return the first enemy found in attack range
            }
        }
        return bestEnemy; // Return the first enemy found in attack range
    }

    public static Player findEnemyInRange(int maxPath) throws IOException {
        Player closestEnemy = null;
        int minPath = Integer.MAX_VALUE;
        int playerX = BotContext.player.getX();
        int playerY = BotContext.player.getY();

        for (Player enemy : BotContext.enemyPlayers) {
            int x = enemy.getX();
            int y = enemy.getY();
            if (Math.abs(x - playerX) + Math.abs(y - playerY) > maxPath) {
                continue; // Skip if the enemy is out of range
            }
            String path = PathUtils.getShortestPath(BotContext.gameMap, PathPlanner.getNodesToAvoid(minPath != 1 , false), BotContext.player, enemy, false);
            if (path == null) {
                continue; // Skip if no path found
            }
            if (minPath > path.length()) {
                minPath = path.length();
                closestEnemy = enemy;
            } else if (minPath == path.length()) {
                // If the path length is the same, prefer the one with higher health
                if (closestEnemy != null && closestEnemy.getHealth() > enemy.getHealth()) {
                    closestEnemy = enemy;
                }
            }
        }
        return closestEnemy;
    }
}