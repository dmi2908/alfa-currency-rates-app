package com.example.demo;

import com.example.demo.controller.CurrencyRatesController;
import com.example.demo.interfaces.CurrencyRatesApiClient;
import com.example.demo.interfaces.GifsApiClient;
import com.example.demo.response.CurrencyRateResponse;
import com.example.demo.response.GifsResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.Calendar;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
@AutoConfigureMockMvc
public class CurrencyRatesControllerTest {
    @Autowired
    private CurrencyRatesController currencyRatesController;

    @MockBean
    private CurrencyRatesApiClient currencyApiClient;

    @MockBean
    private GifsApiClient gifsApiClient;

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

    @Test
    @DisplayName("Check rate not changed")
    void getCurrencyNotChanged() {

        Calendar today = Calendar.getInstance();
        Calendar dayBefore = Calendar.getInstance();
        dayBefore.add(Calendar.DATE, -1);

        CurrencyRateResponse crrToday  = getTestCurrencyRateResponse("EUR", 1.45);
        CurrencyRateResponse crrYesterday = getTestCurrencyRateResponse("EUR", 1.45);

        Mockito.doReturn(crrToday).when(currencyApiClient).getCurrencyRate(currencyRatesController.getDateParam(today),app_id, base, "EUR");
        Mockito.doReturn(crrYesterday).when(currencyApiClient).getCurrencyRate(currencyRatesController.getDateParam(dayBefore),app_id, base, "EUR");

        Assertions.assertEquals("Currency exchange rate not changed", currencyRatesController.getCurrency("EUR"));
    }

    @Test
    @DisplayName("Check rate increased")
    void getCurrencyIncrease() {

        Calendar today = Calendar.getInstance();
        Calendar dayBefore = Calendar.getInstance();
        dayBefore.add(Calendar.DATE, -1);

        CurrencyRateResponse crrToday  = getTestCurrencyRateResponse("EUR", 1.45);
        CurrencyRateResponse crrYesterday = getTestCurrencyRateResponse("EUR", 1.25);
        GifsResponse gr = getTestGifsResponse();

        Mockito.doReturn(crrToday).when(currencyApiClient).getCurrencyRate(currencyRatesController.getDateParam(today),app_id, base, "EUR");
        Mockito.doReturn(crrYesterday).when(currencyApiClient).getCurrencyRate(currencyRatesController.getDateParam(dayBefore),app_id, base, "EUR");
        Mockito.when(gifsApiClient.getGif(eq(api_key), eq(rich), anyInt())).thenReturn(gr);

        Assertions.assertEquals(true, currencyRatesController.getCurrency("EUR").contains("<title>" + rich + "</title>"));
    }

    @Test
    @DisplayName("Check rate decreased")
    void getCurrencyDecrease() {

        Calendar today = Calendar.getInstance();
        Calendar dayBefore = Calendar.getInstance();
        dayBefore.add(Calendar.DATE, -1);

        CurrencyRateResponse crrToday  = getTestCurrencyRateResponse("EUR", 1.25);
        CurrencyRateResponse crrYesterday  = getTestCurrencyRateResponse("EUR", 1.45);
        GifsResponse gr = getTestGifsResponse();

        Mockito.doReturn(crrToday).when(currencyApiClient).getCurrencyRate(currencyRatesController.getDateParam(today),app_id, base, "EUR");
        Mockito.doReturn(crrYesterday).when(currencyApiClient).getCurrencyRate(currencyRatesController.getDateParam(dayBefore),app_id, base, "EUR");
        Mockito.when(gifsApiClient.getGif(eq(api_key), eq(broke), anyInt())).thenReturn(gr);

        Assertions.assertEquals(true, currencyRatesController.getCurrency("EUR").contains("<title>" + broke + "</title>"));
    }

    @Test
    @DisplayName("Check exception occured")
    void getSomeException() {

        Calendar today = Calendar.getInstance();
        Calendar dayBefore = Calendar.getInstance();
        dayBefore.add(Calendar.DATE, -1);

        CurrencyRateResponse crrToday  = getTestCurrencyRateResponse("EUR", 1.25);
        CurrencyRateResponse crrYesterday  = getTestCurrencyRateResponse("EUR", 1.45);
        GifsResponse gr = getTestGifsResponse();

        Mockito.doReturn(crrToday).when(currencyApiClient).getCurrencyRate(currencyRatesController.getDateParam(today),app_id, base, "EUR");
        Mockito.doReturn(crrYesterday).when(currencyApiClient).getCurrencyRate(currencyRatesController.getDateParam(dayBefore),app_id, base, "EUR");
        Mockito.when(gifsApiClient.getGif(eq(api_key), eq(rich), anyInt())).thenReturn(gr);

        Assertions.assertEquals(true, currencyRatesController.getCurrency("EUR").contains("An error occured: "));
    }

    private CurrencyRateResponse getTestCurrencyRateResponse(String currency, double value) {
        Map<String, Double> currencyMap = new LinkedCaseInsensitiveMap<>();
        currencyMap.put(currency, value);
        CurrencyRateResponse crr  = new CurrencyRateResponse();
        crr.rates = currencyMap;
        return crr;
    }

    private GifsResponse getTestGifsResponse() {
        Map<String, Map<String, Map<String, String>>> gefResponseMap = new LinkedCaseInsensitiveMap<>();
        Map<String, Map<String, String>> originalMap = new LinkedCaseInsensitiveMap<>();
        Map<String, String> urlMap = new LinkedCaseInsensitiveMap<>();
        urlMap.put("url" , "http://test.url");
        originalMap.put("original", urlMap);
        gefResponseMap.put("images", originalMap);

        GifsResponse gr = new GifsResponse();
        gr.data = new Map[1];
        gr.data[0] = gefResponseMap;
        return gr;
    }
}
