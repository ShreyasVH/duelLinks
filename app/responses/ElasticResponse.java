package responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class ElasticResponse<T>
{
    @Getter
    @Setter
    private Long totalCount = 0L;

    @Getter
    @Setter
    private List<T> documents = new ArrayList<>();
}
