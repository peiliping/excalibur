package icesword.agent.data.result;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ResultData {

    private Meta                     meta;

    private Map<String, List<IData>> data;

}
