package uz.market.service;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.market.bot.MainBot;
import uz.market.db.Datasource;
import uz.market.entity.*;
import uz.market.entity.enums.State;
import uz.market.util.Button;
import uz.market.util.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uz.market.db.Datasource.*;

public class BuyerService extends MainBot {

    private final Map<Long, List<Product>> userBaskets = new HashMap<>();

    public void service(Update update) {
        Long chatId;

        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            String callbackData = update.getCallbackQuery().getData();
            System.out.println("Callback received: " + callbackData); // Отладка

            if (callbackData.startsWith("productId:")) {
                String productId = callbackData.split(":")[1];
                System.out.println("Product ID to add: " + productId); // Отладка
                addToBasket(chatId, productId);
                return;
            } else {
                System.out.println("Invalid callback data: " + callbackData); // Отладка
                sendMessage(chatId, "Noto'g'ri buyruq!");
            }
        } else {
            chatId = update.getMessage().getChatId();
        }

        String text = update.hasMessage() && update.getMessage().hasText() ?
                update.getMessage().getText() : "";


    private static final Map<Long, Map<String, Integer>> basket = new HashMap<>();

    public void service(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        state.putIfAbsent(chatId, State.BUYER_MAIN);
        State currentState = state.get(chatId);

        if (currentState == State.BUYER_MAIN) {
            switch (text) {
                case "/start" -> sendMessage(chatId, Message.buyerMainPanelMsg);
                case Button.showShopsBuyer -> showShops(chatId);
                case Button.basket -> showBasket(chatId);

                case "📦 Tovarlar" -> showProducts(chatId);
                case Button.searchProduct -> {
                    sendMessage(chatId, "Mahsulot nomini kiriting:");
                    state.put(chatId, State.SEARCH);
                }
            }
        } else if (currentState == State.SEARCH) {
            searchProducts(chatId, text);
            state.put(chatId, State.BUYER_MAIN);
        }
    }

    private void addToBasket(Long chatId, String productId) {
        System.out.println("Attempting to add product with ID: " + productId); // Отладка
        Product product = products.get(productId);

        if (product == null) {
            System.out.println("Product not found in products map for ID: " + productId); // Отладка
            sendMessage(chatId, "Tovar topilmadi! ID: " + productId);
            return;
        }

        userBaskets.putIfAbsent(chatId, new ArrayList<>());
        userBaskets.get(chatId).add(product);

        String message = "✅ " + product.getName() + " savatga qo'shildi!\n" +
                "Narxi: " + product.getPrice() + "\n" +
                "Savatdagi tovarlar soni: " + userBaskets.get(chatId).size();
        sendMessage(chatId, message);
    }

    private void showProducts(Long chatId) {
        StringBuilder sb = new StringBuilder();
        sb.append("📦 Mavjud bo'lgan tovarlar:\n\n");

        List<Product> availableProducts = getAvailableProducts();

        if (availableProducts.isEmpty()) {
            sendMessage(chatId, "Tovar hozircha mavjud emas.");
            return;
        }

        List<String[]> inlineButtonList = new ArrayList<>();
        List<String[]> dataList = new ArrayList<>();

        List<String> rowButtons = new ArrayList<>();
        List<String> rowData = new ArrayList<>();

        for (int i = 0; i < availableProducts.size(); i++) {
            Product product = availableProducts.get(i);

            sb.append(i + 1)
                    .append(". 📋 ")
                    .append(product.getName())
                    .append("\n")
                    .append("   💰 ")
                    .append(product.getPrice())
                    .append("  |  📏 ")
                    .append(product.getQuantity())
                    .append("\n\n");

            rowButtons.add(String.valueOf(i + 1));
            rowData.add("productId:" + product.getId());

            if ((i + 1) % 5 == 0 || i == availableProducts.size() - 1) {
                inlineButtonList.add(rowButtons.toArray(new String[0]));
                dataList.add(rowData.toArray(new String[0]));

                rowButtons.clear();
                rowData.clear();
            }
        }

        String[][] inlineButtons = inlineButtonList.toArray(new String[0][0]);
        String[][] data = dataList.toArray(new String[0][0]);

        sendMessage(chatId, sb.toString(), inlineKeyboard(inlineButtons, data));
    }

