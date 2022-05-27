package test.telegram.bot.service;

import test.telegram.bot.entity.CurrencyEnum;

public interface CurrencyModeService {

    CurrencyEnum getOriginalCurrency(long chatId);

    CurrencyEnum getTargetCurrency(long chatId);

    void setOriginalCurrency(long chatId, CurrencyEnum currency);

    void setTargetCurrency(long chatId, CurrencyEnum currency);
}
