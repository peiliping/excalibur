package phoenix.dataObject;

import lombok.Getter;
import lombok.Setter;

import org.apache.ibatis.type.Alias;

@Alias("area")
public class AreaDO extends AbstractBasicDO {

    private static final long serialVersionUID = 1L;

    @Setter
    @Getter
    private String            countryCode;
    @Setter
    @Getter
    private String            countryName;
    @Setter
    @Getter
    private String            regionCode;
    @Setter
    @Getter
    private String            regionName;
    @Setter
    @Getter
    private long              parentId;

    public AreaDO() {}

    public AreaDO(String countryCode, String countryName, String regionCode, String regionName, long parentId) {
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.regionCode = regionCode;
        this.regionName = regionName;
        this.parentId = parentId;
    }

    @Override
    public boolean isAllowAttribute(String key) {
        return true;
    }

    @Override
    public String displayName() {
        return getCountryName() + "|" + getRegionName();
    }

}
