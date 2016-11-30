package threadpool;

import com.sogou.upd.passport.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 14-10-27
 * Time: 下午8:04
 */
public class ThreadPoolTaskExecutorTest extends BaseTest {


    @Autowired
    private TaskExecutor discardTaskExecutor;

    @Test
    public void testSpringThreadPoolTask() {
        try {
            for (int i = 0; i < 1500; i++) {
                discardTaskExecutor.execute(new TaskThread("message i " + i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class TaskThread implements Runnable {
        private String message;

        public TaskThread(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            System.out.println("Test spring thread pool task :" + message);
        }
    }

}
