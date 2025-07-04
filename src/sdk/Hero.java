package sdk;

import jsclub.codefest.sdk.model.Inventory;
import jsclub.codefest.sdk.factory.SupportItemFactory;

import java.io.IOException;

import bot.BotContext;
import bot.memory.BotMemory;


public class Hero extends jsclub.codefest.sdk.Hero {


    public Hero(String gameID, String playerName, String secretKey) {
        super(gameID, playerName, secretKey);
    }

    public void botMove(String direction) throws IOException {
        if (direction == null || direction.isEmpty()) {
            return; // Avoid moving if direction is not specified
        }
        System.out.println("Action: Moving");
        super.move(direction);
        BotMemory.lastAction.add("move");
    }

    public void botAttack(String direction) throws IOException {
        if (direction == null || direction.isEmpty()) {
            return; // Avoid attacking if direction is not specified
        }
        System.out.println("Action: Attacking");
        super.attack(direction.substring(0, 1)); // Ensure direction is a single character
        // Add the action to the pathAction list            
        BotMemory.lastAction.add("attack");
    }

    public void botShoot(String direction) throws IOException {
        if (direction == null || direction.isEmpty()) {
            return; // Avoid shooting if direction is not specified
        }
        System.out.println("Action: Shooting");
        super.shoot(direction.substring(0, 1)); // Ensure direction is a single character
        // Add the action to the pathAction list
        BotMemory.lastAction.add("shoot");
    }

    public void botThrowItem(String direction) throws IOException {
        if (direction == null || direction.isEmpty()) {
            return; // Avoid throwing item if direction is not specified
        }
        System.out.println("Action: Throwing Item");
        super.throwItem(direction.substring(0, 1));
        BotMemory.lastAction.add("throwItem");
    }

    public void botUseSpecial(String direction) throws IOException {
        if (direction == null || direction.isEmpty()) {
            return; // Avoid using special if direction is not specified
        }
        System.out.println("Action: Using Special");
        super.useSpecial(direction.substring(0, 1)); // Ensure direction is a single character
        // Add the action to the pathAction list
        BotMemory.lastAction.add("useSpecial");
    }

    public void botPickupItem() throws IOException {
        //         
        System.out.println("Action: Picking up item");
        super.pickupItem();
        BotMemory.lastAction.add("pickupItem");
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
        BotMemory.lastAction.add("useItem");
    }


    public void botRevokeItem(String itemId) throws IOException {
        // 
        if (itemId == null || itemId.isEmpty()) {
            return; // Avoid revoking item if itemId is not specified
        }
        System.out.println("Action: Revoking item with ID " + itemId);
        super.revokeItem(itemId);
        BotMemory.lastAction.add("revokeItem");
    }

    public void botIdle() throws IOException {
        // 
        System.out.println("Action: Idle");
        BotMemory.lastAction.add("idle");
    }

}
