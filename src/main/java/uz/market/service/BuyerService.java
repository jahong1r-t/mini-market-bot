package uz.market.service;

import org.telegram.telegrambots.meta.api.objects.Update;
import uz.market.bot.MainBot;
import uz.market.entity.enums.State;
import uz.market.util.Message;

import static uz.market.db.Datasource.state;

public class BuyerService extends MainBot {
    public void service(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        state.putIfAbsent(chatId, State.BUYER_MAIN);
        State currentState = state.get(chatId);

        if (currentState == State.BUYER_MAIN) {
            switch (text) {
                case "/start" -> sendMessage(chatId, Message.buyerMainPanelMsg);
            }
        }

    }
}
