package com.example.util;

import java.util.UUID;

/**
 * @author huangjy
 * @version 1.0
 * @date 2023/03/23
 */
public class UUIDUtils {
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
