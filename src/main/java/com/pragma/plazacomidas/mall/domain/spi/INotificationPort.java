package com.pragma.plazacomidas.mall.domain.spi;

public interface INotificationPort {

    void sendOrderReadySms(String phone, String securityPin);
}
