package ln.vps.wxpub.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Helly on 2017/02/13.
 */
public class DoubleBufferArrayListTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoubleBufferArrayListTest.class);

    @Test
    public void demo() throws Exception {
        AtomicBoolean quit = new AtomicBoolean(false);
        DoubleBufferArrayList<Integer> list = new DoubleBufferArrayList<>();
        Thread r = new Thread(() -> {
            List<Integer> data;
            while (!quit.get()) {
                data = list.getData(quit);
                LOGGER.info("{}", data);
                try {
                    Thread.sleep(1L);
                } catch (InterruptedException e) {
                    //
                }
            }
        }, "read");
        Thread w = new Thread(() -> {
            int i = 0;
            while (!quit.get()) {
                list.addData(i++);
                LOGGER.info("{}", i);
                try {
                    Thread.sleep(1L);
                } catch (InterruptedException e) {
                    //
                }
            }
        }, "write");
        r.start();
        w.start();
        Thread.sleep(1000L);
        quit.set(true);
        Thread.sleep(100L);
    }

    @Test
    public void demo1() {
        AtomicBoolean quit = new AtomicBoolean(false);
        Runnable runnable = () -> {
            DoubleBufferArrayList<Integer> list = new DoubleBufferArrayList<>();
            for (int i = 0; i < 10; i++) {
                list.addData(i);
            }
            LOGGER.info("{}", list.getData(quit));
            LOGGER.info("{}", list.getData(quit));
            LOGGER.info("{}", list.getData(quit));
        };
        Thread thread = new Thread(runnable, "Test");
        thread.start();
        try {
            TimeUnit.MILLISECONDS.sleep(2000L);
        } catch (InterruptedException e) {
            //
        }
        quit.set(true);
        try {
            TimeUnit.MILLISECONDS.sleep(200L);
        } catch (InterruptedException e) {
            //
        }
    }
}