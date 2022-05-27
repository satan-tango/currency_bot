package test.telegram.bot.service.impl;

import test.telegram.bot.entity.CurrencyEnum;
import test.telegram.bot.service.CurrencyModeService;

import java.util.HashMap;
import java.util.Map;

public class CurrencyModeServiceImpl implements CurrencyModeService {

    private final Map<Long, CurrencyEnum> originalCurrency = new HashMap<>();
    private final Map<Long, CurrencyEnum> targetCurrency = new HashMap<>();

    @Override

    public CurrencyEnum getOriginalCurrency(long chatId) {
        return originalCurrency.getOrDefault(chatId, CurrencyEnum.USD);
    }

    @Override
    public CurrencyEnum getTargetCurrency(long chatId) {
        return targetCurrency.getOrDefault(chatId, CurrencyEnum.USD);
    }

    @Override
    public void setOriginalCurrency(long chatId, CurrencyEnum currency) {
        originalCurrency.put(chatId, currency);
    }

    @Override
    public void setTargetCurrency(long chatId, CurrencyEnum currency) {
        targetCurrency.put(chatId, currency);
    }
}
