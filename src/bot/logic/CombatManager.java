package bot.logic;

import bot.utils.GameUtils;
import jsclub.codefest.sdk.model.support_items.SupportItem;
import jsclub.codefest.sdk.model.npcs.Ally;
import jsclub.codefest.sdk.base.Node;
import jsclub.codefest.sdk.model.players.Player;
import jsclub.codefest.sdk.model.weapon.Weapon;
import jsclub.codefest.sdk.algorithm.PathUtils;

import java.util.Comparator;
import java.util.List;

import sdk.Hero;
import sdk.HeroActionType;
import bot.BotContext;
import bot.navigation.PathPlanner;
import bot.memory.BotMemory;

public class CombatManager {
    static Hero hero;
    private static final int HEALTH_THRESHOLD_TO_HEAL = 60;

    public CombatManager(Hero hero) {
        this.hero = hero;
    }

    public boolean attackEnemy(int maxDistance) throws Exception {
        Player enemy = EnemySelector.findEnemyInAttackRange();
        if (handleRangedAttack(enemy)) {
            return true;
        }
        enemy = EnemySelector.findEnemyInRange(maxDistance);
        if (handleMeleeAttack(enemy)) {
            return true;
        }
        return false;
    }

    public boolean tryHealIfNeeded() throws Exception {
        if (BotContext.player.getHealth() < HEALTH_THRESHOLD_TO_HEAL) {
            // nếu máu dưới ngưỡng, ưu tiên dùng item hỗ trợ hồi máu
            SupportItem bestSupport = findBestSupportItem();
            if (bestSupport != null) {
                System.out.println("Low health. Using support item: " + bestSupport.getId());
                hero.botUseItem(bestSupport.getId());
                return true;
            }
            String pathToAlly = PathPlanner.getPathToAlly();
            if (pathToAlly != null &&  !pathToAlly.isEmpty()) {
                System.out.println("Low health and no items. Moving to SPIRIT ally for healing.");
                hero.move(pathToAlly);
                return true;
            }
        }
        return false;
    }

    private boolean handleRangedAttack(Player enemy) throws Exception {
        int sumWeapons = 0;
        if (enemy == null || enemy.getHealth() <= 0) {
            return false;
        }
        String path = PathUtils.getShortestPath(BotContext.gameMap, PathPlanner.getNodesToAvoid(false, true), BotContext.player, enemy, false);
        if (path == null) {
            return false;
        }

        Weapon rangedWeapons[] = {BotContext.inventory.getGun(), BotContext.inventory.getSpecial(), BotContext.inventory.getThrowable()};
        HeroActionType[] types = {HeroActionType.SHOOT, HeroActionType.USE_SPECIAL, HeroActionType.THROW_ITEM};
        Weapon bestWeapon = null;
        if (BotMemory.recentActions.isEmpty()) {
            System.out.println("LOGIC: recentActions is empty");
            return false; // Nếu danh sách hành động rỗng, không làm gì cả.
        }
        HeroActionType previousAction = BotMemory.recentActions.get(BotMemory.recentActions.size() - 1);
        int distance = PathUtils.distance(BotContext.player, enemy);
        boolean check = false;
        for (int i = 0; i < 3; i++) {
            sumWeapons++;
            Weapon weapon = rangedWeapons[i];
            check |= types[i] == previousAction;
            if (weapon == null || types[i] == previousAction) {
                continue; // Skip if no weapon available
            }
            if ((types[i] != HeroActionType.THROW_ITEM && weapon.getRange()[1] >= distance) ||
                (types[i] == HeroActionType.THROW_ITEM && weapon.getRange()[1] - 1 <= distance && distance <= weapon.getRange()[1] + 1)) {
                if (bestWeapon == null || weapon.getDamage() >= bestWeapon.getDamage()) {
                    bestWeapon = weapon; // Select the weapon with the highest damage within range
                }
            }
            if (bestWeapon != null && enemy.getHealth() <= bestWeapon.getDamage()) {
                break; // If the enemy's health is less than or equal to the weapon's damage, break
            }
        }
        if (sumWeapons > 1){
            if (bestWeapon == null) {
                return false;
            }
            switch (bestWeapon.getType()) {
                case GUN:
                    hero.botShoot(path);
                    return true;

                case SPECIAL:
                    hero.botUseSpecial(path);
                    return true;

                case THROWABLE:
                    hero.botThrowItem(path);
                    return true;
            }
            if (check) {
                hero.botAttack(path);
                return true;
            }
        }
        else {
            if (previousAction == HeroActionType.ATTACK && BotContext.inventory.getGun() != null) {
                hero.botShoot(path);
                return true;
            }
            hero.botAttack(path);
            return true;

        }
        return false;
    }

    public static boolean handleMeleeAttack(Player enemy) throws Exception {
        if (enemy == null || enemy.getHealth() <= 0) {
            return false;
        }
        int distance = PathUtils.distance(BotContext.player, enemy);
        String path = PathUtils.getShortestPath(BotContext.gameMap, PathPlanner.getNodesToAvoid(distance > 2, false), BotContext.player, enemy, false);
        if (path == null || path.isEmpty()) {
            return false;
        }
        if (BotMemory.recentActions.isEmpty()) {
            System.out.println("LOGIC: recentActions is empty. Skipping melee attack this turn.");
            return false; // Nếu danh sách hành động rỗng, không làm gì cả.
        }
        if (BotMemory.recentActions.isEmpty()) return false;
        HeroActionType previousAction = BotMemory.recentActions.get(BotMemory.recentActions.size() - 1);
        if (distance == 1) {
            if (previousAction == HeroActionType.ATTACK && BotContext.inventory.getGun() != null) {
                hero.botShoot(path);
                return true;
            }
            // If the enemy is adjacent, perform a melee attack
            hero.botAttack(path);
            return true; // Successfully attacked the enemy
        } else {
            System.out.println("Logic: Botattack");
            Weapon currentGun = BotContext.inventory.getGun();
            Weapon currentThrowable = BotContext.inventory.getThrowable();
            Weapon currentSpecial = BotContext.inventory.getSpecial();
            if ((currentGun != null && GameUtils.isAligned(BotContext.player , enemy) && currentGun.getRange()[1] >= distance &&  currentThrowable == null && currentSpecial == null) ||
                    (currentGun != null && GameUtils.isAligned(BotContext.player , enemy) && currentSpecial != null && currentThrowable != null &&
                            (currentSpecial.getRange()[1] < distance || currentThrowable.getRange()[1] < distance)))
            {
                if (!currentGun.getId().equals("SHOTGUN")){
                    System.out.println("ban doi do......");
                    if (previousAction == HeroActionType.ATTACK && BotContext.inventory.getGun() != null) {
                        hero.botShoot(path);
                        return true;
                    }
                    hero.botAttack(path);
                    return true;
                }
                else {
                    System.out.println("di chuyen1......");
                    hero.botMove(path);
                    return true;
                }
            }
            System.out.println("di chuyen2......");
            hero.botMove(path);
            return true;
            // Move towards the enemy if not adjacent
        }
    }

    private SupportItem findBestSupportItem() {
        List<SupportItem> items = hero.getInventory().getListSupportItem();
        if (items == null || items.isEmpty()) return null;

        return hero.getInventory().getListSupportItem().stream()
                .max(Comparator.comparingInt(SupportItem::getHealingHP))
                .orElse(null);
    }
}