package uz.market.db;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.market.entity.*;
import uz.market.entity.enums.State;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Datasource {

    // Foydalanuvchilar (chatId -> User)
    public static Map<Long, User> users = new ConcurrentHashMap<>();

    // Do‘konlar (shopId -> Shop)
    public static Map<String, Shop> shops = new LinkedHashMap<>();

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


    static {
        shops.put("1", new Shop("1", "Super Market", Arrays.asList(4, 5, 3), 5699941692L, new ArrayList<>()));
        shops.put("2", new Shop("2", "Tech Store", Arrays.asList(5, 5, 4), 5699941692L, new ArrayList<>()));
        shops.put("3", new Shop("3", "Fashion Hub", Arrays.asList(3, 4, 5), 5699941692L, new ArrayList<>()));
        shops.put("4", new Shop("4", "Food Corner", Arrays.asList(4, 4, 4), 5699941692L, new ArrayList<>()));
        shops.put("5", new Shop("5", "Gadget Zone", Arrays.asList(5, 3, 5), 5699941692L, new ArrayList<>()));
        shops.put("6", new Shop("6", "Book World", Arrays.asList(4, 5, 5), 5699941692L, new ArrayList<>()));
        shops.put("7", new Shop("7", "Home Decor", Arrays.asList(3, 3, 4), 5699941692L, new ArrayList<>()));
        shops.put("8", new Shop("8", "Sport Shop", Arrays.asList(5, 4, 5), 5699941692L, new ArrayList<>()));
        shops.put("9", new Shop("9", "Toy Land", Arrays.asList(4, 4, 5), 5699941692L, new ArrayList<>()));
        shops.put("10", new Shop("10", "Beauty Store", Arrays.asList(5, 5, 3), 5699941692L, new ArrayList<>()));
  }

    // usersdan seller ni topib beradi
    public static Seller getSeller(Long userId) {
        User user = Datasource.users.get(userId);
        if (user instanceof Seller) {
            return (Seller) user;
        }
        return null;
    }


    public static List<Shop> getShopsByOwnerId(Long ownerId) {
        return shops.values().stream()
                .filter(shop -> shop.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
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
