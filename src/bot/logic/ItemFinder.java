package bot.logic;

import bot.utils.GameUtils;
import jsclub.codefest.sdk.algorithm.PathUtils;
import jsclub.codefest.sdk.model.GameMap;
import jsclub.codefest.sdk.model.obstacles.Obstacle;
import jsclub.codefest.sdk.model.armors.Armor;
import jsclub.codefest.sdk.model.support_items.SupportItem;
import jsclub.codefest.sdk.model.weapon.Weapon;
import jsclub.codefest.sdk.model.players.Player;
import jsclub.codefest.sdk.model.ElementType;
import jsclub.codefest.sdk.model.Element;
import jsclub.codefest.sdk.factory.WeaponFactory;
import jsclub.codefest.sdk.base.Node;

import java.util.ArrayList;
import java.util.stream.Collectors;

import sdk.Hero;

import java.io.IOException;
import java.util.List;
import java.util.Comparator;

import bot.BotContext;
import bot.memory.BotMemory;

import static bot.navigation.PathPlanner.getNodesToAvoid;

public class ItemFinder {
    private final Hero hero;
    private String pathToGun;
    private Weapon nearestGun;
    private String pathToMelee;
    private Weapon nearestMelee;
    private String pathToThrowable;
    private Weapon nearestThrowable;
    private String pathToSpecial;
    private Weapon nearestSpecial;
    private String pathToChest;
    private Obstacle nearestChest;
    private String pathToArmor;
    private Armor nearestArmor;
    private String pathToHelmet;
    private Armor nearestHelmet;
    private String pathToHealthItem;
    private SupportItem nearestSupportItem;
    private int actionCount = -1;
    private ElementType nearestItem;

    public String pathToItem;
    public int pathToGuns = 0;

    public ItemFinder(Hero hero) {
        this.hero = hero;
    }

    private boolean isItemInSafeZone(Node itemNode) {
        return PathUtils.checkInsideSafeArea(itemNode, BotContext.gameMap.getSafeZone(), BotContext.gameMap.getMapSize());
    }

    private String findPathToItem(Node itemNode) {
        if (itemNode == null) {
            return null;
        }
        return PathUtils.getShortestPath(BotContext.gameMap, getNodesToAvoid(true, false), BotContext.player, itemNode, false);
    }


    private Weapon getNearestGun() {
        // if (BotContext.inventory.getGun() != null) {
        //     return null; // If the player already has a gun, no need to find one
        // }
        List<Weapon> Guns = BotContext.gameMap.getAllGun();
        Weapon nearestGun = null;
        Weapon currentGun = BotContext.inventory.getGun();
        int minDistance = Integer.MAX_VALUE;
        for (Weapon gun : Guns) {
            if (!isItemInSafeZone(gun)) {
                continue;
            }
            if (currentGun != null && gun.getDamage() * gun.getUseCount() <= currentGun.getDamage() * currentGun.getUseCount()) {
                continue;
            }
            String path = findPathToItem(gun);
            int distance = (path == null ? Integer.MAX_VALUE : path.length());
            if (distance < minDistance) {
                minDistance = distance;
                nearestGun = gun;
            }
        }
        return this.nearestGun = nearestGun;
    }

    private Weapon getNearestMelee() {
    // if (BotContext.inventory.getMelee() != WeaponFactory.getWeaponById("HAND")) {
    //     return null; // If the player already has a melee weapon, no need to find one
    // }
        List<Weapon> MeleeWeapons = BotContext.gameMap.getAllMelee();
        Weapon nearestMelee = null;
        Weapon currentMelee = BotContext.inventory.getMelee();
        double minDistance = Double.MAX_VALUE;
        for (Weapon melee : MeleeWeapons) {
            if (!isItemInSafeZone(melee)) {
            continue;
            }

            if (currentMelee != WeaponFactory.getWeaponById("HAND") && melee.getDamage() <= currentMelee.getDamage()) {
                continue;
            }

            String path = findPathToItem(melee);
            int distance = (path == null ? Integer.MAX_VALUE : path.length());
            if (distance < minDistance) {
                minDistance = distance;
                nearestMelee = melee;
            }
        }
        return this.nearestMelee = nearestMelee;
    }

