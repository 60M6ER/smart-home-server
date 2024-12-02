package ru.bomber.smarthomeserver.services.telegram.command;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.bomber.smarthomeserver.services.telegram.TelegramConstants;
import ru.bomber.smarthomeserver.model.User;
import ru.bomber.smarthomeserver.repository.UserRepository;
import ru.bomber.smarthomeserver.services.TelegramService;

import java.util.Optional;

@Component
public class ChangePasswordCommand extends BaseCommandHandler implements CommandService{

    private static final String OLD_PASSWORD_MESSAGE =
            """
                    Для смены пароля введите дейтсвующий пароль:
                    """;

    private static final String BAD_OLD_PASSWORD_MESSAGE =
            """
                    Вы ввели не верный пароль.
                    Попробуйте еще раз:
                    """;

    private static final String TRUE_OLD_PASSWORD_MESSAGE =
            """
                    Введен верный пароль.
                    Теперь введите новый пароль:
                    """;

    private static final String CONFIRM_NEW_PASSWORD_MESSAGE =
            """
                    Повторите ввод нового пароля:
                    """;

    private static final String BAD_CONFIRM_NEW_PASSWORD_MESSAGE =
            """
                    Пароли не совпадают. Введите снова новый пароль:
                    """;

    private static final String FINISH_NEW_PASSWORD_MESSAGE =
            """
                    Пароль изменен.
                    Удалите сообщения с паролями для безопастности.
                    """;

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private String newPassword;

    @Autowired
    public ChangePasswordCommand(TelegramService tService, BCryptPasswordEncoder passwordEncoder, UserRepository userRepository) {
        super(tService);
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @PostConstruct
    private void registerCommand() {
        tService.registerCommand(this);
    }

    @Override
    public void handCommand(String[] args, Long chatId, Update update, Optional<User> user) {
        if (user.isEmpty()) {
            tService.sendTextMessage(TelegramConstants.UNAUTHORIZED_MESSAGE, chatId);
        }else {
            User fUser = user.get();
            switch (step) {
                case 0:
                    tService.addListener(chatId, this);
                    tService.sendTextMessage(OLD_PASSWORD_MESSAGE, chatId);
                    step++;
                break;
                case 1:
                    if (passwordEncoder.matches(args[0], fUser.getPassword())) {
                        step++;
                        tService.sendTextMessage(TRUE_OLD_PASSWORD_MESSAGE, chatId);
                    } else {
                        tService.sendTextMessage(BAD_OLD_PASSWORD_MESSAGE, chatId);
                    }
                    break;
                case 2:
                    step++;
                    newPassword = args[0];
                    tService.sendTextMessage(CONFIRM_NEW_PASSWORD_MESSAGE, chatId);
                    break;
                case 3:
                    if (newPassword.equals(args[0])) {
                        tService.delListener(chatId);
                        fUser.setPassword(passwordEncoder.encode(newPassword));
                        userRepository.save(fUser);
                        tService.sendTextMessage(FINISH_NEW_PASSWORD_MESSAGE, chatId);
                    }else {
                        step--;
                        tService.sendTextMessage(BAD_CONFIRM_NEW_PASSWORD_MESSAGE, chatId);
                    }
                    break;
            }
        }
    }

    @Override
    public boolean isThisCommand(String text) {
        return text.equals("/change_password");
    }

    @Override
    public CommandHandler getInstance() {
        return new ChangePasswordCommand(tService, passwordEncoder, userRepository);
    }
}
