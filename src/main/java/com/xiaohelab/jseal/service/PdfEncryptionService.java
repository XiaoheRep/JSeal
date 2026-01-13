package com.xiaohelab.jseal.service;

import com.xiaohelab.jseal.common.enums.EncryptionAlgorithm;
import com.xiaohelab.jseal.common.enums.PdfPermission;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * PDF加密服务
 */
public class PdfEncryptionService {

    /**
     * 加密PDF文件
     *
     * @param inputFile    输入文件
     * @param outputFile   输出文件
     * @param userPassword 用户密码（打开PDF需要）
     * @param ownerPassword 所有者密码（修改权限需要）
     * @param algorithm    加密算法
     * @param permissions  权限设置
     * @throws IOException 加密失败时抛出
     */
    public void encrypt(File inputFile, File outputFile, 
                       String userPassword, String ownerPassword,
                       EncryptionAlgorithm algorithm, 
                       Set<PdfPermission> permissions) throws IOException {
        
        try (PDDocument document = PDDocument.load(inputFile)) {
            // 创建权限对象
            AccessPermission ap = createAccessPermission(permissions);
            
            // 创建保护策略
            StandardProtectionPolicy policy = new StandardProtectionPolicy(
                ownerPassword, userPassword, ap);
            policy.setEncryptionKeyLength(algorithm.getKeyLength());
            
            // 应用保护
            document.protect(policy);
            
            // 保存加密后的文件
            document.save(outputFile);
        }
    }

    /**
     * 仅设置密码保护（保留所有权限）
     */
    public void encryptWithPassword(File inputFile, File outputFile,
                                   String userPassword, String ownerPassword,
                                   EncryptionAlgorithm algorithm) throws IOException {
        
        try (PDDocument document = PDDocument.load(inputFile)) {
            AccessPermission ap = new AccessPermission();
            // 保留所有权限
            ap.setCanPrint(true);
            ap.setCanModify(true);
            ap.setCanExtractContent(true);
            ap.setCanModifyAnnotations(true);
            ap.setCanFillInForm(true);
            ap.setCanExtractForAccessibility(true);
            ap.setCanAssembleDocument(true);
            
            StandardProtectionPolicy policy = new StandardProtectionPolicy(
                ownerPassword, userPassword, ap);
            policy.setEncryptionKeyLength(algorithm.getKeyLength());
            
            document.protect(policy);
            document.save(outputFile);
        }
    }

    /**
     * 根据权限枚举集合创建AccessPermission
     */
    private AccessPermission createAccessPermission(Set<PdfPermission> permissions) {
        AccessPermission ap = new AccessPermission();
        
        // 默认禁止所有权限
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
     * 检查PDF是否已加密
     */
    public boolean isEncrypted(File pdfFile) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            return document.isEncrypted();
        } catch (IOException e) {
            // 如果无法打开，可能是加密的
            return true;
        }
    }
}
