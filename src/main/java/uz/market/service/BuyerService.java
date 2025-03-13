package uz.market.service;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.market.bot.MainBot;
import uz.market.db.Datasource;
import uz.market.entity.*;
import uz.market.entity.enums.State;
import uz.market.util.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uz.market.db.Datasource.*;

public class BuyerService extends MainBot {
    private static final Map<Long, Map<String, Integer>> basket = new HashMap<>();
    public void service(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        state.putIfAbsent(chatId, State.BUYER_MAIN);
        State currentState = state.get(chatId);

        if (currentState == State.BUYER_MAIN) {
            switch (text) {
                case "/start" -> sendMessage(chatId, Message.buyerMainPanelMsg);
                case "buy" -> showShops(chatId);
                case "showHistoryBuyer" -> showHistory(chatId);
                case "manage balance" -> manageBalance(chatId);
                default -> sendMessage(chatId, "Iltimos kerakli bo'limni tanlang...");
            }
        }else if (currentState==State.BUYER_SELECTING_SHOP){
            showProducts(chatId, text);
        }else if (currentState==State.BUYER_SELECTING_PRODUCT){
            buyProduct(chatId, text);
        }else if (currentState==State.BUYER_MANAGING_BALANCE){
            updateBalance(chatId, text);
        }
    }
    private void showShops(Long chatId){
        if (shops.isEmpty()) {
            sendMessage(chatId, "hozircha do'konlar mavjud emas...");
            return;
        }
        List<List<InlineKeyboardButton>>rows = new ArrayList<>();
        for(String shopId: shops.keySet()){
            Shop shop = shops.get(shopId);
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(shop.getName())
                    .callbackData(shopId)
                    .build();
            rows.add(List.of(button));
        }
        InlineKeyboardMarkup markup= new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        sendMessage(chatId, "Do'konlardan birini tanlang:", markup);
        state.put(chatId, State.BUYER_SELECTING_SHOP);
    }
    private void showProducts(Long chatId, String shopId){
        if(!shops.containsKey(shopId)){
            sendMessage(chatId, "notogri kiritma...");
            return;
        }
        List<List<InlineKeyboardButton>>rows = new ArrayList<>();
        for(Product product : products.values()) {
            if (product.getShopId().equals(shopId)) {
                InlineKeyboardButton addButton = InlineKeyboardButton.builder()
                        .text("+" + product.getName() + "-" + product.getPrice() + "soums")
                        .callbackData("add-" + product.getId())
                        .build();
                rows.add(List.of(addButton));
            }
        }
        InlineKeyboardButton viewBasketButton = InlineKeyboardButton.builder()
                .text("🛒 Savatni ko'rish")
                .callbackData("VIEW_BASKET")
                .build();
        rows.add(List.of(viewBasketButton));
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        sendMessage(chatId, "mahsulotni tanlang:", markup);
        state.put(chatId, State.BUYER_SELECTING_PRODUCT);
    }
    private void handleBasket(Long chatId, String command){
        if(command.startsWith("add_")) {
            String productId = command.substring(4);
            basket.putIfAbsent(chatId, new HashMap<>());
            basket.get(chatId).put(productId, basket.get(chatId).getOrDefault(productId, 0) + 1);
            sendMessage(chatId, "Mahsulot savatga qoshildi!");
        }else if(command.equals("VIEW_BASKET")) {
            viewBasket(chatId);
        } else if(command.startsWith("remove_")){
            String productId = command.substring(7);
            if(basket.containsKey(chatId) && basket.get(chatId).containsKey(productId)){
                int count = basket.get(chatId).get(productId);
                if(count>1){
                    basket.get(chatId).put(productId, count-1);
                }else {
                    basket.get(chatId).remove(productId);
                }
                sendMessage(chatId, "mahsulot savatdan chiqarildi!");
            }

        }
    }
    private void viewBasket(Long chatId){
        if(!basket.containsKey(chatId) || basket.get(chatId).isEmpty()){
            sendMessage(chatId, " \uD83D\uDED2 hozirda sizning savatingiz bosh..");
            return;
        }
        StringBuilder basketMsg = new StringBuilder("Sizning savatingiz:\n");
        List<List<InlineKeyboardButton>>rows = new ArrayList<>();
        for(Map.Entry<String, Integer> entry: basket.get(chatId).entrySet()){
            Product product = products.get(entry.getKey());
            basketMsg.append("-").append(product.getName()).append("x")
                    .append(entry.getValue()).append("\n");

            InlineKeyboardButton minusButton = InlineKeyboardButton.builder()
                    .text("-" + product.getName())
                    .callbackData("remove_" + product.getId())
                    .build();
            rows.add(List.of(minusButton));
        }
        InlineKeyboardButton buyButton = InlineKeyboardButton.builder()
                .text("xarid qilish")
                .callbackData("buy")
                .build();
        rows.add(List.of(buyButton));
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        sendMessage(chatId, basketMsg.toString(), markup);
    }
    private void buyProduct(Long chatId, String productId){
        Buyer buyer = getBuyer(chatId);
        if(buyer==null){
            sendMessage(chatId, "siz hali royhatdan otmagansiz...");
            return;
        }
        Product product = products.get(productId);
        if (product==null){
            sendMessage(chatId, "Mahsulot topilmadi...");
            return;
        }
        if(buyer.getBalance()<product.getPrice()){
            sendMessage(chatId, "balansingizda mablag yetarli emas...");
            return;
        }
        buyer.setBalance(buyer.getBalance()- product.getPrice());
        sendMessage(chatId ,  product.getName() + " sotib olindi...");
        state.put(chatId, State.BUYER_MAIN);
    }
    private void showHistory(Long chatId){
        Buyer buyer = getBuyer(chatId);
        if(buyer==null){
            sendMessage(chatId, "siz hali royhatdan otmagansiz...");
            return;
        }
        List<Order>orders = Datasource.getOrdersByBuyerId(Long.valueOf(String.valueOf(chatId)));
        if(orders.isEmpty()){
            sendMessage(chatId, "siz hali hech narsa sotib olmadingiz...");
            return;
        }
        StringBuilder historyMsg = new StringBuilder("Xaridlar tarixi:\n");
        for (Order order : orders) {
            historyMsg.append("shop: ").append(shops.get(order.getShopId()).getName())
                    .append("\n Mahsulotlar: \n");
            for (Map.Entry<String, Integer> entry : order.getProductQuantities().entrySet()) {
                Product product = products.get(entry.getKey());
                historyMsg.append("    - ").append(product.getName())
                        .append(" x ").append(entry.getValue())
                        .append("\n");
            }
        }
        sendMessage(chatId, historyMsg.toString());
    }
    private void manageBalance(Long chatId){
        Buyer buyer = getBuyer(chatId);
        if (buyer == null) {
            sendMessage(chatId, "Siz ro‘yxatdan o‘tmagansiz!");
            return;
        }
        sendMessage(chatId, "Hozirda sizning balansingiz:" + buyer.getBalance() +
                "\nBalansizgizni toldirish uchun summa kiriting:");
        state.put(chatId, State.BUYER_MANAGING_BALANCE);
    }
    private void updateBalance(Long chatId, String text){
        try {
            double amount = Double.parseDouble(text);
            Buyer buyer = getBuyer(chatId);
            if(buyer!=null){
                buyer.setBalance(buyer.getBalance()+ amount);
                sendMessage(chatId, "balansingiz muvaffaqiyatli toldirildi, hozirda" + buyer.getBalance() + "soums");
                state.put(chatId, State.BUYER_MAIN);
            }
        } catch (NumberFormatException e ){
            sendMessage(chatId, "summa xato kiritildi...qayta urining!");
        }


    }
}

