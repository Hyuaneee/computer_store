package com.example.pojo;

import lombok.Data;

@Data
public class AliPay {
    private String traceNo; //我们的订单号
    private Double totalAmount; //总金额
    private String subject; //主题
    private String alipayTraceNo;//阿里的流水号
}
