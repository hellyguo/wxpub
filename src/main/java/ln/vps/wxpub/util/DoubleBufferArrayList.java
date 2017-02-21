package ln.vps.wxpub.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Helly on 2016/12/05.
 */
public class DoubleBufferArrayList<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoubleBufferArrayList.class);
    private static final long ONE_SECOND = 1000L;
    private static final int MAX_INDEX = 1000;
    private AtomicInteger idx = new AtomicInteger(0);
    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty;
    private List<T>[] list;

    public DoubleBufferArrayList() {
        this.notEmpty = this.lock.newCondition();
        List<T> list0 = new ArrayList<>();
        List<T> list1 = new ArrayList<>();
        this.list = new List[]{list0, list1};
    }

    public List<T> getData(AtomicBoolean quitFlag) {
        List<T> list = null;
        while (!quitFlag.get()) {
            this.lock.lock();
            try {
                list = this.getActiveList();
                if (!((List) list).isEmpty()) {
                    break;
                }
                list = null;
                this.notEmpty.await(ONE_SECOND, TimeUnit.MILLISECONDS);
            } catch (Exception var7) {
                LOGGER.warn(var7.getMessage(), var7);
            } finally {
                this.lock.unlock();
            }
        }
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public void addData(T data) {
        this.lock.lock();
        try {
            this.getIdleList().add(data);
            this.notEmpty.signal();
        } finally {
            this.lock.unlock();
        }
    }

    private List<T> getActiveList() {
        this.idx.compareAndSet(MAX_INDEX, 0);
        int index = this.idx.incrementAndGet();
        int i = (index - 1) % 2;
        int j = index % 2;
        LOGGER.debug("read|current idx = {},active={},idle={}", index, i, j);
        List<T> list = this.list[i];
        this.list[i] = new ArrayList<>();
        return list;
    }

    private List<T> getIdleList() {
        int index = idx.get();
        int i = (index - 1) % 2;
        int j = index % 2;
        LOGGER.debug("write|current idx = {},active={},idle={}", index, i, j);
        return this.list[j];
    }
}
