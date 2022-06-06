package com.example.demo.interfaces;

import com.example.demo.response.CurrencyRateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "${app.feign.config.currency-rates.name}", url = "${app.feign.config.currency-rates.url}")
public interface CurrencyRatesApiClient {

    @GetMapping("historical/{date}?app_id={app_id}&base={base}&symbols={symbols}")
    CurrencyRateResponse getCurrencyRate(@PathVariable("date") String date, @RequestParam("app_id") String app_id, @RequestParam("base") String base, @RequestParam("symbols") String symbols);
}
