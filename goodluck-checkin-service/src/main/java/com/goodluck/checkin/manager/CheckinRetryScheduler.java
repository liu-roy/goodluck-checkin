package com.goodluck.checkin.manager;

import java.util.concurrent.*;
/**
 * @author liuleyi
 */
public class CheckinRetryScheduler {


    private final static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final static DelayQueue<DelayedTask> delayQueue = new DelayQueue<>();

    public static void start() {
        scheduler.scheduleWithFixedDelay(CheckinRetryScheduler::processQueue, 0, 1, TimeUnit.MINUTES);
    }

    private static void processQueue() {
        try {
            DelayedTask task = delayQueue.take();
            scheduler.schedule(task.getTask(), 0, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void scheduleTask(Runnable task, long delay, TimeUnit unit) {
        delayQueue.put(new DelayedTask(task, delay, unit));
    }

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
