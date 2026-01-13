package com.xiaohelab.jseal.worker;

import com.xiaohelab.jseal.model.DecryptionRequest;
import com.xiaohelab.jseal.model.TaskResult;
import com.xiaohelab.jseal.service.PdfDecryptionService;

import javax.swing.*;
import java.util.function.Consumer;

/**
 * 解密任务 Worker
 */
public class DecryptionWorker extends SwingWorker<TaskResult, Integer> {
    
    private final DecryptionRequest request;
    private final PdfDecryptionService decryptionService;
    private final Consumer<Integer> progressCallback;
    private final Consumer<TaskResult> completionCallback;

    public DecryptionWorker(DecryptionRequest request,
                            PdfDecryptionService decryptionService,
                            Consumer<Integer> progressCallback,
                            Consumer<TaskResult> completionCallback) {
        this.request = request;
        this.decryptionService = decryptionService;
        this.progressCallback = progressCallback;
        this.completionCallback = completionCallback;
    }

    @Override
    protected TaskResult doInBackground() throws Exception {
        long startTime = System.currentTimeMillis();
        
        try {
            publish(10);
            
            decryptionService.decrypt(
                request.getInputFile(),
                request.getOutputFile(),
                request.getPassword()
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
