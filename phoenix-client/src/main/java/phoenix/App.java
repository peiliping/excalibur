package phoenix;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import phoenix.config.Config;
import phoenix.config.Context;

import com.google.common.base.Preconditions;

public class App {

    public static void main(String[] args) throws Exception {
        Preconditions.checkNotNull(args, "Missing Params");
        Config.setContext(Context.buildContextByFilePath(args[0]));
        Config.setApplicationContext(new ClassPathXmlApplicationContext("classpath*:spring.xml"));

        // SqlSessionFactoryBean b = (SqlSessionFactoryBean)
        // context.getBean(SqlSessionFactoryBean.class);
        // SqlSessionFactory f = b.getObject();
        // AreaDao a = context.getBean(AreaDao.class);
        // Map<String, Object> m = new HashMap<>();
        // List<AreaDO> l = a.queryAreaList(m);
        // System.out.println(l.size());
    }
}
