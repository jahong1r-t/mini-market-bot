package uz.market.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.market.service.AuthService;
import uz.market.util.Bot;

import java.io.File;

public class MainBot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            new AuthService().service(update);
        }
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

    @Override
    public String getBotUsername() {
        return Bot.USERNAME;
    }

    @Override
    public String getBotToken() {
        return Bot.TOKEN;
    }
}