    private Weapon getNearestThrowable() {
        // if (BotContext.inventory.getThrowable() != null) {
        //     return null; // If the player already has a throwable weapon, no need to find one
        // }
        List<Weapon> Throwables = BotContext.gameMap.getAllThrowable();
        Weapon nearestThrowable = null;
        Weapon currentThrowable = BotContext.inventory.getThrowable();
        double minDistance = Double.MAX_VALUE;
        for (Weapon throwable : Throwables) {
            if (!isItemInSafeZone(throwable)){
                continue;
            }
            if (currentThrowable != null && throwable.getDamage() * throwable.getUseCount() <= currentThrowable.getDamage() * currentThrowable.getDamage()) {
                continue;
            }
            String path = findPathToItem(throwable);
            int distance = (path == null ? Integer.MAX_VALUE : path.length());
            if (distance < minDistance) {
                minDistance = distance;
                nearestThrowable = throwable;
            }
        }
        return this.nearestThrowable = nearestThrowable;
    }

    private Weapon getNearestSpecial() {
        // if (BotContext.inventory.getSpecial() != null) {
        //     return null; // If the player already has a special weapon, no need to find one
        // }
        List<Weapon> SpecialWeapons = BotContext.gameMap.getAllSpecial();
        Weapon nearestSpecial = null;
        double minDistance = Double.MAX_VALUE;
        for (Weapon special : SpecialWeapons) {
            if (!isItemInSafeZone(special)) {
                continue; // Skip items not in the safe zone
            }
            String path = findPathToItem(special);
            int distance = (path == null ? Integer.MAX_VALUE : path.length());
            if (distance < minDistance) {
                minDistance = distance;
                nearestSpecial = special;
            }
        }
        return this.nearestSpecial = nearestSpecial;
    }

    private Obstacle getNearestChest() {
        if (hasEssentialItems()) {
            return null;
        }
        List<Obstacle> chests = BotContext.gameMap.getListObstacles().stream()
                .filter(obstacle -> ElementType.CHEST.equals(obstacle.getType()))
                .collect(Collectors.toList());

        Obstacle nearestChest = null;
        double minDistance = Double.MAX_VALUE;
        for (Obstacle chest : chests) {
            if (!isItemInSafeZone(chest)) {
                continue; // Skip items not in the safe zone
            }
            String path = findPathToItem(chest);
            int distance = (path == null ? Integer.MAX_VALUE : path.length());
            if (distance < minDistance) {
                minDistance = distance;
                nearestChest = chest;
            }
        }
        return this.nearestChest = nearestChest;
    }

    private Armor getNearestArmor() {
         List<Armor> armors = BotContext.gameMap.getListArmors();
        if (BotContext.inventory.getArmor() != null) {
            return null;
        }
        Armor nearestArmor = null;
        double minDistance = Double.MAX_VALUE;
        for (Armor armor : armors) {
            if (!isItemInSafeZone(armor) || armor.getType() == ElementType.HELMET) {
                continue; // Skip helmets, as they are handled separately
            }
            String path = findPathToItem(armor);
            int distance = (path == null ? Integer.MAX_VALUE : path.length());
            if (distance < minDistance) {
                minDistance = distance;
                nearestArmor = armor;
            }
        }
        return this.nearestArmor = nearestArmor;
    }

    private Armor getNearestHelmet() {
         List<Armor> helmets = BotContext.gameMap.getListArmors();
        if (BotContext.inventory.getHelmet() != null) {
            return null;
        }
        
        Armor nearestHelmet = null;
        double minDistance = Double.MAX_VALUE;
        for (Armor helmet : helmets) { 
            if (!isItemInSafeZone(helmet) || helmet.getType() != ElementType.HELMET) {
                continue; // Only consider helmets
            }
            String path = findPathToItem(helmet);
            int distance = (path == null ? Integer.MAX_VALUE : path.length());
            if (distance < minDistance) {
                minDistance = distance;
                nearestHelmet = helmet;
            }
        }
        return this.nearestHelmet = nearestHelmet;
    }

