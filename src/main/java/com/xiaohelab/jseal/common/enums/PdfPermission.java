package com.xiaohelab.jseal.common.enums;

/**
 * PDF权限枚举
 */
public enum PdfPermission {
    PRINT("允许打印"),
    PRINT_DEGRADED("允许低质量打印"),
    MODIFY("允许修改内容"),
    EXTRACT_CONTENT("允许复制文本和图像"),
    MODIFY_ANNOTATIONS("允许添加注释"),
    FILL_FORMS("允许填写表单"),
    EXTRACT_FOR_ACCESSIBILITY("允许屏幕阅读器提取"),
    ASSEMBLE("允许组装文档");

    private final String displayName;

    PdfPermission(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
