package uz.market.service;

import org.telegram.telegrambots.meta.api.objects.Update;
import uz.market.bot.MainBot;
import uz.market.entity.enums.State;
import uz.market.entity.Product;
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

        state.putIfAbsent(chatId, State.BUYER_MAIN);
        State currentState = state.get(chatId);

        if (currentState == State.BUYER_MAIN) {
            switch (text) {
                case "/start" -> sendMessage(chatId, Message.buyerMainPanelMsg);
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
            }
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
}