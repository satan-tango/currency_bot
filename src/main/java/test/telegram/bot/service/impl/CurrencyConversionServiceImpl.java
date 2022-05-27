package test.telegram.bot.service.impl;

import lombok.SneakyThrows;
import org.json.JSONObject;
import test.telegram.bot.entity.CurrencyEnum;
import test.telegram.bot.service.CurrencyConversionService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CurrencyConversionServiceImpl implements CurrencyConversionService {

    @Override
    public double getConversionRatio(CurrencyEnum originalCurrency, CurrencyEnum targetCurrency) {
        double originalRate = getRate(originalCurrency);
        double targetRate = getRate(targetCurrency);
        return originalRate / targetRate;
    }

    @SneakyThrows
    private double getRate(CurrencyEnum currency) {
        if (currency.name().equals("BYN")) {
            return 1;
        }

        URL url = new URL("https://www.nbrb.by/api/exrates/rates/" + currency.getId());
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        JSONObject json = new JSONObject(response.toString());
        double rate = json.getDouble("Cur_OfficialRate");
        double scale = json.getDouble("Cur_Scale");
        return rate / scale;
    }
}
