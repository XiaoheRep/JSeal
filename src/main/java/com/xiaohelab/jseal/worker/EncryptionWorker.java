package com.xiaohelab.jseal.worker;

import com.xiaohelab.jseal.model.EncryptionRequest;
import com.xiaohelab.jseal.model.TaskResult;
import com.xiaohelab.jseal.service.PdfEncryptionService;
import com.xiaohelab.jseal.service.PdfPermissionService;

import javax.swing.*;
import java.util.function.Consumer;

/**
 * 加密任务 Worker
 */
public class EncryptionWorker extends SwingWorker<TaskResult, Integer> {
    
    private final EncryptionRequest request;
    private final PdfEncryptionService encryptionService;
    private final PdfPermissionService permissionService;
    private final Consumer<Integer> progressCallback;
    private final Consumer<TaskResult> completionCallback;

    public EncryptionWorker(EncryptionRequest request,
                            PdfEncryptionService encryptionService,
                            PdfPermissionService permissionService,
                            Consumer<Integer> progressCallback,
                            Consumer<TaskResult> completionCallback) {
        this.request = request;
        this.encryptionService = encryptionService;
        this.permissionService = permissionService;
        this.progressCallback = progressCallback;
        this.completionCallback = completionCallback;
    }

    @Override
    protected TaskResult doInBackground() throws Exception {
        long startTime = System.currentTimeMillis();
        
        try {
            publish(10);
            
            // 根据是否有权限设置选择加密方法
            if (request.getPermissions() != null && !request.getPermissions().isEmpty()) {
                publish(30);
                encryptionService.encrypt(
                    request.getInputFile(),
                    request.getOutputFile(),
                    request.getUserPassword(),
                    request.getOwnerPassword(),
                    request.getAlgorithm(),
                    request.getPermissions()
                );
            } else {
                publish(30);
                encryptionService.encryptWithPassword(
                    request.getInputFile(),
                    request.getOutputFile(),
                    request.getUserPassword(),
                    request.getOwnerPassword(),
                    request.getAlgorithm()
                );
            }
            
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