    private void searchProducts(Long chatId, String text) {
        StringBuilder sb = new StringBuilder();
        sb.append("🔍 Qidiruv natijasi (Nomi: ").append(text).append("):\n\n");

        List<Product> availableProducts = getAvailableProducts();
        Product foundProduct = null;

        String searchName = text.trim().toLowerCase();
        for (Product product : availableProducts) {
            if (product.getName().toLowerCase().equals(searchName)) {
                foundProduct = product;
                break;
                case "buy" -> showShops(chatId);
                case "showHistoryBuyer" -> showHistory(chatId);
                case Button.balance -> manageBalance(chatId);
                default -> sendMessage(chatId, "Iltimos kerakli bo'limni tanlang...");
            }
        } else if (currentState == State.BUYER_SELECTING_SHOP) {
            showProducts(chatId, text);
        } else if (currentState == State.BUYER_SELECTING_PRODUCT) {
            buyProduct(chatId, text);
        } else if (currentState == State.BUYER_MANAGING_BALANCE) {
            updateBalance(chatId, text);
        }
    }

    private void showBasket(Long chatId) {
        String basketId = getBuyer(chatId).getBasketId();
        Basket basket = baskets.get(basketId);

        if (basket == null || basket.getProductId().isEmpty()) {
            sendMessage(chatId, "🛒 Sizning savatingiz bo‘sh!");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("🛒 Sizning savatingiz\n\n");

        double totalPrice = 0;
        int index = 0;

        for (String productId : basket.getProductId()) {
            Product product = products.get(productId);
            if (product == null) continue;

            index++;
            totalPrice += product.getPrice();

            sb.append(index).append(". ").append(product.getName()).append("\n")
                    .append("   💰 Narx: ").append(product.getPrice()).append(" so‘m\n")
                    .append("   🏪 Do‘kon: ").append(shops.get(product.getShopId()).getName()).append("\n\n");
        }

        sb.append("💵 Umumiy narx: ").append(totalPrice).append(" so‘m");

        sendMessage(chatId, sb.toString(), inlineKeyboard(new String[][]{{"Sotib olish"}}, new String[][]{{"basket:" + basketId + ":price:" + totalPrice}}));

    }

    private void showProducts(Long chatId, String shopId) {
        if (!shops.containsKey(shopId)) {
            sendMessage(chatId, "notogri kiritma...");
            return;
        }
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (Product product : products.values()) {
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

    private void handleBasket(Long chatId, String command) {
        if (command.startsWith("add_")) {
            String productId = command.substring(4);
            basket.putIfAbsent(chatId, new HashMap<>());
            basket.get(chatId).put(productId, basket.get(chatId).getOrDefault(productId, 0) + 1);
            sendMessage(chatId, "Mahsulot savatga qoshildi!");
        } else if (command.equals("VIEW_BASKET")) {
            viewBasket(chatId);
        } else if (command.startsWith("remove_")) {
            String productId = command.substring(7);
            if (basket.containsKey(chatId) && basket.get(chatId).containsKey(productId)) {
                int count = basket.get(chatId).get(productId);
                if (count > 1) {
                    basket.get(chatId).put(productId, count - 1);
                } else {
                    basket.get(chatId).remove(productId);
                }
                sendMessage(chatId, "mahsulot savatdan chiqarildi!");
            }

        }
    }

    private void viewBasket(Long chatId) {
        if (!basket.containsKey(chatId) || basket.get(chatId).isEmpty()) {
            sendMessage(chatId, " \uD83D\uDED2 hozirda sizning savatingiz bosh..");
            return;
        }
        StringBuilder basketMsg = new StringBuilder("Sizning savatingiz:\n");
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : basket.get(chatId).entrySet()) {
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

    private void buyProduct(Long chatId, String productId) {
        Buyer buyer = getBuyer(chatId);
        if (buyer == null) {
            sendMessage(chatId, "siz hali royhatdan otmagansiz...");
            return;
        }
        Product product = products.get(productId);
        if (product == null) {
            sendMessage(chatId, "Mahsulot topilmadi...");
            return;
        }
        if (buyer.getBalance() < product.getPrice()) {
            sendMessage(chatId, "balansingizda mablag yetarli emas...");
            return;
        }
        buyer.setBalance(buyer.getBalance() - product.getPrice());
        sendMessage(chatId, product.getName() + " sotib olindi...");
        state.put(chatId, State.BUYER_MAIN);
    }

    private void showHistory(Long chatId) {
        Buyer buyer = getBuyer(chatId);
        if (buyer == null) {
            sendMessage(chatId, "siz hali royhatdan otmagansiz...");
            return;
        }
        List<Order> orders = Datasource.getOrdersByBuyerId(Long.valueOf(String.valueOf(chatId)));
        if (orders.isEmpty()) {
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

    private void manageBalance(Long chatId) {
        Buyer buyer = getBuyer(chatId);
        if (buyer == null) {
            sendMessage(chatId, "Siz ro‘yxatdan o‘tmagansiz!");
            return;
        }
        sendMessage(chatId, "Hozirda sizning balansingiz:" + buyer.getBalance() +
                "\nBalansizgizni toldirish uchun summa kiriting:");
        state.put(chatId, State.BUYER_MANAGING_BALANCE);
    }

    private void updateBalance(Long chatId, String text) {
        try {
            double amount = Double.parseDouble(text);
            Buyer buyer = getBuyer(chatId);
            if (buyer != null) {
                buyer.setBalance(buyer.getBalance() + amount);
                sendMessage(chatId, "balansingiz muvaffaqiyatli toldirildi, hozirda" + buyer.getBalance() + "soums");
                state.put(chatId, State.BUYER_MAIN);
            }
        } catch (NumberFormatException e) {
            sendMessage(chatId, "summa xato kiritildi...qayta urining!");
        }


        if (foundProduct == null) {
            sendMessage(chatId, "Nomi bo'yicha tovar topilmadi: " + text);
            return;
        }

        System.out.println("Found Product ID: " + foundProduct.getId()); // Отладка
        sb.append("📋 ")
                .append(foundProduct.getName())
                .append("\n")
                .append("   💰 ")
                .append(foundProduct.getPrice())
                .append("  |  📏 ")
                .append(foundProduct.getQuantity())
                .append("\n");

        String[][] inlineButtons = new String[][]{{"1"}};
        String[][] data = new String[][]{{"productId:" + foundProduct.getId()}};
        System.out.println("Callback Data: " + data[0][0]); // Отладка
        sendMessage(chatId, sb.toString(), inlineKeyboard(inlineButtons, data));
    }

    private List<Product> getAvailableProducts() {
        System.out.println("Products in Datasource: " + products); // Отладка
        return new ArrayList<>(products.values());
    }

    private void showShops(Long chatId) {
        StringBuilder sb = new StringBuilder();
        sb.append("📢 Barcha do‘konlar:\n\n");

        if (shops.isEmpty()) {
            sendMessage(chatId, "❌ Hech qanday do‘kon topilmadi.");
            return;
        }

        List<String[]> inlineButtonList = new ArrayList<>();
        List<String[]> dataList = new ArrayList<>();

        List<String> rowButtons = new ArrayList<>();
        List<String> rowData = new ArrayList<>();

        int index = 0;
        for (Shop shop : shops.values()) {
            index++;

            sb
                    .append(index)
                    .append(". 🏪 Do'kon nomi: ")
                    .append(shop.getName())
                    .append("\n")
                    .append("   ⭐️ Reyting: ")
                    .append(shop.getRating())
                    .append("  |  📦 Mahsulotlar soni: ")
                    .append(shop.getProductIds().size())
                    .append("\n\n");

            rowButtons.add(String.valueOf(index));
            rowData.add("shopId:" + shop.getId());

            if (index % 5 == 0 || index == shops.size()) {
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

}
