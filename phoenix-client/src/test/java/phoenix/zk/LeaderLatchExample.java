package phoenix.zk;

import java.util.Iterator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.framework.recipes.leader.Participant;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;

public class LeaderLatchExample {

    private static final String PATH = "/examples/leader";
    private static int          flag = 1;

    public static void start() throws Exception {
        TestingServer server = new TestingServer();
        CuratorFramework client1 = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
        CuratorFramework client2 = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
        LeaderLatch example1 = new LeaderLatch(client1, PATH, "Client #" + 1);
        LeaderLatch example2 = new LeaderLatch(client2, PATH, "Client #" + 2);
        example1.addListener(new LeaderLatchListener() {
            @Override
            public void notLeader() {
                // TODO Auto-generated method stub
            }

            @Override
            public void isLeader() {
                // TODO Auto-generated method stub
            }
        });
        client1.start();
        client2.start();
        example1.start();
        example2.start();
        Thread.sleep(1000 * 10);
        if (example1.hasLeadership()) {
            System.out.println("L is 1");
            CloseableUtils.closeQuietly(example1);
            flag = 1;
        } else {
            System.out.println("L is 2");
            CloseableUtils.closeQuietly(example2);
            flag = 2;
        }
        Thread.sleep(1000 * 10);
        System.out.println(example1.hasLeadership());
        System.out.println(example2.hasLeadership());
        Thread.sleep(1000 * 10);
        if (flag == 1) {
            example1 = new LeaderLatch(client1, PATH, "Client #" + 1);
            example1.start();
        } else {
            example2 = new LeaderLatch(client2, PATH, "Client #" + 2);
            example2.start();
        }
        Thread.sleep(1000 * 10);
        System.out.println(example1.hasLeadership());
        System.out.println(example2.hasLeadership());

        Iterator<Participant> it = example1.getParticipants().iterator();
        while (it.hasNext()) {
            Participant t = it.next();
            System.out.println(t.getId());
        }



        CloseableUtils.closeQuietly(example1);
        CloseableUtils.closeQuietly(example2);
        CloseableUtils.closeQuietly(client1);
        CloseableUtils.closeQuietly(client2);
        CloseableUtils.closeQuietly(server);
    }
}
