package bot.navigation;

import java.util.ArrayList;
import java.util.List;

import jsclub.codefest.sdk.base.Node;
import jsclub.codefest.sdk.model.obstacles.Obstacle;
import jsclub.codefest.sdk.model.obstacles.ObstacleTag;
import jsclub.codefest.sdk.model.players.Player;
import jsclub.codefest.sdk.model.npcs.Enemy;
import jsclub.codefest.sdk.base.Node;

import bot.BotContext;

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
            for (Enemy enemy : BotContext.gameMap.getListEnemies()) {
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        restrictedNodes.add(new Node(enemy.getX() + i, enemy.getY() + j));
                    }
                }
            }
        }
        return restrictedNodes;
    }
}