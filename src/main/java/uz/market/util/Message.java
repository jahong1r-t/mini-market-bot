package uz.market.util;

public interface Message {
    ///Auth service messages
    String authStart = "Assalomu alaykum! \uD83D\uDE0A Bizning mini market botimizga xush kelibsiz. Bu yerda siz qulay narxlarda turli mahsulotlarni topishingiz va osongina buyurtma berishingiz mumkin!";
    String phoneRequest = "Iltimos telefon raqamingizni kiring";
    String successRegisterMsg = "Ro‘yxatdan o‘tish muvaffaqiyatli yakunlandi!";
    String phoneNumberError = "Iltimos, telefon raqamingizni tugma orqali yuboring!";

    ///Seller service messages
    String sellerMainPanelMsg = "👋 Assalomu alaykum. Ho'sh bugun nima qilamiz";
    String shopCreateMsg = "Boshladik! Avvalo, do‘koningiz nomini yuboring.";
    String noShopsMsg = "Afsus sizda hozircha do'kon mavjud emas";
    String noProductsMsg = "Afsus bu do'konda mahsulotlar mavjud emas";
    String productCreateMsg = "📦 Mahsulot nomini kiriting: ";
    String productPriceMsg = "💰 Mahsulot narxini kiriting: ";
    String productImageMsg = "Mahsulot uchun rasm kiriting:";
    String chooseShopMsg = "🛍 Qaysi do‘konga mahsulot qo‘shmoqchisiz?";
    String productAddedMsg = "✅ Mahsulot muvaffaqiyatli qo‘shildi!";
    String invalidPriceMsg = "❌ Iltimos, to'g'ri qiymat kiriting!";

    ///Buyer service messages
    String buyerMainPanelMsg = "👋 Assalomu alaykum. Ho'sh bugun nima sotib olamiz";
}
