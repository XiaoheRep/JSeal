package com.xiaohelab.jseal.service;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * 证书管理服务
 * 用于生成自签名证书、导入导出证书
 */
public class CertificateService {

    private static final String BC_PROVIDER = "BC";

    // 静态块确保 BouncyCastle 提供者被注册
    static {
        if (Security.getProvider(BC_PROVIDER) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * 生成自签名证书并保存为PKCS12格式
     *
     * @param commonName   通用名称 (CN)，如 "张三"
     * @param organization 组织 (O)，如 "XiaoHe Lab"
     * @param country      国家代码 (C)，如 "CN"
     * @param validDays    有效期天数
     * @param keySize      RSA密钥长度 (2048 或 4096)
     * @param alias        证书别名
     * @param password     密钥库密码
     * @param outputFile   输出的.p12文件
     * @throws Exception 生成失败时抛出
     */
    public void generateSelfSignedCertificate(
            String commonName,
            String organization,
            String country,
            int validDays,
            int keySize,
            String alias,
            char[] password,
            File outputFile) throws Exception {

        // 1. 生成RSA密钥对
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", BC_PROVIDER);
        keyGen.initialize(keySize, new SecureRandom());
        KeyPair keyPair = keyGen.generateKeyPair();

        // 2. 构建证书主题
        X500Name subject = new X500NameBuilder(BCStyle.INSTANCE)
                .addRDN(BCStyle.CN, commonName)
                .addRDN(BCStyle.O, organization)
                .addRDN(BCStyle.C, country)
                .build();

        // 3. 设置有效期
        Date notBefore = new Date();
        Date notAfter = new Date(System.currentTimeMillis() + (long) validDays * 24 * 60 * 60 * 1000);

        // 4. 构建X.509 v3证书
        BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                subject,        // 颁发者（自签名，所以和主题相同）
                serialNumber,
                notBefore,
                notAfter,
                subject,        // 主题
                keyPair.getPublic()
        );

        // 5. 添加扩展
        certBuilder.addExtension(Extension.basicConstraints, true,
                new BasicConstraints(false));
        certBuilder.addExtension(Extension.keyUsage, true,
                new KeyUsage(KeyUsage.digitalSignature | KeyUsage.nonRepudiation));

        // 6. 签名
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .setProvider(BC_PROVIDER)
                .build(keyPair.getPrivate());
        X509CertificateHolder certHolder = certBuilder.build(signer);

        // 7. 转换为X509Certificate
        X509Certificate certificate = new JcaX509CertificateConverter()
                .setProvider(BC_PROVIDER)
                .getCertificate(certHolder);

        // 8. 保存到PKCS12密钥库
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, null);
        keyStore.setKeyEntry(alias, keyPair.getPrivate(), password,
                new Certificate[]{certificate});

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            keyStore.store(fos, password);
        }
    }

    /**
     * 加载PKCS12证书
     *
     * @param p12File  .p12文件
     * @param password 密码
     * @return KeyStore对象
     */
    public KeyStore loadKeyStore(File p12File, char[] password) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream(p12File)) {
            keyStore.load(fis, password);
        }
        return keyStore;
    }

    /**
     * 获取证书信息
     */
    public CertificateInfo getCertificateInfo(File p12File, char[] password) throws Exception {
        KeyStore keyStore = loadKeyStore(p12File, password);
        String alias = keyStore.aliases().nextElement();
        X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);

        CertificateInfo info = new CertificateInfo();
        info.setAlias(alias);
        info.setSubject(cert.getSubjectX500Principal().getName());
        info.setIssuer(cert.getIssuerX500Principal().getName());
        info.setSerialNumber(cert.getSerialNumber().toString(16).toUpperCase());
        info.setNotBefore(cert.getNotBefore());
        info.setNotAfter(cert.getNotAfter());
        info.setAlgorithm(cert.getSigAlgName());
        info.setValid(cert.getNotAfter().after(new Date()));

        return info;
    }

    /**
     * 验证证书密码是否正确
     */
    public boolean verifyPassword(File p12File, char[] password) {
        try {
            loadKeyStore(p12File, password);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 证书信息类
     */
    public static class CertificateInfo {
        private String alias;
        private String subject;
        private String issuer;
        private String serialNumber;
        private Date notBefore;
        private Date notAfter;
        private String algorithm;
        private boolean valid;

        // Getters and Setters
        public String getAlias() { return alias; }
        public void setAlias(String alias) { this.alias = alias; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getIssuer() { return issuer; }
        public void setIssuer(String issuer) { this.issuer = issuer; }
        public String getSerialNumber() { return serialNumber; }
        public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
        public Date getNotBefore() { return notBefore; }
        public void setNotBefore(Date notBefore) { this.notBefore = notBefore; }
        public Date getNotAfter() { return notAfter; }
        public void setNotAfter(Date notAfter) { this.notAfter = notAfter; }
        public String getAlgorithm() { return algorithm; }
        public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }

        public String getValidityStatus() {
            return valid ? "有效" : "已过期";
        }
    }
}
