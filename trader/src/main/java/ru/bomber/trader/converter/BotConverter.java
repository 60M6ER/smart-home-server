package ru.bomber.trader.converter;

import ru.bomber.core.mqtt.trader.models.BotDTO;
import ru.bomber.trader.models.Bot;

public class BotConverter {

    public static BotDTO toDTO(Bot bot) {
        BotDTO dto = new BotDTO();
        dto.setId(bot.getId());
        dto.setName(bot.getName());
        dto.setPair(bot.getPair());
        dto.setBaseInstrument(bot.getBaseInstrument());
        dto.setMinAmount(bot.getMinAmount());
        dto.setAmountAtPercent(bot.getAmountAtPercent());
        dto.setProfit(bot.getProfit());
        dto.setStepToBay(bot.getStepToBay());
        dto.setStepAtPercent(bot.getStepAtPercent());
        dto.setEqualSteps(bot.getEqualSteps());
        dto.setStrategyType(bot.getStrategyType());
        dto.setActive(bot.getActive());
        dto.setVendor(bot.getVendor());

        return dto;
    }

    public static Bot fromDTO(BotDTO dto) {
        Bot bot = new Bot();
        bot.setId(dto.getId());
        bot.setName(dto.getName());
        bot.setPair(dto.getPair());
        bot.setBaseInstrument(dto.getBaseInstrument());
        bot.setMinAmount(dto.getMinAmount());
        bot.setAmountAtPercent(dto.getAmountAtPercent());
        bot.setProfit(dto.getProfit());
        bot.setStepToBay(dto.getStepToBay());
        bot.setStepAtPercent(dto.getStepAtPercent());
        bot.setEqualSteps(dto.getEqualSteps());
        bot.setStrategyType(dto.getStrategyType());
        bot.setActive(dto.getActive());
        bot.setVendor(dto.getVendor());

        return bot;
    }
}
