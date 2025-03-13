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
    //Vaqtinchalik product saqlash uchun (chatId->Product)
    public static Map<Long,Product>tempProduct=new HashMap<>();

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
        List<String> productIds=new ArrayList<>();
        productIds.add("1");
        productIds.add("2");
        productIds.add("3");
        productIds.add("4");
        productIds.add("5");
        shops.put("11",new Shop("11","Flowers",Arrays.asList(4,5,4),6569144041L,productIds));
  }
    static{
        products.put("1",new Product("1","atirgul",30.0,"resources/img.png","11"));
        products.put("2",new Product("2","lola",40.0,"resources/img_1.png","11"));
        products.put("3",new Product("3","orxideya",50.0,"resources/img_2.png","11"));
        products.put("4",new Product("4","nargiz",20.0,"resources/img_3.png","11"));
        products.put("5",new Product("5","lavanda",60.0,"resources/img_4.png","11"));

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
static {
        // 10 ta default mahsulot qo'shish (добавлено quantity)
        products.put("1", new Product("1", "Smartfon", 299.99, "phone.jpg", "shop1", 10));
        products.put("2", new Product("2", "Noutbuk", 799.99, "laptop.jpg", "shop1", 5));
        products.put("3", new Product("3", "Televizor", 499.99, "tv.jpg", "shop2", 8));
        products.put("4", new Product("4", "Naushnik", 59.99, "headphones.jpg", "shop1", 15));
        products.put("5", new Product("5", "Planshet", 199.99, "tablet.jpg", "shop3", 7));
        products.put("6", new Product("6", "Aqlli soat", 129.99, "smartwatch.jpg", "shop2", 12));
        products.put("7", new Product("7", "Kamera", 349.99, "camera.jpg", "shop1", 4));
        products.put("8", new Product("8", "O'yin konsoli", 399.99, "console.jpg", "shop3", 6));
        products.put("9", new Product("9", "Printer", 149.99, "printer.jpg", "shop2", 9));
        products.put("10", new Product("10", "Klaviatura", 39.99, "keyboard.jpg", "shop1", 20));
    }


    public static List<Order> getOrdersByBuyerId(Long buyerId){
        return orders.values().stream()
                .filter(order -> order.getBuyerId().equals(buyerId))
                .toList();


    }

}
