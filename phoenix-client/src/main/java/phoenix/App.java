package phoenix;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import phoenix.dao.AreaDao;
import phoenix.dataObject.AreaDO;

public class App {
    public static void main(String[] args) throws Exception {
        ApplicationContext context = new FileSystemXmlApplicationContext("/src/main/resources/spring.xml");
        SqlSessionFactoryBean b = (SqlSessionFactoryBean) context.getBean(SqlSessionFactoryBean.class);
        SqlSessionFactory f = b.getObject();
        AreaDao a = context.getBean(AreaDao.class);
        Map<String, Object> params = new HashMap<>();
        List<AreaDO> list = a.queryAreaList(params);
        System.out.println(list.size());
    }
}
