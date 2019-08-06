package requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardsFilterRequest
{
    private Integer count = 100;

    private Integer offset = 0;

    private Map<String, List<String>> filters = new HashMap<>();
}