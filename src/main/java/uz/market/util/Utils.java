package uz.market.util;


import static uz.market.util.Button.*;

public interface Utils {
    String[][] sellerOrBuyerKeyboard = {
            {Button.sellerButton, Button.buyerButton}
    };

    String[][] sellerMainKeyboard = {
            {addProduct, editProduct},
            {addShop, showShops},
            {showHistory},
            {deleteProfile}
    };
    String[][] buyerMainKeyboard = {
            {showShopsBuyer, searchProduct},
            {basket, showHistoryBuyer},
            {balance},
    };
}
