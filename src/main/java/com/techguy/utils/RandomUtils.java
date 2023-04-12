package com.techguy.utils;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class RandomUtils {

    private static final Logger log = LoggerFactory.getLogger(RandomUtils.class);

    private static final String ALPHABETIC_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMERIC_CHARS = "0123456789";
    private static final String DEFAULT_CHARS = ALPHABETIC_CHARS + NUMERIC_CHARS;

    /**
     * An instance of secure random to ensure randomness is secure.
     */
    private static final SecureRandom RANDOMIZER = SecureRandomUtils.getNativeInstance();

    public static String randomNum(int length) {
        return random(length, NUMERIC_CHARS);
    }


    public static String randomAlph(int length) {
        return random(length, DEFAULT_CHARS);
    }

    private static String random(int length, char[] chars) {
        if (chars == null || chars.length == 0) {
            throw new IllegalArgumentException("未指定字符全集,无法生成随机字符串");
        }
        return random(length, new String(chars));
    }

    private static String random(int length, String chars) {
        if (length <= 0) {
            throw new IllegalArgumentException("无效的随机字符串长度: " + length);
        }
        if (!StringUtils.hasText(chars)) {
            throw new IllegalArgumentException("未指定字符全集,无法生成随机字符串");
        }
        return IntStream.range(0, length)
                .map(i -> RANDOMIZER.nextInt(chars.length()))
                .mapToObj(randomInt -> chars.substring(randomInt, randomInt + 1))
                .collect(Collectors.joining());
    }


    public static String randomBase64UrlSafedString(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("无效的随机字符串长度: " + size);
        }
        return Base64.encodeBase64URLSafeString(randomStringBytes(size));
    }

    public static byte[] randomStringBytes(int size) {
        final byte[] randomBytes = new byte[size];
        RANDOMIZER.nextBytes(randomBytes);
        return randomBytes;
    }
}
