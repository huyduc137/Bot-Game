package sdk;

import bot.navigation.PathPlanner;
import bot.utils.GameUtils;
import jsclub.codefest.sdk.base.Node;
import jsclub.codefest.sdk.factory.SupportItemFactory;

import java.io.IOException;

import bot.BotContext;
import bot.memory.BotMemory;
import sdk.HeroActionType;


public class Hero extends jsclub.codefest.sdk.Hero {


    public Hero(String gameID, String playerName, String secretKey) {
        super(gameID, playerName, secretKey);
    }

    public void botMove(String direction) throws IOException {
        if (direction == null || direction.isEmpty()) {
            return; // Avoid moving if direction is not specified
        }
        System.out.println("Action: Moving");
//        super.move(direction);
//        BotMemory.recentActions.add(HeroActionType.MOVE);
        executeSmartMove(direction);
    }

    public void botAttack(String direction) throws IOException {
        if (direction == null || direction.isEmpty()) {
            return; // Avoid attacking if direction is not specified
        }
        System.out.println("Action: Attacking");
        super.attack(direction.substring(0, 1)); // Ensure direction is a single character
        // Add the action to the pathAction list            
        BotMemory.recentActions.add(HeroActionType.ATTACK);
    }

    public void botShoot(String direction) throws IOException {
        if (direction == null || direction.isEmpty()) {
            return; // Avoid shooting if direction is not specified
        }
        System.out.println("Action: Shooting");
        super.shoot(direction.substring(0, 1)); // Ensure direction is a single character
        // Add the action to the pathAction list
        BotMemory.recentActions.add(HeroActionType.SHOOT);
    }

    public void botThrowItem(String direction) throws IOException {
        if (direction == null || direction.isEmpty()) {
            return; // Avoid throwing item if direction is not specified
        }
        System.out.println("Action: Throwing Item");
        super.throwItem(direction.substring(0, 1));
        BotMemory.recentActions.add(HeroActionType.THROW_ITEM);
    }

    public void botUseSpecial(String direction) throws IOException {
        if (direction == null || direction.isEmpty()) {
            return; // Avoid using special if direction is not specified
        }
        System.out.println("Action: Using Special");
        super.useSpecial(direction.substring(0, 1)); // Ensure direction is a single character
        // Add the action to the pathAction list
        BotMemory.recentActions.add(HeroActionType.USE_SPECIAL);
    }

    public void botPickupItem() throws IOException {
        //         
        System.out.println("Action: Picking up item");
        super.pickupItem();
        BotMemory.recentActions.add(HeroActionType.PICKUP_ITEM);
    }

    public void botUseItem(String itemId) throws IOException {
        if (itemId == null || itemId.isEmpty()) {
            return; // Avoid using item if itemId is not specified
        }

        // Kiểm tra xem item có trong inventory không
        if (!BotContext.inventory.getListSupportItem()
                .contains(SupportItemFactory.getSupportItemById(itemId))) {
            return; // Avoid using item if it is not in the inventory
        }

        System.out.println("Action: Using item with ID " + itemId);
        super.useItem(itemId);
        BotMemory.recentActions.add(HeroActionType.USE_ITEM);
    }


    public void botRevokeItem(String itemId) throws IOException {
        // 
        if (itemId == null || itemId.isEmpty()) {
            return; // Avoid revoking item if itemId is not specified
        }
        System.out.println("Action: Revoking item with ID " + itemId);
        super.revokeItem(itemId);
        BotMemory.recentActions.add(HeroActionType.REVOKE_ITEM);
    }

    public void botIdle() throws IOException {
        // 
        System.out.println("Action: Idle");
        BotMemory.recentActions.add(HeroActionType.IDLE);
    }

    private void executeSmartMove(String path) throws IOException {
        char nextMoveDirection = path.charAt(0);

        if (shouldZigzag(nextMoveDirection)) {

            Node currentPos = BotContext.player;
            Node destination = GameUtils.getDestinationNodeFromPath(currentPos, path);

            int dx = destination.getX() - currentPos.getX();
            int dy = destination.getY() - currentPos.getY();

            char preferredZigzag1 = ' ';
            char preferredZigzag2 = ' ';

            if (nextMoveDirection == 'u' || nextMoveDirection == 'd') {
                if (dx > 0) {
                    preferredZigzag1 = 'r';
                    preferredZigzag2 = 'l';
                } else {
                    preferredZigzag1 = 'l';
                    preferredZigzag2 = 'r';
                }
            }
            else if (nextMoveDirection == 'l' || nextMoveDirection == 'r') {
                if (dy > 0) {
                    preferredZigzag1 = 'u';
                    preferredZigzag2 = 'd';
                } else {
                    preferredZigzag1 = 'd';
                    preferredZigzag2 = 'u';
                }
            }

            if (tryZigzagMove(preferredZigzag1)) {
                return;
            }
            if (tryZigzagMove(preferredZigzag2)) {
                return;
            }
        }
        System.out.println("Action: Moving (Normal) " + nextMoveDirection);
        super.move(String.valueOf(nextMoveDirection));
        updateMoveHistory(nextMoveDirection);
        BotMemory.recentActions.add(HeroActionType.MOVE);
    }

    private boolean shouldZigzag(char nextMove) {
        if (BotMemory.lastThreeMoves.size() < 5) {
            return false;
        }
        return BotMemory.lastThreeMoves.stream().allMatch(move -> move == nextMove);
    }

    private void updateMoveHistory(char move) {
        BotMemory.lastThreeMoves.addFirst(move);
        while (BotMemory.lastThreeMoves.size() > 5) {
            BotMemory.lastThreeMoves.removeLast();
        }
    }

    private boolean tryZigzagMove(char direction) throws IOException {
        if (direction == ' ') return false; // Không có hướng rẽ hợp lệ

        Node currentPos = BotContext.player;
        int targetX = currentPos.getX();
        int targetY = currentPos.getY();

        switch (direction) {
            case 'u': targetY++; break;
            case 'd': targetY--; break;
            case 'l': targetX--; break;
            case 'r': targetX++; break;
        }

        Node targetCell = new Node(targetX, targetY);

        if (PathPlanner.isCellSafeForZigzag(targetCell)) {
            System.out.println("Action: Moving (Zigzag) " + direction);
            super.move(String.valueOf(direction)); // Gọi hàm gốc của SDK
            BotMemory.recentActions.add(HeroActionType.MOVE);

            BotMemory.lastThreeMoves.clear();

            return true;
        }
        return false;
    }

}
