package uz.market.bot;

import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.market.entity.*;
import uz.market.entity.enums.Role;
import uz.market.entity.enums.State;
import uz.market.service.AuthService;
import uz.market.util.Bot;
import uz.market.util.Message;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static uz.market.db.Datasource.*;

public class MainBot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            new AuthService().service(update);
        } else if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            Long id = update.getCallbackQuery().getFrom().getId();

            if (data.startsWith("shopId:")) {
                String substring = data.substring(7);
                getProductsByShopId(substring, id);
            } else if (data.startsWith("productId:")) {
                getOneProductItem(data.substring(10), id);
            } else if (data.startsWith("toBasketProduct:")) {
                addToBasket(id, data.substring(16));
            } else if (data.startsWith("editProduct:")) {

            } else if (data.startsWith("shopIdForProduct:")) {
                String shopId = data.replace("shopIdForProduct:", "");
                addProductToShop(shopId, id);
            } else if (data.startsWith("basket:")) {
                String basketId = data.substring(data.indexOf("basket:") + 7, data.indexOf(":price:"));
                String totalPrice = data.substring(data.indexOf(":price:") + 7);
                buyProduct(basketId, id, Double.parseDouble(totalPrice));
            }
        }
    }

    private void buyProduct(String basketId, Long chatId, Double price) {
        if (users.get(chatId).getBalance() < price) {
            sendMessage(chatId, "Afsuski sizda mablag' yetarli emas");
        } else {
            for (String s : baskets.get(basketId).getProductId()) {
                products.get(s).setQuantity(products.get(s).getQuantity() - 1);
            }
            users.get(chatId).setBalance(users.get(chatId).getBalance() - price);
            sendMessage(chatId, "Mahsulotlar sotib olindi");
        }
    }

    private void addProductToShop(String shopId, Long chatId) {
        if (shops.containsKey(shopId) && tempProduct.containsKey(chatId)) {
            Shop selectedShop = shops.get(shopId);
            Product product = tempProduct.get(chatId);
            product.setShopId(shopId);
            selectedShop.getProductIds().add(product.getId());
            products.put(product.getId(), product);

            sendMessage(chatId, "✅ Mahsulot " + selectedShop.getName() + " do‘koniga muvaffaqiyatli qo‘shildi!");
            tempProduct.remove(chatId);
            state.put(chatId, State.SELLER_MAIN);
        } else {
            sendMessage(chatId, "❌ Xatolik: Do‘kon yoki mahsulot topilmadi.");

        }
    }

    private void addToBasket(Long chatId, String productId) {
        String basketId = Objects.requireNonNull(getBuyer(chatId)).getBasketId();
        baskets.get(basketId).getProductId().add(productId);
        sendMessage(chatId, "Mahsulot savatga qo'shildi");
    }

    private void getOneProductItem(String productId, Long chatId) {
        Product product = products.get(productId);
        Shop shop = shops.get(product.getShopId());
        System.out.println(shop);

        String caption = String.format(
                "🛍 %s\n\n" +
                        "💰 Narxi: %.2f so'm\n" +
                        "🏪 Do‘kon: %s",
                product.getName(),
                product.getPrice(),
                shop.getName()
        );

        User user = users.get(chatId);

        sendMessage(chatId, caption, new File(product.getProductImg()), user.getRole() == Role.BUYER ? inlineKeyboard(new String[][]{{"Savatga qo'shish"}}, new String[][]{{"toBasketProduct:" + product.getId()}}) : inlineKeyboard(new String[][]{{"Mahsulotni taxrirlash"}}, new String[][]{{"editProduct:" + product.getId()}}));
    }

    private void getProductsByShopId(String shoId, Long chatId) {
        Shop shop = shops.get(shoId);
        sendMessage(chatId, shop.getId());

        List<String> productIds = shops.get(shoId).getProductIds();

        ArrayList<Product> product = new ArrayList<>();

        for (String id : productIds) {
            if (products.containsKey(id)) {
                product.add(products.get(id));
            }
        }

        if (productIds.isEmpty()) {
            sendMessage(chatId, Message.noProductsMsg);
            return;
        }

        StringBuilder sb = new StringBuilder();

        List<String[]> inlineButtonList = new ArrayList<>();
        List<String[]> dataList = new ArrayList<>();

        List<String> rowButtons = new ArrayList<>();
        List<String> rowData = new ArrayList<>();

        for (int i = 0; i < product.size(); i++) {
            Product p = product.get(i);

            sb.append(i + 1)
                    .append(". 🛍️ ")
                    .append(p.getName())
                    .append("\n")
                    .append("   💰 Narxi: ")
                    .append(p.getPrice())
                    .append(" so‘m\n")
                    .append("   🏪 Do‘kon: ")
                    .append(shops.get(shoId).getName())
                    .append("\n\n");

            rowButtons.add(String.valueOf(i + 1));
            rowData.add("productId:" + p.getId());

            if ((i + 1) % 5 == 0 || i == product.size() - 1) {
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

    /**
     * Bu method telegramda faqat text message jo'natish uchun
     * @param chatId yubormoachi bo'lgan odamni chatIdsi beriladi
     * @param message yuborilmoqchi bo'lgan text String tipida messagega beriladi
     */
    public void sendMessage(Long chatId, String message) {
        try {
            execute(SendMessage.builder().chatId(chatId).text(message).build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Bu method rams jo'natish uchun
     * @param chatId yubormoqchi bo'lgan odamni chatIdsi
     * @param caption rasmni ostda chiqadigan text
     * @param path  ramsni yo'li kiritiladi
     */
    public void sendMessage(Long chatId, String caption, File path) {
        try {
            execute(SendPhoto.builder().chatId(chatId).photo(new InputFile(path)).caption(caption).build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Bu method buttonli keyboard yuborish uchun
     * @param chatId ybormoqchi bo'lgan odamni chatIdsi
     * @param message buttondan oldin yuboriladigan xabar
     * @param replyKeyboard {@link uz.market.db.Datasource} keyboard metodi kiritiladi. Keyboardni ichiga {@link uz.market.util.Utils}
     */
    public void sendMessage(Long chatId, String message, ReplyKeyboard replyKeyboard) {
        try {
            execute(SendMessage.builder().chatId(chatId).replyMarkup(replyKeyboard).text(message).build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Bu method rasm text va tagida inline button chiqarish uchun
     * @param chatId yubormoqchi bo'lgan odam id si
     * @param caption rasmin ostida chiqadigan text
     * @param path ramni yo'li
     * @param replyKeyboard {@link uz.market.db.Datasource} ni ichidagi {@link uz.market.db.Datasource#inlineKeyboard(String[][], String[][])} methodi beriladi. meyhod ichiga esa {@link uz.market.util.Utils} dagi keyboard
     */
    public void sendMessage(Long chatId, String caption, File path, ReplyKeyboard replyKeyboard) {
        try {
            execute(SendPhoto.builder().chatId(chatId).photo(new InputFile(path)).caption(caption).replyMarkup(replyKeyboard).build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    //rasmlarni saqlash uchun
    public String saveImage(String fileId) {
        try {
            GetFile getFile = new GetFile(fileId);
            org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);
            String fileUrl = file.getFileUrl(getBotToken());

            URL url = new URL(fileUrl);
            InputStream inputStream = url.openStream();

            String folderPath = "src/main/resources";
            File directory = new File(folderPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filePath = folderPath + UUID.randomUUID() + ".jpg";
            FileUtils.copyInputStreamToFile(inputStream, new File(filePath));

            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public String getBotUsername() {
        return Bot.USERNAME;
    }

    @Override
    public String getBotToken() {
        return Bot.TOKEN;
    }
}
