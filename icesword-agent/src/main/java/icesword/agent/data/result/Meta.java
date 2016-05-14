package icesword.agent.data.result;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Builder
public class Meta {

    public long   app_group_id;
    public long   app_id;
    public String idnetifier;
    public String name_space;

}
