package phoenix;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import phoenix.config.Config;
import phoenix.config.Context;
import phoenix.dao.DualDao;
import phoenix.util.PropertiesTool;

import com.google.common.base.Preconditions;

public class App {

    public static void main(String[] args) throws Exception {
        Preconditions.checkNotNull(args, "Missing Params");
        Config.setContext(new Context(PropertiesTool.loadFile(args[0])));
        Config.setApplicationContext(new ClassPathXmlApplicationContext("classpath*:spring.xml"));

        SqlSessionFactoryBean b = (SqlSessionFactoryBean) Config.getApplicationContext().getBean(SqlSessionFactoryBean.class);
        SqlSessionFactory f = b.getObject();
        DualDao a = Config.getApplicationContext().getBean(DualDao.class);
        Map<String, Object> m = new HashMap<>();
        List<String> l = a.query(m);
        System.out.println(l.get(0));
    }
}
