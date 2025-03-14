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

    public static Map<Long, History> histories = new ConcurrentHashMap<>();

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

    //Vaqtinchalik product saqlash uchun (chatId->Product)
    public static Map<Long, Product> tempProduct = new HashMap<>();

    // usersdan buyer ni topib beradi
    public static Buyer getBuyer(Long userId) {
        User user = Datasource.users.get(userId);
        if (user instanceof Buyer) {
            return (Buyer) user;
        }
        return null;
    }


    static {
        products.put("1", new Product("1", "Atirgul", 30.0, "src/main/resources3f7af495-6531-47fc-ae03-c6d3ab7375a3.jpg", "1", 4));
        products.put("2", new Product("2", "Lola", 40.0, "src/main/resources3f7af495-6531-47fc-ae03-c6d3ab7375a3.jpg", "1", 5));
        products.put("3", new Product("3", "Orxideya", 50.0, "src/main/resources3f7af495-6531-47fc-ae03-c6d3ab7375a3.jpg", "1", 2));
        products.put("4", new Product("4", "Nargiz", 20.0, "src/main/resources3f7af495-6531-47fc-ae03-c6d3ab7375a3.jpg", "1", 1));
        products.put("5", new Product("5", "Lavanda", 60.0, "src/main/resources3f7af495-6531-47fc-ae03-c6d3ab7375a3.jpg", "1", 1));


        List<String> productIds = new ArrayList<>();
        productIds.add("1");
        productIds.add("2");
        productIds.add("3");
        productIds.add("4");
        productIds.add("6");
        productIds.add("7");
        productIds.add("8");
        productIds.add("9");
        productIds.add("10");
        shops.put("1", new Shop("1", "Flowers", Arrays.asList(4, 5, 4), 5699941692L, productIds));
    }


    // usersdan seller ni topib beradi
    public static Seller getSeller(Long userId) {
        User user = Datasource.users.get(userId);
        if (user instanceof Seller) {
            return (Seller) user;
        }
        return null;
    }

    public static List<Product> getAvailableProducts() {
        return new ArrayList<>(products.values());
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

    static {
        // 10 ta default mahsulot qo'shish (добавлено quantity)
        products.put("6", new Product("6", "Noutbuk", 799.99, "src/main/resources3f7af495-6531-47fc-ae03-c6d3ab7375a3.jpg", "1", 5));
        products.put("7", new Product("7", "Televizor", 499.99, "src/main/resources3f7af495-6531-47fc-ae03-c6d3ab7375a3.jpg", "1", 8));
        products.put("8", new Product("8", "Naushnik", 59.99, "src/main/resources3f7af495-6531-47fc-ae03-c6d3ab7375a3.jpg", "1", 15));
        products.put("9", new Product("9", "Planshet", 199.99, "src/main/resources3f7af495-6531-47fc-ae03-c6d3ab7375a3.jpg", "1", 7));
        products.put("10", new Product("10", "Aqlli soat", 129.99, "src/main/resources3f7af495-6531-47fc-ae03-c6d3ab7375a3.jpg", "1", 12));
    }


    public static List<Order> getOrdersByBuyerId(Long buyerId) {
        return orders.values().stream()
                .filter(order -> order.getBuyerId().equals(buyerId))
                .toList();
    }
}
