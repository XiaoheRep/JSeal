package com.xiaohelab.jseal.worker;

import com.xiaohelab.jseal.model.SignatureRequest;
import com.xiaohelab.jseal.model.TaskResult;
import com.xiaohelab.jseal.service.PdfSignatureService;

import javax.swing.*;
import java.util.function.Consumer;

/**
 * 签名任务 Worker
 */
public class SignatureWorker extends SwingWorker<TaskResult, Integer> {
    
    private final SignatureRequest request;
    private final PdfSignatureService signatureService;
    private final Consumer<Integer> progressCallback;
    private final Consumer<TaskResult> completionCallback;

    public SignatureWorker(SignatureRequest request,
                           PdfSignatureService signatureService,
                           Consumer<Integer> progressCallback,
                           Consumer<TaskResult> completionCallback) {
        this.request = request;
        this.signatureService = signatureService;
        this.progressCallback = progressCallback;
        this.completionCallback = completionCallback;
    }

    @Override
    protected TaskResult doInBackground() throws Exception {
        long startTime = System.currentTimeMillis();
        
        try {
            publish(10);
            
            signatureService.sign(
                request.getInputFile(),
                request.getOutputFile(),
                request.getKeyStore(),
                request.getAlias(),
                request.getPassword().toCharArray(),
                request.getReason(),
                request.getLocation(),
                request.getContactInfo()
            );
            
            publish(100);
            
            TaskResult result = TaskResult.success(
                request.getInputFile().getAbsolutePath(),
                request.getOutputFile().getAbsolutePath()
            );
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
