package uz.market.service;


import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.market.bot.MainBot;
import uz.market.entity.Product;
import uz.market.entity.Shop;
import uz.market.entity.enums.State;
import uz.market.util.Button;
import uz.market.util.Message;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;


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
                case Button.showShops -> showShops(chatId, false);
                case Button.addProduct -> {

                    sendMessage(chatId, Message.productCreateMsg);
                    tempProduct.put(chatId, new Product());
                    state.put(chatId, State.CREATE_PRODUCT_NAME);
                }
            }
        } else if (currentState == State.CREATE_SHOP) {
            String id = UUID.randomUUID().toString();

            shops.put(id, new Shop(id, text, new ArrayList<>(), chatId, new ArrayList<>()));
            sendMessage(chatId, "\uD83C\uDF89 Tabriklaymiz! Do‘koningiz muvaffaqiyatli qo‘shildi!");
            state.put(chatId, State.SELLER_MAIN);

        } else if (currentState == State.CREATE_PRODUCT_NAME) {
            tempProduct.get(chatId).setId(UUID.randomUUID().toString());
            tempProduct.get(chatId).setName(text);
            sendMessage(chatId, Message.productPriceMsg);
            state.put(chatId, State.CREATE_PRODUCT_PRICE);
        } else if (currentState == State.CREATE_PRODUCT_PRICE) {
            if (!text.matches("\\d+(\\.\\d+)?") || Double.parseDouble(text) <= 0) {
                sendMessage(chatId, Message.invalidPriceMsg);
                return;
            }
            tempProduct.get(chatId).setPrice(Double.parseDouble(text));
            sendMessage(chatId, Message.productImageMsg);
            state.put(chatId, State.CREATE_PRODUCT_IMAGE);
        } else if (currentState == State.CREATE_PRODUCT_IMAGE) {
            if (update.getMessage().hasPhoto()) {
                PhotoSize photo = update.getMessage().getPhoto().stream()
                        .max(Comparator.comparing(PhotoSize::getFileSize))
                        .orElse(null);

                if (photo != null) {
                    String imagePath = saveImage(photo.getFileId());
                    tempProduct.get(chatId).setProductImg(imagePath);

                    sendMessage(chatId, Message.chooseShopMsg);
                    showShops(chatId, true);
                    state.put(chatId, State.CHOOSE_SHOP);
                } else {
                    sendMessage(chatId, "❌ Rasmni yuklashda xatolik. Qaytadan yuboring:");
                }
            } else {
                sendMessage(chatId, "❌ Mahsulot rasmi bo‘sh bo‘lmasligi kerak! Qayta kiriting:");
            }
        } else if (currentState == State.CHOOSE_SHOP) {

            Product product = tempProduct.get(chatId);
            List<Shop> shopsByOwnerId = getShopsByOwnerId(chatId);
            boolean isValidShop = shopsByOwnerId.stream()
                    .anyMatch(shop -> shop.getName().equals(text));
            if (!isValidShop) {
                sendMessage(chatId, "❌ Noto‘g‘ri do‘kon! Qayta tanlang:");
                return;
            }
            shopsByOwnerId.stream()
                    .filter(shop -> shop.getName().equals(text))
                    .findFirst()
                    .ifPresent(shop -> shop.getProductIds().add(product.getId()));

            product.setShopId(text);
            products.put(product.getId(), product);
            tempProduct.remove(chatId);
            sendMessage(chatId, Message.productAddedMsg);


            state.put(chatId, State.SELLER_MAIN);
        }

    }


    private void showShops(Long chatId, boolean isForAddProduct) {
        StringBuilder sb = new StringBuilder();
        sb.append("📢 Dokon:\n\n");

        List<Shop> shopsByOwnerId = getShopsByOwnerId(chatId);

        if (shopsByOwnerId.isEmpty()) {
            sendMessage(chatId, Message.noProductsMsg);
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
                    .append(". 🏪 Do'kon nomi: ")
                    .append(shop.getName())
                    .append("\n")
                    .append("   ⭐️ Reyting: ")
                    .append(shop.getRating())
                    .append("  |  📦Mahsulotlar soni: ")
                    .append(shop.getProductIds().size())
                    .append("\n\n");

            rowButtons.add(String.valueOf(i + 1));
            rowData.add(isForAddProduct ? "shopIdForProduct:" + shop.getId() : "shopId:" + shop.getId());

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
