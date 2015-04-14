package phoenix.dataObject;

import org.apache.ibatis.type.Alias;

@Alias("data")
public class DataDO extends AbstractBasicDO {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean isAllowAttribute(String key) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String displayName() {      
        return null;
    }

}
