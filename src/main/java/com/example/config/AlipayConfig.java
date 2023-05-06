package com.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "alipay")
public class AlipayConfig {

    //自己的appId
    public static String appId = "2021000122602661";

    //支付宝默认网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    //设置内网穿透回调地址
    public static String callBack = "computerstore.natapp1.cc";

    //使用的编码格式
    public static String charset = "utf-8";

    //数据格式
    private static String format = "JSON";

    //推荐使用这个秘钥
    public static String signType = "RSA2";

    //异步回调地址
    public static String notifyUrl = "http://" + callBack + "/alipay/notifyNotice";

    //同步回调地址
    public static String returnUrl = "http://" + callBack + "/alipay/returnNotice";

    //应用私有秘钥
    public static String appPrivateKey = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDjknFKlqj6fwVZodKhqYew366TAUfAEF2AOAAmkyUOtMMlS6sG2WSw+onBWsW0zpsqiICdFX0DSPKoNCY39xs4JhL/" +
            "yMugfrRUrfEWTWZnwCmEN5wkAiYO4DitIjzIUthVX7o7FPFP2ZDjx1qHSfG5uBpCBN2bUzJvxOwDZc4MyxOKvO57J/kIGcTFkE/gJbIuJBFKIGmfzFpQ/CsZXwY1U2FkJpmCS4Fn8l1fyNg4lVaMD0uyPedlp/" +
            "jIXSvumWnsrz4j64hlatote5ZFoVm5kdBmjzTyw22KkA3+JjB4hG0Np5OxLT7sjRV9OSJuj8BnMEpFzon75XfBzpWPyI8PAgMBAAECggEAXCB1sq9pOvdfEBkdrwp1fxoejy6OgGdfor+I+BTLhLeS5K9MTlq" +
            "Fk8vpJph7vyWA1WgrQOxpDoJikcuCyVdE8qcfO+Nq489Ha5x7I/8zfL7jT74/fKhOBempqBVbK/G12GIHf1GMGzpFmKCkum4p6HhqUN2tZOr6WRsMPJfjIXP1B2g5SLXiSkUmX/Zqy7pIlCyXZ8uFu4kSzrgLUf6d+/" +
            "ZBYbGO++YK49F/syPy3Xb80GyZiyR/dqox7svFBEJ+RKec+SnJXT7loL1D/uDk2M3LD1m3vZUszz0r9tSIiRbZikbKKO+9U1mvHO0/GXGzwKCxtYQYPybXbYho+OvfQQKBgQD+EGXx4UfSZxyRqrRAUufr13Jdcxkzq" +
            "6uYSbWG6uTe6IP0OKIC0WnQ3Ve9Brqx34sg6RHvwt4A21xt48c8OnA1id37Pl25f6bCTVoQqc3OcoH4YjaHJ/7jy/D6FbU6icmg3v0VobMUA2RwE0fHLAQX1Te6bzP3vdG3TXDC3AbYLwKBgQDlTl3PxaRp0tnvhYhFN/" +
            "PF3v7cVV1JApCbW1wK6j/B5t2FPD2JLYTFWxkIub4XW9lkX3YbrytOog3v3ue+PgM4gTaUkZl6fyLAhpuTZ88lj07fe+aYyHjYyTTlsFYuUNj5T1MxYbyW5JNLBmFJId2xcbWwv9L95R8Qh1uDFZcfIQKBgBVjMgHLsi0fK" +
            "wOEOEpV8FolN+ZwAwYbhIV8ZwClk4DEWB9eCR1tnZfMmk86WssQg3jQxgbGA8RUsxI5I5OJY9rN8VbuKBuuiE5BDrzqXn3rm89aDTcRgd6RU4bp/uifos6dFvcUNqwPoQviQ5+br/iFpXxXUiuf2iCxLzXoKddXAoGAe3fu22" +
            "k4ViLKZkYoUd0GzYx8/naYfC99XambreTj36xHqu381FhGHV4n5KLe0AUM0Rb1qYJwdy6bAoWrkqwhX9NzaBvd4C+ErtXL/1ruFvNLLNqyF6IaL+BnMyX3wYiWy+cCjJxrx3PcO5F74LzzehAYHMus+DjeuS+o/" +
            "rQpYwECgYALIvrqIXbYQob2nCIOFM3pk7YbHw/F2xrQcxkWlCN0kosaDxwQuCU3gse7VAQ92NWXsqO60lyRR0nuEvGDW7hsIWP5DwSryq313cJ+cznOI4Q15M+TNn5qUJekUptRrTw1F4Err0GQrajX9y6/Dpn62qL5ymFS5sua2DK+kyp5Ng==";

    //支付宝公钥
    public static String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAj63FTZRa+qGiPG6cgUu5mvkVgSiG7pXfqGuD4ElnsvO/" +
            "G00DyhdqFR5KJRZN1rAWeSt1OwPEfRJyf1TkszozAvRG1PngPmQAjfKqLCSXIDUUClsuwvjLQ6T/+MbOoN0vylPP1FlbBkaxw5x2SAEHSj/nqVyxWGZNLGnc3RIMbmQSzugvD+F9taqrTH9kdWlO/" +
            "uRFE2GhhnNZpwWiEsHoKNx8wucTNI5uFNEGk51khdtFxqcoHS+EqQEnyt84LHHkCHHdq7xE1jYfbVqHnkAxAVrceVFwI7VvJ+f8rOJ4jtZoNilW+zD6Py0ylnd6BtTv2GIDqnfuf+7pqMSNwL1GXQIDAQAB";


}
