package uz.market.service;

import org.telegram.telegrambots.meta.api.objects.Update;
import uz.market.bot.MainBot;
import uz.market.entity.enums.State;
import uz.market.util.Message;

import static uz.market.db.Datasource.*;

public class SellerService extends MainBot {
    public void service(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        state.putIfAbsent(chatId, State.SELLER_MAIN);
        State currentState = state.get(chatId);

        if (currentState == State.SELLER_MAIN) {
            switch (text) {
                case "/start" -> sendMessage(chatId, Message.sellerMainPanelMsg);
            }
        }

    }
}
