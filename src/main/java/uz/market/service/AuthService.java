package uz.market.service;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.market.bot.MainBot;
import uz.market.entity.Buyer;
import uz.market.entity.Seller;
import uz.market.entity.enums.Role;
import uz.market.entity.enums.State;
import uz.market.util.Button;
import uz.market.util.Message;
import uz.market.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static uz.market.db.Datasource.*;

public class AuthService extends MainBot {
    public void service(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        User from = update.getMessage().getFrom();

        if (isRegister(chatId, Role.SELLER)) {
            new SellerService().service(update);
        } else if (isRegister(chatId, Role.BUYER)) {
            new BuyerService().service(update);
        } else {
            State currentState = state.getOrDefault(chatId, State.AUTHORIZATION);

            switch (currentState) {
                case AUTHORIZATION -> {
                    switch (text) {
                        case "/start" -> sendMessage(chatId, Message.authStart, keyboard(Utils.sellerOrBuyerKeyboard));

                        case Button.sellerButton -> {
                            Seller seller = new Seller();
                            seller.setUserId(chatId);
                            seller.setUserName(from.getUserName() != null ? from.getUserName() : "Nomalum");
                            seller.setFullName(from.getFirstName() + " " + (from.getLastName() != null ? from.getLastName() : ""));
                            seller.setRole(Role.SELLER);
                            seller.setBalance(0.0d);
                            seller.setIsRegister(false);
                            seller.setShops(new ArrayList<>());
                            users.put(chatId, seller);

                            sendMessage(chatId, Message.phoneRequest, createPhoneButton());
                            state.put(chatId, State.PHONE_NUMBER_REQUEST);
                        }
                        case Button.buyerButton -> {
                            Buyer buyer = new Buyer();
                            buyer.setUserId(chatId);
                            buyer.setUserName(from.getUserName() != null ? from.getUserName() : "Nomalum");
                            buyer.setFullName(from.getFirstName() + " " + (from.getLastName() != null ? from.getLastName() : ""));
                            buyer.setRole(Role.BUYER);
                            buyer.setBalance(0.0d);
                            buyer.setIsRegister(false);
                            buyer.setOrders(new ArrayList<>());
                            users.put(chatId, buyer);

                            sendMessage(chatId, Message.phoneRequest, createPhoneButton());
                            state.put(chatId, State.PHONE_NUMBER_REQUEST);
                        }
                        default ->
                                sendMessage(chatId, "Iltomos kerakli roleni tanlang:", keyboard(Utils.sellerOrBuyerKeyboard));
                    }
                }
                case PHONE_NUMBER_REQUEST -> {
                    if (update.getMessage().hasContact()) {
                        String phoneNumber = update.getMessage().getContact().getPhoneNumber();
                        users.get(chatId).setPhoneNumber(phoneNumber);
                        users.get(chatId).setIsRegister(true);
                        Role role = users.get(chatId).getRole();

                        sendMessage(chatId, Message.successRegisterMsg, keyboard(role == Role.BUYER ? Utils.buyerMainKeyboard : Utils.sellerMainKeyboard));
                        state.remove(chatId);
                    } else {
                        sendMessage(chatId, Message.phoneNumberError);
                    }
                }
            }
        }
    }

    public boolean isRegister(Long chatId, Role role) {
        uz.market.entity.User user = users.get(chatId);

        return user != null && user.getPhoneNumber() != null && user.getRole() == role;
    }


    private ReplyKeyboardMarkup createPhoneButton() {
        KeyboardRow row = new KeyboardRow();
        KeyboardButton phoneButton = new KeyboardButton("Telefon raqamini yuborish");
        phoneButton.setRequestContact(true);
        row.add(phoneButton);

        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(row);

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(true);
        keyboard.setKeyboard(rows);
        return keyboard;
    }

}