    private SupportItem getNearestSupportItem() {
        // List<SupportItem> currentSupportItems = BotContext.inventory.getListSupportItem();
        // if (currentSupportItems != null && currentSupportItems.size() == 4) {
        //     return null;
        // }

        List<SupportItem> supportItems = BotContext.gameMap.getListSupportItems();
        SupportItem nearestSupportItem = null;
        double minDistance = Double.MAX_VALUE;

        for (SupportItem supportItem : supportItems) {
            if (!isItemInSafeZone(supportItem) || supportItem.getId().equals("COMPASS")) {
                continue; // Skip compass, as it is not a healing support item
            }

            String path = findPathToItem(supportItem);
            int distance = (path == null ? Integer.MAX_VALUE : path.length());

            if (distance < minDistance) {
                minDistance = distance;
                nearestSupportItem = supportItem;
            }
        }

        return this.nearestSupportItem = nearestSupportItem;
    }


    public void findPathToItem() throws IOException {
        if (actionCount == BotMemory.actionCount) {
            // nếu actionCount không thay đổi, không cần tìm lại đường đi
            return ;
        }
        actionCount = BotMemory.actionCount;

        pathToGun = findPathToItem(getNearestGun());
        pathToMelee = findPathToItem(getNearestMelee());
        pathToThrowable = findPathToItem(getNearestThrowable());
        pathToSpecial = findPathToItem(getNearestSpecial());
        pathToChest = findPathToItem(getNearestChest());
        pathToArmor = findPathToItem(getNearestArmor());
        pathToHelmet = findPathToItem(getNearestHelmet());
        pathToHealthItem = findPathToItem(getNearestSupportItem());


        String[] pathsArray = {
            pathToGun, pathToMelee, pathToThrowable, pathToSpecial,
            pathToChest, pathToArmor, pathToHelmet, pathToHealthItem
        };

        ElementType[] types = {
            ElementType.GUN, ElementType.MELEE, ElementType.THROWABLE, ElementType.SPECIAL,
            ElementType.CHEST, ElementType.ARMOR, ElementType.HELMET, ElementType.SUPPORT_ITEM
        };

        nearestItem = null;
        pathToItem = null;

        for (int i = 0; i < pathsArray.length; i++) {
            String path = pathsArray[i];
            if (path == null) {
                continue;
            }
            if (pathToItem == null || path.length() < pathToItem.length()) {
                pathToItem = path;
                nearestItem = types[i];
            }
        }

        // System.err.println("Best path type: " + bestType);
        // System.err.println("Best path: " + bestPath);
    }

    public Boolean action() throws IOException {
        findPathToItem();
        if (pathToItem != null) {
            if (pathToItem.length() == 1 && pathToChest != null && pathToChest.length() == 1) {
                System.out.println("Attacking along path: " + pathToItem);
                hero.botAttack( String.valueOf(pathToItem.charAt(0)));
                return true;
            } else if (pathToItem.length() == 0) {
                if (nearestItem != null) {
                    String itemId = getInventoryWeapon(nearestItem);
                    if (itemId != null) {
                        hero.botRevokeItem(itemId);
                        return true;
                    }
                }
                System.out.println("Picking up item");
                hero.botPickupItem();
                return true;
            } else {
                System.out.println("Moving along path: " + pathToItem);
                hero.botMove(pathToItem);
                return true;
            }
        } else {
            System.out.println("No path to item found");
            pathToItem = null; // Reset pathToItem if no item is found
            pathToGuns = 1;
        }
        return false;
    }
    private SupportItem findWorstSupportItem() {
        List<SupportItem> items = BotContext.inventory.getListSupportItem();
        if (items == null || items.isEmpty()) return null;

        return items.stream()
                .min(Comparator.comparingInt(SupportItem::getHealingHP))
                .orElse(null);
    }

