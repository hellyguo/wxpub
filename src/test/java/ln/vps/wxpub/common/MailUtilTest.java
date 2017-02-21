package ln.vps.wxpub.common;

import ln.vps.wxpub.util.MailUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Helly on 2017/02/12.
 */
public class MailUtilTest {

    private MailUtil util;

    @Before
    public void setUp() throws Exception {
        util = MailUtil.getInstance();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void mail() throws Exception {
        util.mail("hello","world","135xxxxxxxx@139.com");
    }

}