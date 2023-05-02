package ru.larionov.smarthomeserver.services.telegram;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.larionov.smarthomeserver.exeptions.CannotSendMessage;
import ru.larionov.smarthomeserver.services.TelegramService;

@Slf4j
public class TelegramHandler extends TelegramLongPollingBot {

    private final String botUserName;
    private final TelegramService service;

    public TelegramHandler(DefaultBotOptions options, String botUserName, String botToken, TelegramService service) {
        super(options, botToken);
        this.botUserName = botUserName;
        this.service = service;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
                log.info("T_Bot received a message: " + update.getMessage().getText());
                service.onNewUpdate(update.getMessage().getText(),
                        update.getMessage().getChatId(),
                        update);
        } else if (update.hasCallbackQuery()) {
            log.info("T_Bot received a Callback Query: " + update.getCallbackQuery().getData());
            service.onNewUpdate(update.getCallbackQuery().getData(),
                    update.getCallbackQuery().getMessage().getChatId(),
                    update);
        }
    }

    public synchronized void sndTextMsg(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        try {
            execute(sendMessage); // Call method to send the message
            log.info("T_Bot successfully sent the message: ");
        } catch (TelegramApiException e) {
            log.error("T_Bot cannot sent message.", e);
        }
    }

    public synchronized void sendMessage(SendMessage message) throws CannotSendMessage {
        try {
            execute(message);
            log.debug("T_Bot successfully sent the message: ");
        } catch (TelegramApiException e) {
            log.error("T_Bot cannot sent message.", e);
            throw new CannotSendMessage(e);
        }
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public void onRegister() {
        super.onRegister();
        log.info("On register event.");
    }
}
