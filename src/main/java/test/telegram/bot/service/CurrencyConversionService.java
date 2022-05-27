package test.telegram.bot.service;

import test.telegram.bot.entity.CurrencyEnum;

public interface CurrencyConversionService {

    double getConversionRatio(CurrencyEnum originalCurrency, CurrencyEnum targetCurrency);
}
