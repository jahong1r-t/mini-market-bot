package uz.market.db;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.market.entity.*;
import uz.market.entity.enums.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Datasource {

    // Foydalanuvchilar (chatId -> User)
    public static Map<Long, User> users = new ConcurrentHashMap<>();

    // Do‘konlar (shopId -> Shop)
    public static Map<String, Shop> shops = new ConcurrentHashMap<>();

    // Mahsulotlar (productId -> Product)
    public static Map<String, Product> products = new ConcurrentHashMap<>();

    // Buyurtmalar (orderId -> Order)
    public static Map<String, Order> orders = new ConcurrentHashMap<>();

    // Savatlar (basketId -> Basket)
    public static Map<String, Basket> baskets = new ConcurrentHashMap<>();

    // Foydalanuvchi holatlari (chatId -> State)
    public static Map<Long, State> state = new ConcurrentHashMap<>();

    // usersdan buyer ni topib beradi
    public static Buyer getBuyer(Long userId) {
        User user = Datasource.users.get(userId);
        if (user instanceof Buyer) {
            return (Buyer) user;
        }
        return null;
    }


    // usersdan seller ni topib beradi
    public static Seller getSeller(Long userId) {
        User user = Datasource.users.get(userId);
        if (user instanceof Seller) {
            return (Seller) user;
        }
        return null;
    }

    public static ReplyKeyboardMarkup keyboard(String[][] buttons) {
        List<KeyboardRow> rows = new ArrayList<>();

        for (String[] button : buttons) {
            KeyboardRow keyboardRow = new KeyboardRow();
            for (String s : button) {
                keyboardRow.add(s);
            }
            rows.add(keyboardRow);
        }
        ReplyKeyboardMarkup reply = new ReplyKeyboardMarkup();
        reply.setResizeKeyboard(true);
        reply.setKeyboard(rows);

        return reply;
    }

    public static ReplyKeyboard inlineKeyboard(String[][] buttons, String[][] data) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (int i = 0; i < buttons.length; i++) {
            List<InlineKeyboardButton> buttonRow = new ArrayList<>();
            for (int j = 0; j < buttons[i].length; j++) {
                buttonRow.add(InlineKeyboardButton.builder().callbackData(data[i][j]).text(buttons[i][j]).build());
            }
            rows.add(buttonRow);
        }

        markup.setKeyboard(rows);

        return markup;
    }
}
