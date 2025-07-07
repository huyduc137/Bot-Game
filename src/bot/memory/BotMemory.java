package bot.memory;

import bot.utils.GameUtils;
import jsclub.codefest.sdk.base.Node;
import jsclub.codefest.sdk.model.GameMap;
import jsclub.codefest.sdk.model.players.Player;

import java.util.List;
import java.util.ArrayList;

import sdk.HeroActionType;

public class BotMemory {
    public static final Node NULL_NODE = new Node(-1, -1);

    private static final List<GameMap> recentGameMaps = new ArrayList<> ();
    public static List<Node> previousPositions = new ArrayList<>();
    public static List<HeroActionType> recentActions = new ArrayList<>();
    public static int actionCount = 0;
    public static List<Node[]> trackedNpcSegments = new ArrayList<>();

    public static void update(GameMap gameMap) {
        recentGameMaps.add(gameMap);
        if (recentGameMaps.size() > 10) {
            recentGameMaps.remove(0); // Xoá phần tử đầu tiên
        }

        for (Node npc : gameMap.getListAllies()) {
            update(npc);
        }

        for (Node npc : gameMap.getListEnemies()) {
            update(npc);
        }

        actionCount++;
        Player player = gameMap.getCurrentPlayer();
        if (player == null || player.getHealth() == 0) {
            previousPositions.add(NULL_NODE);
            recentActions.add(HeroActionType.DEATH);
        }
    }

    public static HeroActionType checkRepeatedActions() {
        if (recentActions.size() < 5 || previousPositions.size() < 5) {
            return null;
        }

        HeroActionType last = recentActions.get(recentActions.size() - 1);

        for (int i = 2; i <= 5; i++) {
            if (last != recentActions.get(recentActions.size() - i)) {
                return null; 
            }
        }

        if (last == HeroActionType.MOVE) {
            for (int i = 0; i < 3; i++) {
                Node pos1 = previousPositions.get(previousPositions.size() - 1 - i);
                Node pos2 = previousPositions.get(previousPositions.size() - 3 - i);

                if (pos1.getX() == pos2.getX() && pos1.getY() == pos2.getY()) {
                    return last; 
                }
            }
            return null;
        }

        return last;
    }

    private static void update(Node currentNpc) {
        boolean updated = false;

        for (Node[] npcPair : trackedNpcSegments) {
            Node endpoint1 = npcPair[0];
            Node endpoint2 = npcPair[1];

            Node vecToEnd1 = GameUtils.vectorBetween(currentNpc, endpoint1);
            Node vecToEnd2 = GameUtils.vectorBetween(currentNpc, endpoint2);

            // Kiểm tra cùng phương
            boolean sameLine = (vecToEnd1.getX() * vecToEnd2.getY()) == (vecToEnd1.getY() * vecToEnd2.getX());

            if (!sameLine) continue;

            // Kiểm tra nếu currentNpc nằm giữa a và b trên cùng đoạn thẳng
            int dot = vecToEnd1.getX() * vecToEnd2.getX() + vecToEnd1.getY() * vecToEnd2.getY();
            if (dot < 0) {
                // currentNpc nằm giữa đoạn nối a và b, không làm gì thêm
                return;
            }

            // Kiểm tra kề 1 trong 2 node
            int manhattan1 = Math.abs(vecToEnd1.getX()) + Math.abs(vecToEnd1.getY());
            int manhattan2 = Math.abs(vecToEnd2.getX()) + Math.abs(vecToEnd2.getY());

            if (manhattan1 != 1 && manhattan2 != 1) continue;

            // Cập nhật
            if (manhattan1 == 1) {
                npcPair[0] = currentNpc;
            } else {
                npcPair[1] = currentNpc;
            }

            updated = true;

            // Sắp xếp lại npcPair[0] và npcPair[1] theo thứ tự (ưu tiên Y tăng, sau đó X tăng)
            Node start = npcPair[0], end = npcPair[1];
            if ((start.getY() > end.getY()) || (start.getY() == end.getY() && start.getX() > end.getX())) {
                npcPair[0] = end;
                npcPair[1] = start;
            }

            break; // Đã cập nhật thì không duyệt thêm
        }

        // Nếu không cập nhật được vào cặp nào → thêm mới
        if (!updated) {
            trackedNpcSegments.add(new Node[] { currentNpc, currentNpc });
        }
    }


}