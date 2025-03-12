package uz.market.db;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.market.entity.History;
import uz.market.entity.User;
import uz.market.entity.enums.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Datasource {
    public static Map<Long, User> users = new HashMap<>();
    public static Map<Long, State> state = new HashMap<>();
    public static Map<Long, History> history = new HashMap<>();

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
