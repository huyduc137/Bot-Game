package bot.core;

import sdk.Hero;
import bot.BotContext;
import bot.navigation.SafeZoneNavigator;
import bot.logic.CombatManager;
import bot.logic.ItemFinder;
import bot.memory.BotMemory;

public class BotController {
    private final Hero hero;
    private final SafeZoneNavigator safeZoneNavigator;
    private final CombatManager combatManager;
    private final ItemFinder itemFinder;

    public BotController(Hero hero) {
        this.hero = hero;
        this.safeZoneNavigator = new SafeZoneNavigator(hero);
        this.combatManager = new CombatManager(hero);
        this.itemFinder = new ItemFinder(hero);
    }

    public void handleMapUpdate(Object... args) {
        try {
            if (args == null || args.length == 0) {
                System.out.println("No update data received.");
                return; // Không có dữ liệu cập nhật
            }            
            if (!BotContext.updateAll(hero, args)) {
                // xử lý khi không cập nhật được dữ liệu
                System.out.println("Failed to update game context.");
            }

            // kiểm tra có lặp không

            // if ("move".equals(BotMemory.handledRepeatingSameAction())) {
            //     hero.botIdle();
            //     return;
            // }

            // ƯU TIÊN 1: luôn trong bo và xử lý hồi máu
            if (safeZoneNavigator.isInSafeZone() || combatManager.tryHealIfNeeded()) {
                return;
                // đổi hàm từ handleSurvival thành isInSafeZone trong class SafeZoneNavigator
                // đã tách hồi máu ít ở CombatManager.java
            }

            // ƯU TIÊN 2: nhặt đồ, ưu tiên súng;
            if (BotContext.inventory.getGun() == null) {
                itemFinder.action();
            }

            itemFinder.findPathToItem();

            // ƯU TIÊN 3: tìm mục tiêu hoặc nhặt đồ
            if (combatManager.attackEnemy(itemFinder.pathToItem != null ? itemFinder.pathToItem.length() : Integer.MAX_VALUE) ||
                itemFinder.action()) {
                // Nếu có kẻ địch trong phạm vi tấn công, thực hiện tấn công
                return;
            }

            hero.botIdle(); // Không có hành động nào cần thực hiện, bot sẽ đứng yên
            return;

        } catch (Exception e) {
            System.err.println("Error handling map update: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
