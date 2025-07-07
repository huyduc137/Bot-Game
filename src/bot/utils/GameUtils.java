package bot.utils;

import jsclub.codefest.sdk.base.Node;

public class GameUtils {
    public static Node getCenterOfMap(int mapSize) {
        return new Node(mapSize / 2, mapSize / 2);
    }
    public static Node vectorBetween(Node from, Node to) {
        return new Node(to.getX() - from.getX(), to.getY() - from.getY());
    }
}