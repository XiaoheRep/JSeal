package com.xiaohelab.jseal.service;

import com.xiaohelab.jseal.common.enums.PdfPermission;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;

/**
 * PDF权限控制服务
 */
public class PdfPermissionService {

    /**
     * 设置PDF权限
     *
     * @param inputFile     输入文件
     * @param outputFile    输出文件
     * @param ownerPassword 所有者密码
     * @param userPassword  用户密码（可为空）
     * @param permissions   权限集合
     * @param keyLength     加密密钥长度（128或256）
     * @throws IOException 操作失败时抛出
     */
    public void setPermissions(File inputFile, File outputFile,
                               String ownerPassword, String userPassword,
                               Set<PdfPermission> permissions,
                               int keyLength) throws IOException {
        
        try (PDDocument document = PDDocument.load(inputFile)) {
            AccessPermission ap = createAccessPermission(permissions);
            
            StandardProtectionPolicy policy = new StandardProtectionPolicy(
                ownerPassword, 
                userPassword != null ? userPassword : "", 
                ap);
            policy.setEncryptionKeyLength(keyLength);
            
            document.protect(policy);
            document.save(outputFile);
        }
    }

    /**
     * 读取PDF当前权限
     *
     * @param pdfFile  PDF文件
     * @param password 密码（可为空）
     * @return 权限集合
     * @throws IOException 读取失败时抛出
     */
    public Set<PdfPermission> getPermissions(File pdfFile, String password) throws IOException {
        Set<PdfPermission> permissions = EnumSet.noneOf(PdfPermission.class);
        
        try (PDDocument document = PDDocument.load(pdfFile, password)) {
            if (!document.isEncrypted()) {
                // 未加密的文件拥有所有权限
                return EnumSet.allOf(PdfPermission.class);
            }
            
            AccessPermission ap = document.getCurrentAccessPermission();
            
            if (ap.canPrint()) permissions.add(PdfPermission.PRINT);
            if (ap.canPrintDegraded()) permissions.add(PdfPermission.PRINT_DEGRADED);
            if (ap.canModify()) permissions.add(PdfPermission.MODIFY);
            if (ap.canExtractContent()) permissions.add(PdfPermission.EXTRACT_CONTENT);
            if (ap.canModifyAnnotations()) permissions.add(PdfPermission.MODIFY_ANNOTATIONS);
            if (ap.canFillInForm()) permissions.add(PdfPermission.FILL_FORMS);
            if (ap.canExtractForAccessibility()) permissions.add(PdfPermission.EXTRACT_FOR_ACCESSIBILITY);
            if (ap.canAssembleDocument()) permissions.add(PdfPermission.ASSEMBLE);
        }
        
        return permissions;
    }

    /**
     * 根据权限枚举集合创建AccessPermission
     */
    private AccessPermission createAccessPermission(Set<PdfPermission> permissions) {
        AccessPermission ap = new AccessPermission();
        
        ap.setCanPrint(permissions.contains(PdfPermission.PRINT));
        ap.setCanPrintDegraded(permissions.contains(PdfPermission.PRINT_DEGRADED));
        ap.setCanModify(permissions.contains(PdfPermission.MODIFY));
        ap.setCanExtractContent(permissions.contains(PdfPermission.EXTRACT_CONTENT));
        ap.setCanModifyAnnotations(permissions.contains(PdfPermission.MODIFY_ANNOTATIONS));
        ap.setCanFillInForm(permissions.contains(PdfPermission.FILL_FORMS));
        ap.setCanExtractForAccessibility(permissions.contains(PdfPermission.EXTRACT_FOR_ACCESSIBILITY));
        ap.setCanAssembleDocument(permissions.contains(PdfPermission.ASSEMBLE));
        
        return ap;
    }

    /**
     * 获取"仅阅读"权限预设
     */
    public static Set<PdfPermission> getReadOnlyPreset() {
        return EnumSet.of(
            PdfPermission.PRINT,
            PdfPermission.EXTRACT_FOR_ACCESSIBILITY
        );
    }

    /**
     * 获取"禁止打印"权限预设
     */
    public static Set<PdfPermission> getNoPrintPreset() {
        Set<PdfPermission> permissions = EnumSet.allOf(PdfPermission.class);
        permissions.remove(PdfPermission.PRINT);
        permissions.remove(PdfPermission.PRINT_DEGRADED);
        return permissions;
    }

    /**
     * 获取"禁止复制"权限预设
     */
    public static Set<PdfPermission> getNoCopyPreset() {
        Set<PdfPermission> permissions = EnumSet.allOf(PdfPermission.class);
        permissions.remove(PdfPermission.EXTRACT_CONTENT);
        return permissions;
    }
}
