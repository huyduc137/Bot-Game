package bot.utils;

import jsclub.codefest.sdk.base.Node;

public class GameUtils {
    public static Node getCenterOfMap(int mapSize) {
        return new Node(mapSize / 2, mapSize / 2);
    }
    
    public static Node vectorBetween(Node from, Node to) {
        return new Node(to.getX() - from.getX(), to.getY() - from.getY());
    }
    
    public static boolean isLE(Node x, Node y) {
        return x.getX() <= y.getX() && x.getY() <= y.getY();
    }

    public static boolean isAligned(Node p1, Node p2) {
        if (p1 == null || p2 == null) return false;
        return p1.getX() == p2.getX() || p1.getY() == p2.getY();
    }
    public static Node getDestinationNodeFromPath(Node start, String path) {
        if (path == null || path.isEmpty()) {
            return start;
        }
        Node destination = new Node(start.getX(), start.getY());

        for (char move : path.toCharArray()) {
            switch (move) {
                case 'u': destination.y++; break;
                case 'd': destination.y--; break;
                case 'l': destination.x--; break;
                case 'r': destination.x++; break;
            }
        }
        return destination;
    }
}