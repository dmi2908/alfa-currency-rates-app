package com.example.demo.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
public class GifsResponse {
    public Map[] data;
}
