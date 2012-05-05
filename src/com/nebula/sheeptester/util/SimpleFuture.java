/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author shevek
 */
public class SimpleFuture<T> implements Future<T> {

    private enum State {

        RUN, CANCELLED, DONE;
    }
    private T value;
    private Throwable throwable;
    private State state = State.RUN;
    private final Object lock = new Object();

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        synchronized (lock) {
            setThrowable(new InterruptedException());
            this.state = State.CANCELLED;
            return true;
        }
    }

    @Override
    public boolean isCancelled() {
        synchronized (lock) {
            return state == State.CANCELLED;
        }
    }

    @Override
    public boolean isDone() {
        synchronized (lock) {
            return state != State.RUN;
        }
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        synchronized (lock) {
            while (!isDone())
                lock.wait();
            if (throwable != null)
                throw new ExecutionException(throwable);
            return value;
        }
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        long now = System.currentTimeMillis();
        synchronized (lock) {
            while (!isDone()) {
                long delta = System.currentTimeMillis() - now;
                if (delta <= 0)
                    throw new TimeoutException();
                lock.wait(delta);
            }
            if (throwable != null)
                throw new ExecutionException(throwable);
            return value;
        }
    }

    public void setValue(T value) {
        synchronized (lock) {
            this.value = value;
            this.state = State.DONE;
            lock.notifyAll();
        }
    }

    public void setThrowable(Throwable throwable) {
        synchronized (lock) {
            this.throwable = throwable;
            this.state = State.DONE;
            lock.notifyAll();
        }
    }
}
