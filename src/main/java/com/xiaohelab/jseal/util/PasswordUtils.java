package com.xiaohelab.jseal.util;

import java.util.regex.Pattern;

/**
 * 密码工具类
 */
public class PasswordUtils {

    /**
     * 密码强度等级
     */
    public enum PasswordStrength {
        WEAK("弱", 1),
        MEDIUM("中", 2),
        STRONG("强", 3),
        VERY_STRONG("非常强", 4);

        private final String displayName;
        private final int level;

        PasswordStrength(String displayName, int level) {
            this.displayName = displayName;
            this.level = level;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getLevel() {
            return level;
        }
    }

    /**
     * 评估密码强度
     */
    public static PasswordStrength evaluateStrength(String password) {
        if (password == null || password.isEmpty()) {
            return PasswordStrength.WEAK;
        }

        int score = 0;

        // 长度评分
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        if (password.length() >= 16) score++;

        // 包含小写字母
        if (Pattern.compile("[a-z]").matcher(password).find()) score++;

        // 包含大写字母
        if (Pattern.compile("[A-Z]").matcher(password).find()) score++;

        // 包含数字
        if (Pattern.compile("[0-9]").matcher(password).find()) score++;

        // 包含特殊字符
        if (Pattern.compile("[!@#$%^&*(),.?\":{}|<>]").matcher(password).find()) score++;

        // 根据分数返回强度
        if (score <= 2) return PasswordStrength.WEAK;
        if (score <= 4) return PasswordStrength.MEDIUM;
        if (score <= 6) return PasswordStrength.STRONG;
        return PasswordStrength.VERY_STRONG;
    }

    /**
     * 生成随机密码
     */
    public static String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder sb = new StringBuilder();
        java.security.SecureRandom random = new java.security.SecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * 掩码显示密码
     */
    public static String mask(String password) {
        if (password == null || password.isEmpty()) {
            return "";
        }
        return "*".repeat(password.length());
    }
}
