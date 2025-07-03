package bot.memory;

import jsclub.codefest.sdk.base.Node;

import java.util.List;
import java.util.ArrayList;

public class BotMemory {
    public static List<Node> previousPositions = new ArrayList<>();
    public static List<String> lastAction = new ArrayList<>();
    public static int actionCount = 0;

    public static String handledRepeatingSameAction() {
        if (lastAction.size() < 5 || previousPositions.size() < 5) {
            return null;
        }

        String last = lastAction.get(lastAction.size() - 1);

        for (int i = 2; i <= 5; i++) {
            String act = lastAction.get(lastAction.size() - i);
            if (!act.equals(last)) {
                return null; 
            }
        }

        if (last.equals("move")) {
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
}