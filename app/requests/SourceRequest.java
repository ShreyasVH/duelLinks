package requests;

import enums.SourceType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SourceRequest
{
    private Long id;
    private String name;
    private SourceType type;
    private Integer quantity = 1;
    private List<Long> cards;
    private String expiry;
}