    private String getInventoryWeapon(ElementType type) {
        List<SupportItem> supportItems = BotContext.inventory.getListSupportItem();
        Element[] inventory = {
            BotContext.inventory.getGun(),
            BotContext.inventory.getMelee() != WeaponFactory.getWeaponById("HAND") ? BotContext.inventory.getMelee() : null,
            BotContext.inventory.getThrowable(),
            BotContext.inventory.getSpecial(),
            BotContext.inventory.getArmor(),
            BotContext.inventory.getHelmet(),
            (supportItems != null && supportItems.size() == 4) ?  findWorstSupportItem() : null
        };

        for (Element item : inventory) {
            if (item != null && item.getType() == type) {
                return item.getId();
            }
        }

        return null;
    }

    private boolean hasEssentialItems() {
        boolean hasGun = BotContext.inventory.getGun() != null;

        boolean hasMelee = BotContext.inventory.getMelee() != null &&
                !BotContext.inventory.getMelee().equals(WeaponFactory.getWeaponById("HAND"));

        boolean hasThrowable = BotContext.inventory.getThrowable() != null;

        List<SupportItem> supportItems = BotContext.inventory.getListSupportItem();
        int supportCount = (supportItems != null) ? supportItems.size() : 0;
        boolean hasAtLeastTwoSupportItems = supportCount >= 2;

        return hasGun && hasMelee && hasThrowable && hasAtLeastTwoSupportItems;
    }

    public boolean moveToNearestGun(GameMap gameMap, Player player) throws IOException {
        List<Weapon> gunsInSafeZone = gameMap.getAllGun().stream()
                .filter(gun -> PathUtils.checkInsideSafeArea(gun.getPosition(), gameMap.getSafeZone(), gameMap.getMapSize()))
                .collect(Collectors.toList());
        Weapon nearestGunInMethod = (Weapon) findNearestNode(player, gunsInSafeZone);

        if (nearestGunInMethod != null) {
            System.out.println("LOGIC: Found gun " + nearestGunInMethod.getId() + " in safe zone. Moving to it.");
            String pathToGun = PathUtils.getShortestPath(gameMap, getNodesToAvoid(true, true), player, nearestGunInMethod, false);

            if (pathToGun != null) {
                if (pathToGun.isEmpty()) {
                    hero.pickupItem();
                } else {
                    hero.move(pathToGun);
                }
            } else {
                // Không tìm thấy đường đi dù súng tồn tại, có thể bị chặn hoàn toàn
                System.out.println("LOGIC: Gun found but path is blocked. Moving to center.");
                moveToSafeZone(gameMap, player);
                return false;
            }
        } else {
            // Không tìm thấy súng nào trong vùng an toàn
            System.out.println("LOGIC: No gun found in safe zone. Moving to center.");
            moveToSafeZone(gameMap, player);
            return false;
        }
        return true;
    }
    private Node findNearestNode(Player myPlayer, List<? extends Node> nodes) {
        if (nodes == null || nodes.isEmpty()) return null;
        return nodes.stream()
                .min(Comparator.comparingInt(node -> PathUtils.distance(myPlayer, node)))
                .orElse(null);
    }
    private void moveToSafeZone(GameMap gameMap, Player myPlayer) throws IOException {
        Node centerOfMap = GameUtils.getCenterOfMap(gameMap.getMapSize());
        String pathToSafety = PathUtils.getShortestPath(gameMap, getNodesToAvoid(true, true), myPlayer, centerOfMap, false);
        if (pathToSafety != null && !pathToSafety.isEmpty()) {
            hero.move(pathToSafety);
        }
    }
    public Player findNearestPlayer(GameMap gameMap, Player myPlayer) {
        return gameMap.getOtherPlayerInfo().stream()
                .filter(p -> p.getHealth() > 0)
                .filter(p -> PathUtils.checkInsideSafeArea(p.getPosition(), gameMap.getSafeZone(), gameMap.getMapSize()))
                .min(Comparator.comparingInt(p -> PathUtils.distance(myPlayer, p)))
                .orElse(null);
    }
}
