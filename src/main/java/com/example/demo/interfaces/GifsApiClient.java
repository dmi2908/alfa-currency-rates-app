package com.example.demo.interfaces;

import com.example.demo.response.GifsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "${app.feign.config.gifs-api.name}", url = "${app.feign.config.gifs-api.url}")
public interface GifsApiClient {

    @GetMapping("?api_key={api_key}&q={q}&limit=1&offset={offset}")
    GifsResponse getGif(@PathVariable("api_key") String api_key, @PathVariable("q") String q, @PathVariable("offset") int offset);
}
