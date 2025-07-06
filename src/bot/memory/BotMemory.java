package bot.memory;

import jsclub.codefest.sdk.base.Node;
import jsclub.codefest.sdk.model.GameMap;
import jsclub.codefest.sdk.model.players.Player;

import java.util.List;
import java.util.ArrayList;

import sdk.HeroActionType;

public class BotMemory {
    public static final Node NULL_NODE = new Node(-1, -1);

    private static List<GameMap> visitedMaps = new ArrayList<> ();
    public static List<Node> previousPositions = new ArrayList<>();
    public static List<HeroActionType> recentActions = new ArrayList<>();
    public static int actionCount = 0;

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

    public static void update(GameMap gameMap) {
        visitedMaps.add(gameMap);
        actionCount++;
        Player player = gameMap.getCurrentPlayer();
        if (player == null || player.getHealth() == 0) {
            previousPositions.add(NULL_NODE);
            recentActions.add(HeroActionType.DEATH);
        }
    }
}