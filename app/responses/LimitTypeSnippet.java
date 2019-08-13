package responses;

import enums.LimitType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LimitTypeSnippet
{
    private Integer id;
    private String name;

    public LimitTypeSnippet(LimitType limitType)
    {
        this.id = limitType.getValue();
        this.name = limitType.name();
    }
}
