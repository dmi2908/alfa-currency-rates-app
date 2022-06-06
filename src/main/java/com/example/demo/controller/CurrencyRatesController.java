package com.example.demo.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;

import com.example.demo.interfaces.CurrencyRatesApiClient;
import com.example.demo.response.CurrencyRateResponse;
import com.example.demo.interfaces.GifsApiClient;
import com.example.demo.response.GifsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/currency"})
public class CurrencyRatesController {

    @Value("${app.feign.config.currency-rates.app_id}")
    private String app_id;

    @Value("${app.feign.config.currency-rates.base}")
    private String base;

    @Value("${app.feign.config.gifs-api.api_key}")
    private String api_key;

    @Value("${app.feign.config.gifs-api.rich_key}")
    private String rich;

    @Value("${app.feign.config.gifs-api.broke_key}")
    private String broke;
    @Autowired
    private CurrencyRatesApiClient currencyApiClient;
    @Autowired
    private GifsApiClient gifsApiClient;

    @RequestMapping({"/getCurrency/{currency}"})
    public String getCurrency(@PathVariable String currency) {
        Calendar today = Calendar.getInstance();
        Calendar dayBefore = Calendar.getInstance();
        dayBefore.add(Calendar.DATE, -1);
        String gif = "";
        String url = "";
        String keyWord = "";

        try {
            // get actual rate
            CurrencyRateResponse current = getCurrencyRate(getDateParam(today), app_id, base, currency);
            // get yesterday's rate
            CurrencyRateResponse yesterday = getCurrencyRate(getDateParam(dayBefore), app_id, base, currency);
            // check if rate increased return "rich" keyword else return "broke" if equals return "same"
            keyWord = current.rates.get(currency) > yesterday.rates.get(currency) ? rich
                    : current.rates.get(currency).equals(yesterday.rates.get(currency)) ? "same" : broke;

            // parse response from giphy API
            if (!keyWord.equals("same")) {
                GifsResponse gr = getGif(keyWord);
                System.out.println("gr.data[0]::: " + gr.data[0]);
                Map gifResponse = (Map) gr.data[0].get("images");
                gifResponse = (Map) gifResponse.get("original");
                url = (String) gifResponse.get("url");
                gif = "<html><head><title>"+ keyWord +"</title></head><body><img src=\"" + url
                        + "\"></body></html>";
            } else {
                gif = "Currency exchange rate not changed";
            }
        } catch(Exception e) {
            gif = "An error occured: " + '\n' + e.getMessage();
        }

        return gif;
    }

    public static String getDateParam(Calendar dateRequested) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormat.format(dateRequested.getTime()) + ".json";
        return date;
    }

    public CurrencyRateResponse getCurrencyRate(String date, String app_id, String base, String symbols) {
        CurrencyRateResponse cr = this.currencyApiClient.getCurrencyRate(date, app_id, base, symbols);
        return cr;
    }

    public GifsResponse getGif(String keyWord) {
        GifsResponse gif = this.gifsApiClient.getGif(api_key, keyWord, new Random().nextInt(100));
        return gif;
    }
}
