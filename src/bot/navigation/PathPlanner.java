package bot.navigation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import jsclub.codefest.sdk.algorithm.PathUtils;
import jsclub.codefest.sdk.base.Node;
import jsclub.codefest.sdk.model.Element;
import jsclub.codefest.sdk.model.ElementType;
import jsclub.codefest.sdk.model.obstacles.Obstacle;
import jsclub.codefest.sdk.model.obstacles.ObstacleTag;
import jsclub.codefest.sdk.model.players.Player;
import jsclub.codefest.sdk.model.npcs.Enemy;
import jsclub.codefest.sdk.base.Node;

import bot.BotContext;
import bot.memory.BotMemory;

public class PathPlanner {
    public static List<Node> getNodesToAvoid(boolean avoidPlayers, boolean avoidEnemies) {
        List<Node> restrictedNodes = new ArrayList<>();
        List<Obstacle> allObstacles = BotContext.gameMap.getListObstacles();
        for (Obstacle obstacle : allObstacles) {
            if (obstacle.getType() == ElementType.TRAP) {
                restrictedNodes.add(obstacle);
                continue;
            }

            List<ObstacleTag> tags = obstacle.getTags();
            if (tags == null || !tags.contains(ObstacleTag.CAN_GO_THROUGH)) {
                restrictedNodes.add(obstacle);
            }
        }

        if (avoidPlayers) {
            for (Player otherPlayer : BotContext.gameMap.getOtherPlayerInfo()) {
                if (otherPlayer.getX() != BotContext.player.getX() || otherPlayer.getY() != BotContext.player.getY()) {
                    restrictedNodes.add(otherPlayer);
                }
            }
        }

        if (avoidEnemies) {
            restrictedNodes.addAll(BotMemory.getAffectedNodes());
        }

        return restrictedNodes;
    }

    public static String getPathToAlly() {
        Node spiritAlly = findNearestAffectedNodeFromAllies();

        if (spiritAlly == null || !PathUtils.checkInsideSafeArea(
                spiritAlly,
                BotContext.gameMap.getSafeZone(),
                BotContext.gameMap.getMapSize())) {
            return null;
        }

        String pathToAlly = PathUtils.getShortestPath(
                BotContext.gameMap,
                PathPlanner.getNodesToAvoid(true, true),
                BotContext.player,
                spiritAlly,
                false
        );

        if (pathToAlly == null) return null;

        Node[] segment = BotMemory.getNpcPathSegment(spiritAlly);
        if (segment == null) return pathToAlly;

        Node player = BotContext.player;
        Node p1 = segment[0];
        Node p2 = segment[1];

        Node perpendicularTarget = null;

        // Nếu đoạn thẳng đứng (x cố định)
        if (p1.getX() == p2.getX()) {
            int x = p1.getX();
            int py = player.getY();
            if (py < p1.getY()) {
                perpendicularTarget = new Node(x, p1.getY());
            } else if (py > p2.getY()) {
                perpendicularTarget = new Node(x, p2.getY());
            } else {
                perpendicularTarget = new Node(x, py);
            }
        }

        // Nếu đoạn nằm ngang (y cố định)
        else if (p1.getY() == p2.getY()) {
            int y = p1.getY();
            int px = player.getX();
            if (px < p1.getX()) {
                perpendicularTarget = new Node(p1.getX(), y);
            } else if (px > p2.getX()) {
                perpendicularTarget = new Node(p2.getX(), y);
            } else {
                perpendicularTarget = new Node(px, y);
            }
        }

        if (perpendicularTarget != null) {
            String perpendicularPath = PathUtils.getShortestPath(
                    BotContext.gameMap,
                    PathPlanner.getNodesToAvoid(true, true),
                    player,
                    perpendicularTarget,
                    false
            );
            if (perpendicularPath != null) {
                return perpendicularPath;
            }
        }

        return pathToAlly;
    }

    private static Node findNearestAffectedNodeFromAllies() {
        return BotMemory.getAffectedNodes().stream()
                .min(Comparator.comparingInt(node -> PathUtils.distance(BotContext.player, node)))
                .orElse(null);
    }
    public static boolean isCellSafeForZigzag(Node cell) {
        int mapSize = BotContext.gameMap.getMapSize();
        if (cell.getX() < 0 || cell.getX() >= mapSize || cell.getY() < 0 || cell.getY() >= mapSize) {
            return false;
        }

        Element element = BotContext.gameMap.getElementByIndex(cell.getX(), cell.getY());

        if (element instanceof Obstacle) {
            Obstacle obstacle = (Obstacle) element;
            if (obstacle.getType() == ElementType.TRAP) {
                return false;
            }
            if (obstacle.getTags() != null && !obstacle.getTags().contains(ObstacleTag.CAN_GO_THROUGH)) {
                return false;
            }
        }

        return true;
    }
}