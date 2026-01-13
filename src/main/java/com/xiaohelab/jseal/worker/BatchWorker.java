package com.xiaohelab.jseal.worker;

import com.xiaohelab.jseal.model.TaskResult;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 批量处理任务 Worker
 */
public class BatchWorker extends SwingWorker<List<TaskResult>, BatchWorker.Progress> {
    
    private final List<File> files;
    private final Function<File, TaskResult> taskExecutor;
    private final BiConsumer<Integer, Integer> progressCallback; // current, total
    private final Consumer<List<TaskResult>> completionCallback;

    public BatchWorker(List<File> files,
                       Function<File, TaskResult> taskExecutor,
                       BiConsumer<Integer, Integer> progressCallback,
                       Consumer<List<TaskResult>> completionCallback) {
        this.files = files;
        this.taskExecutor = taskExecutor;
        this.progressCallback = progressCallback;
        this.completionCallback = completionCallback;
    }

    @Override
    protected List<TaskResult> doInBackground() throws Exception {
        List<TaskResult> results = new ArrayList<>();
        int total = files.size();
        
        for (int i = 0; i < files.size(); i++) {
            if (isCancelled()) {
                break;
            }
            
            File file = files.get(i);
            TaskResult result = taskExecutor.apply(file);
            results.add(result);
            
            publish(new Progress(i + 1, total));
        }
        
        return results;
    }

    @Override
    protected void process(List<Progress> chunks) {
        if (progressCallback != null && !chunks.isEmpty()) {
            Progress latest = chunks.get(chunks.size() - 1);
            progressCallback.accept(latest.current, latest.total);
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
                List<TaskResult> errorResult = new ArrayList<>();
                errorResult.add(TaskResult.failure(e));
                completionCallback.accept(errorResult);
            }
        }
    }

    public static class Progress {
        public final int current;
        public final int total;

        public Progress(int current, int total) {
            this.current = current;
            this.total = total;
        }
    }
}
