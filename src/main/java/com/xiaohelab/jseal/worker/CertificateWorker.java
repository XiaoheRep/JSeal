package com.xiaohelab.jseal.worker;

import com.xiaohelab.jseal.model.CertificateRequest;
import com.xiaohelab.jseal.model.TaskResult;
import com.xiaohelab.jseal.service.CertificateService;

import javax.swing.*;
import java.util.function.Consumer;

/**
 * 证书生成任务 Worker
 */
public class CertificateWorker extends SwingWorker<TaskResult, Integer> {
    
    private final CertificateRequest request;
    private final CertificateService certificateService;
    private final Consumer<Integer> progressCallback;
    private final Consumer<TaskResult> completionCallback;

    public CertificateWorker(CertificateRequest request,
                             CertificateService certificateService,
                             Consumer<Integer> progressCallback,
                             Consumer<TaskResult> completionCallback) {
        this.request = request;
        this.certificateService = certificateService;
        this.progressCallback = progressCallback;
        this.completionCallback = completionCallback;
    }

    @Override
    protected TaskResult doInBackground() throws Exception {
        long startTime = System.currentTimeMillis();
        
        try {
            publish(10);
            
            certificateService.generateSelfSignedCertificate(
                request.getCommonName(),
                request.getOrganization(),
                request.getCountry(),
                request.getValidDays(),
                request.getKeySize(),
                request.getAlias(),
                request.getPassword().toCharArray(),
                request.getOutputFile()
            );
            
            publish(100);
            
            TaskResult result = TaskResult.success("证书生成成功: " + request.getOutputFile().getName());
            result.setOutputFile(request.getOutputFile().getAbsolutePath());
            result.setDuration(System.currentTimeMillis() - startTime);
            return result;
            
        } catch (Exception e) {
            TaskResult result = TaskResult.failure(e);
            result.setDuration(System.currentTimeMillis() - startTime);
            return result;
        }
    }

    @Override
    protected void process(java.util.List<Integer> chunks) {
        if (progressCallback != null && !chunks.isEmpty()) {
            progressCallback.accept(chunks.get(chunks.size() - 1));
        }
    }

    @Override
    protected void done() {
        try {
            if (completionCallback != null) {
                completionCallback.accept(get());
            }
        } catch (Exception e) {
            if (completionCallback != null) {
                completionCallback.accept(TaskResult.failure(e));
            }
        }
    }
}
