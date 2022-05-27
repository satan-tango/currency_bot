package test.telegram.bot;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import test.telegram.bot.entity.CurrencyEnum;
import test.telegram.bot.service.CurrencyConversionService;
import test.telegram.bot.service.CurrencyModeService;
import test.telegram.bot.service.impl.CurrencyConversionServiceImpl;
import test.telegram.bot.service.impl.CurrencyModeServiceImpl;

import java.util.*;


public class TestBot extends TelegramLongPollingBot {

    private final CurrencyModeService currencyModeService = new CurrencyModeServiceImpl();
    private final CurrencyConversionService currencyConversionService = new CurrencyConversionServiceImpl();

    @Override
    public String getBotUsername() {
        return "@DemoLTTSBot";
    }

    @Override
    public String getBotToken() {
        return "5578991068:AAFzUAtXbh6IEvcDZe_RqN-2GqO3fd1mHn8";
    }


    @SneakyThrows
    public static void main(String[] args) {
        TestBot bot = new TestBot();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handelCallback(update.getCallbackQuery());
        } else if (update.hasMessage()) {
            handleMessage(update.getMessage());
        }
    }

    @SneakyThrows
    private void handelCallback(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        String[] param = callbackQuery.getData().split(":");
        String action = param[0].trim();
        CurrencyEnum currencyEnum = CurrencyEnum.valueOf(param[1].trim());

        switch (action) {
            case "ORIGINAL":
                currencyModeService.setOriginalCurrency(message.getChatId(), currencyEnum);
                break;
            case "TARGET":
                currencyModeService.setTargetCurrency(message.getChatId(), currencyEnum);
                break;
        }

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        CurrencyEnum originalCurrency = currencyModeService.getOriginalCurrency(message.getChatId());
        CurrencyEnum targetCurrency = currencyModeService.getTargetCurrency(message.getChatId());

        for (CurrencyEnum currency : CurrencyEnum.values()) {
            buttons.add(
                    Arrays.asList(
                            InlineKeyboardButton.builder()
                                    .text(getCurrencyButton(originalCurrency, currency))
                                    .callbackData("ORIGINAL : " + currency)
                                    .build(),
                            InlineKeyboardButton.builder()
                                    .text(getCurrencyButton(targetCurrency, currency))
                                    .callbackData("TARGET : " + currency)
                                    .build()

                    )
            );
        }

        execute(EditMessageReplyMarkup.builder()
                .chatId(message.getChatId().toString())
                .messageId(message.getMessageId())
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(buttons)
                        .build())
                .build());
    }

    @SneakyThrows
    private void handleMessage(Message message) {
        if (message.hasText() && message.hasEntities()) {
            Optional<MessageEntity> commandEntity = message.getEntities().stream()
                    .filter(e -> "bot_command".equals(e.getType())).findFirst();

            String command = "";
            if (commandEntity.isPresent()) {
                command = message.getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());

                switch (command) {
                    case "/set_currency":

                        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                        CurrencyEnum originalCurrency = currencyModeService.getOriginalCurrency(message.getChatId());
                        CurrencyEnum targetCurrency = currencyModeService.getTargetCurrency(message.getChatId());

                        for (CurrencyEnum currency : CurrencyEnum.values()) {
                            buttons.add(
                                    Arrays.asList(
                                            InlineKeyboardButton.builder()
                                                    .text(getCurrencyButton(originalCurrency, currency))
                                                    .callbackData("ORIGINAL: " + currency)
                                                    .build(),
                                            InlineKeyboardButton.builder()
                                                    .text(getCurrencyButton(targetCurrency, currency))
                                                    .callbackData("TARGET: " + currency)
                                                    .build()

                                    )
                            );
                        }

                        execute(SendMessage.builder()
                                .chatId(String.valueOf(message.getChatId()))
                                .text("Please choose Original and Target Currency")
                                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                                .build()
                        );

                }
            }

        }

        if (message.hasText()) {
            String messageText = message.getText();
            Optional<Double> value = parseDouble(messageText);
            CurrencyEnum originalCurrency = currencyModeService.getOriginalCurrency(message.getChatId());
            CurrencyEnum targetCurrency = currencyModeService.getTargetCurrency(message.getChatId());
            if (value.isPresent()) {
                double ratio = currencyConversionService.getConversionRatio(originalCurrency, targetCurrency);
                execute(SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text(String.format("%4.2f %s  %4.2f %s",
                                value.get(), originalCurrency.name(), value.get() * ratio, targetCurrency.name()))
                        .build());
            }
        }
    }

    private Optional<Double> parseDouble(String messageText) {
        try {
            return Optional.of(Double.parseDouble(messageText));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private String getCurrencyButton(CurrencyEnum saved, CurrencyEnum current) {
        if (saved == current) {
            return current.name() + "\uD83D\uDC23";
        } else {
            return current.name();
        }
    }
}
