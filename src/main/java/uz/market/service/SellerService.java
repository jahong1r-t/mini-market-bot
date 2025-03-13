package uz.market.service;

import org.telegram.telegrambots.meta.api.objects.Update;
import uz.market.bot.MainBot;
import uz.market.entity.Shop;
import uz.market.entity.enums.State;
import uz.market.util.Button;
import uz.market.util.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import static com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat.UUID;
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
                case Button.addShop -> {
                    sendMessage(chatId, Message.shopCreateMsg);
                    state.put(chatId, State.CREATE_SHOP);
                }
                case Button.showShops -> showShops(chatId);
            }
        } else if (currentState == State.CREATE_SHOP) {
            String id = java.util.UUID.randomUUID().toString();
         //   String id = UUID.randomUUID().toString();

            shops.put(id, new Shop(id, text, new ArrayList<>(), chatId, new ArrayList<>()));
            sendMessage(chatId, "\uD83C\uDF89 Tabriklaymiz! Do‘koningiz muvaffaqiyatli qo‘shildi!");
            state.remove(chatId);
        } else {

        }
    }


    private void showShops(Long chatId) {
        StringBuilder sb = new StringBuilder();
        sb.append("📢 Sizning do‘konlaringiz:\n\n");

        List<Shop> shopsByOwnerId = getShopsByOwnerId(chatId);

        if (shopsByOwnerId.isEmpty()) {
            sendMessage(chatId, Message.noShopsMsg);
            return;
        }

        List<String[]> inlineButtonList = new ArrayList<>();
        List<String[]> dataList = new ArrayList<>();

        List<String> rowButtons = new ArrayList<>();
        List<String> rowData = new ArrayList<>();

        for (int i = 0; i < shopsByOwnerId.size(); i++) {
            Shop shop = shopsByOwnerId.get(i);

            sb
                    .append(i + 1)
                    .append(". 🏪 ")
                    .append(shop.getName())
                    .append("\n")
                    .append("   ⭐️ ")
                    .append(shop.getRating())
                    .append("  |  📦 ")
                    .append(shop.getProductIds().size())
                    .append("\n\n");

            rowButtons.add(String.valueOf(i + 1));
            rowData.add("shopId:" + shop.getId());

            if ((i + 1) % 5 == 0 || i == shopsByOwnerId.size() - 1) {
                inlineButtonList.add(rowButtons.toArray(new String[0]));
                dataList.add(rowData.toArray(new String[0]));

                rowButtons.clear();
                rowData.clear();
            }
        }

        String[][] inlineButton = inlineButtonList.toArray(new String[0][0]);
        String[][] data = dataList.toArray(new String[0][0]);

        sendMessage(chatId, sb.toString(), inlineKeyboard(inlineButton, data));
    }


}
