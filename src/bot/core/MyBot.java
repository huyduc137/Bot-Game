package bot.core;

import io.socket.emitter.Emitter;
import sdk.Hero;

import bot.BotConfig;

public class MyBot implements Emitter.Listener {
    private final BotController controller;

    private MyBot(Hero hero) {
        this.controller = new BotController(hero);
    }

    public static void run() {
        try {
            Hero hero = new Hero(BotConfig.GAME_ID, BotConfig.PLAYER_NAME, BotConfig.SECRET_KEY);
            MyBot bot = new MyBot(hero);

            hero.setOnMapUpdate(bot);
            hero.start(BotConfig.SERVER_URL);

        } catch (Exception e) {
            System.err.println("Failed to start bot: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void call(Object... args) {
        controller.handleMapUpdate(args); // Ủy quyền cho BotController
    }
}
