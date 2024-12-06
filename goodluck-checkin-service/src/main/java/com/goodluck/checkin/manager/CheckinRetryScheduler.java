package com.goodluck.checkin.manager;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 单例模式的任务调度器，用于延迟任务重试的管理。
 * 使用双重检查锁实现线程安全的单例模式。
 *
 * @author liuleyi
 */
@Slf4j
public class CheckinRetryScheduler {

    private static volatile CheckinRetryScheduler instance; // 单例实例
    private final ExecutorService taskExecutor; // 单一线程池，用于队列处理和任务执行
    private final DelayQueue<DelayedTask> delayQueue; // 延迟任务队列
    private final AtomicBoolean isRunning; // 调度器运行状态

    private CheckinRetryScheduler() {
        // 固定线程池
        taskExecutor = new ThreadPoolExecutor(5, 5, 10L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(5), Executors.defaultThreadFactory());
        delayQueue = new DelayQueue<>();
        isRunning = new AtomicBoolean(false);
    }

    /**
     * 获取单例实例
     *
     * @return CheckinRetryScheduler 单例
     */
    public static CheckinRetryScheduler getInstance() {
        if (instance == null) {
            synchronized (CheckinRetryScheduler.class) {
                if (instance == null) {
                    instance = new CheckinRetryScheduler();
                }
            }
        }
        return instance;
    }

    /**
     * 启动调度器
     */
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            log.info("Starting CheckinRetryScheduler...");
            taskExecutor.submit(this::processQueue); // 提交队列处理任务
        }
    }

    /**
     * 停止调度器，释放资源
     */
    public void stop() {
        if (isRunning.compareAndSet(true, false)) {
            log.info("Stopping CheckinRetryScheduler...");
            taskExecutor.shutdownNow(); // 关闭线程池
            try {
                if (!taskExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    log.warn("Task executor did not terminate in time.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Interrupted while stopping scheduler.");
            }
        }
    }

    /**
     * 提交延迟任务
     *
     * @param task  需要执行的任务
     * @param delay 延迟时间
     * @param unit  时间单位
     */
    public void scheduleTask(Runnable task, long delay, TimeUnit unit) {
        if (isRunning.get()) {
            delayQueue.put(new DelayedTask(task, delay, unit));
        } else {
            throw new IllegalStateException("任务调度器未启动，请调用 start() 方法！");
        }
    }

    /**
     * 处理任务队列
     */
    private void processQueue() {
        log.info("Task queue processing started...");
        try {
            while (isRunning.get() && !Thread.currentThread().isInterrupted()) {
                // 从队列中获取任务（阻塞等待）
                DelayedTask delayedTask = delayQueue.take();
                log.info("Executing delayed task: {}", delayedTask);
                taskExecutor.submit(delayedTask.getTask()); // 执行任务
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 恢复中断状态
            log.warn("Task queue processing interrupted.");
        } catch (Exception e) {
            log.error("Error processing task queue: {}", e.getMessage(), e);
        }
    }

    /**
     * 延迟任务实现类
     */
    private static class DelayedTask implements Delayed {
        private final Runnable task;
        private final long startTime;

        public DelayedTask(Runnable task, long delay, TimeUnit unit) {
            this.task = task;
            this.startTime = System.currentTimeMillis() + unit.toMillis(delay);
        }

        public Runnable getTask() {
            return task;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long diff = startTime - System.currentTimeMillis();
            return unit.convert(diff, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            return Long.compare(this.startTime, ((DelayedTask) o).startTime);
        }
    }
}
