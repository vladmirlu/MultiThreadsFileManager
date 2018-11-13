package com.multithreads.manager.statistics;

public class BufferTime {

    /**
     * Buffer of tasks.
     */
    private long buffer;

    /**
     * Time to do buffer tasks (in nanoseconds).
     */
    private long timeNanoSec;

    public BufferTime(long buffer, long timeNanoSec){
           this.buffer = buffer;
           this.timeNanoSec = timeNanoSec;
    }

    public long getBuffer() {
        return buffer;
    }

    public void setBuffer(long buffer) {
        this.buffer = buffer;
    }

    public long getTimeNanoSec() {
        return timeNanoSec;
    }

    public void setTimeNanoSec(long timeNanoSec) {
        this.timeNanoSec = timeNanoSec;
    }
}
