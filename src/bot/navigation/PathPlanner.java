package bot.navigation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import jsclub.codefest.sdk.algorithm.PathUtils;
import jsclub.codefest.sdk.base.Node;
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

        // Thêm các vật cản không thể đi qua
        for (Obstacle obstacle : BotContext.gameMap.getListObstacles()) {
            List<ObstacleTag> tags = obstacle.getTags();
            // Sửa lại điều kiện một chút để bao gồm cả những vật cản không có tag
            // Chỉ những vật cản có tag CAN_GO_THROUGH mới được đi qua.
            if (tags == null || !tags.contains(ObstacleTag.CAN_GO_THROUGH)) {
                restrictedNodes.add(obstacle);
            }
        }

        if (avoidPlayers) {
            for (Player otherPlayer : BotContext.gameMap.getOtherPlayerInfo()) {
                // --- SỬA LỖI Ở ĐÂY ---
                // Chỉ thêm người chơi khác vào danh sách né
                // nếu họ không ở cùng vị trí với bot của mình.
                // Điều này tránh trường hợp bot tự chặn đường mình khi đứng trên xác đối thủ.
                if (otherPlayer.getX() != BotContext.player.getX() || otherPlayer.getY() != BotContext.player.getY()) {
                    restrictedNodes.add(otherPlayer);
                }
            }
        }

        if (avoidEnemies) {
            restrictedNodes.addAll(BotMemory.getAffectedNodes(false));
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
        System.out.println(perpendicularTarget.getX() + " "  + perpendicularTarget.getY());

        return pathToAlly;
    }

    private static Node findNearestAffectedNodeFromAllies() {
        return BotMemory.getAffectedNodes(true).stream()
                .min(Comparator.comparingInt(node -> PathUtils.distance(BotContext.player, node)))
                .orElse(null);
    }
}