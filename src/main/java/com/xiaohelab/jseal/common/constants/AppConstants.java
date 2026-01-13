package com.xiaohelab.jseal.common.constants;

/**
 * 应用常量
 */
public final class AppConstants {

    private AppConstants() {}

    // 应用信息
    public static final String APP_NAME = "JSeal";
    public static final String APP_VERSION = "1.0.0";
    public static final String APP_TITLE = "JSeal - PDF安全工具";

    // 文件相关
    public static final String PDF_EXTENSION = ".pdf";
    public static final String P12_EXTENSION = ".p12";
    public static final String PFX_EXTENSION = ".pfx";

    // 默认配置
    public static final int DEFAULT_KEY_SIZE = 2048;
    public static final int DEFAULT_CERT_VALID_DAYS = 365;
    public static final int DEFAULT_ENCRYPTION_KEY_LENGTH = 256;

    // 文件后缀
    public static final String ENCRYPTED_SUFFIX = "_encrypted";
    public static final String DECRYPTED_SUFFIX = "_decrypted";
    public static final String SIGNED_SUFFIX = "_signed";
    public static final String PROTECTED_SUFFIX = "_protected";

    // 用户数据目录名
    public static final String APP_DATA_DIR = ".jseal";
    public static final String CERTIFICATES_DIR = "certificates";
    public static final String HISTORY_FILE = "history.json";
    public static final String CONFIG_FILE = "config.json";
}
