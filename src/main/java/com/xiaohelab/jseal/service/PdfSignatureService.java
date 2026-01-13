package com.xiaohelab.jseal.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

import java.io.*;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * PDF数字签名服务
 */
public class PdfSignatureService {

    private static final String BC_PROVIDER = "BC";

    // 静态块确保 BouncyCastle 提供者被注册
    static {
        if (Security.getProvider(BC_PROVIDER) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * 对PDF文件进行数字签名
     *
     * @param inputFile    输入的PDF文件
     * @param outputFile   签名后的输出文件
     * @param keyStore     包含证书和私钥的KeyStore
     * @param alias        证书别名
     * @param password     私钥密码
     * @param reason       签名原因
     * @param location     签名位置
     * @param contactInfo  联系信息
     * @throws Exception 签名失败时抛出
     */
    public void sign(File inputFile, File outputFile,
                     KeyStore keyStore, String alias, char[] password,
                     String reason, String location, String contactInfo) throws Exception {

        try (PDDocument document = PDDocument.load(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {

            // 创建签名对象
            PDSignature signature = new PDSignature();
            signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
            signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
            signature.setSignDate(Calendar.getInstance());

            if (reason != null && !reason.isEmpty()) {
                signature.setReason(reason);
            }
            if (location != null && !location.isEmpty()) {
                signature.setLocation(location);
            }
            if (contactInfo != null && !contactInfo.isEmpty()) {
                signature.setContactInfo(contactInfo);
            }

            // 获取证书链和私钥
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password);
            Certificate[] chain = keyStore.getCertificateChain(alias);

            // 创建签名实现
            SignatureInterface signatureInterface = 
                    new CMSSignatureInterface(privateKey, chain);

            // 添加签名
            document.addSignature(signature, signatureInterface);

            // 保存（增量保存以保留签名）
            document.saveIncremental(fos);
        }
    }

    /**
     * 验证PDF签名
     *
     * @param pdfFile PDF文件
     * @return 签名验证结果列表
     */
    public List<SignatureInfo> verifySignatures(File pdfFile) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            List<PDSignature> signatures = document.getSignatureDictionaries();
            
            return signatures.stream().map(sig -> {
                SignatureInfo info = new SignatureInfo();
                info.setName(sig.getName());
                info.setReason(sig.getReason());
                info.setLocation(sig.getLocation());
                info.setContactInfo(sig.getContactInfo());
                info.setSignDate(sig.getSignDate() != null ? 
                        sig.getSignDate().getTime() : null);
                info.setFilter(sig.getFilter());
                info.setSubFilter(sig.getSubFilter());
                return info;
            }).toList();
        }
    }

    /**
     * 检查PDF是否已签名
     */
    public boolean isSigned(File pdfFile) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            return !document.getSignatureDictionaries().isEmpty();
        }
    }

    /**
     * CMS签名实现
     */
    private static class CMSSignatureInterface implements SignatureInterface {
        private final PrivateKey privateKey;
        private final Certificate[] certificateChain;

        public CMSSignatureInterface(PrivateKey privateKey, Certificate[] chain) {
            this.privateKey = privateKey;
            this.certificateChain = chain;
        }

        @Override
        public byte[] sign(InputStream content) throws IOException {
            try {
                // 读取要签名的内容
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int len;
                while ((len = content.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                byte[] data = baos.toByteArray();

                // 创建CMS签名
                CMSSignedDataGenerator gen = new CMSSignedDataGenerator();

                X509Certificate signingCert = (X509Certificate) certificateChain[0];

                ContentSigner sha256Signer = new JcaContentSignerBuilder("SHA256withRSA")
                        .setProvider(BC_PROVIDER)
                        .build(privateKey);

                gen.addSignerInfoGenerator(
                        new JcaSignerInfoGeneratorBuilder(
                                new JcaDigestCalculatorProviderBuilder()
                                        .setProvider(BC_PROVIDER)
                                        .build())
                                .build(sha256Signer, signingCert));

                gen.addCertificates(new JcaCertStore(Arrays.asList(certificateChain)));

                CMSProcessableByteArray msg = new CMSProcessableByteArray(data);
                CMSSignedData signedData = gen.generate(msg, false);

                return signedData.getEncoded();

            } catch (Exception e) {
                throw new IOException("签名失败: " + e.getMessage(), e);
            }
        }
    }

    /**
     * 签名信息类
     */
    public static class SignatureInfo {
        private String name;
        private String reason;
        private String location;
        private String contactInfo;
        private java.util.Date signDate;
        private String filter;
        private String subFilter;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getContactInfo() { return contactInfo; }
        public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
        public java.util.Date getSignDate() { return signDate; }
        public void setSignDate(java.util.Date signDate) { this.signDate = signDate; }
        public String getFilter() { return filter; }
        public void setFilter(String filter) { this.filter = filter; }
        public String getSubFilter() { return subFilter; }
        public void setSubFilter(String subFilter) { this.subFilter = subFilter; }
    }
}
