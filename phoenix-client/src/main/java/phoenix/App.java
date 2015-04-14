package phoenix;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import phoenix.dao.AreaDao;
import phoenix.dataObject.DataDO;

public class App {
    public static void main(String[] args) throws Exception {
        ApplicationContext context = new FileSystemXmlApplicationContext("/src/main/resources/spring.xml");
        SqlSessionFactoryBean b = (SqlSessionFactoryBean) context.getBean(SqlSessionFactoryBean.class);
        SqlSessionFactory f = b.getObject();
        final AreaDao a = context.getBean(AreaDao.class);
        ThreadPoolExecutor pe = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        for (int i = 0; i < 10; i++)
            pe.submit(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        long t = System.currentTimeMillis();
                        Map<String, Object> params = new HashMap<>();
                        Random r = new Random();
                        params.put("timesc", 396937 - r.nextInt(48));
                        List<DataDO> list = a.metricTest(params);
                        System.out.println(list.size() + ":" + (System.currentTimeMillis() - t));
                    }
                }
            });
    }
}
