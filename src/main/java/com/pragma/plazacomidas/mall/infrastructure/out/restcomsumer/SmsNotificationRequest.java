package com.pragma.plazacomidas.mall.infrastructure.out.restcomsumer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SmsNotificationRequest {
    private String phone;
    private String message;
}
