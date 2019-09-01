package responses;

import enums.SourceType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SourceTypeSnippet
{
    private Integer id;
    private String name;

    public SourceTypeSnippet(SourceType sourceType)
    {
        this.id = sourceType.getValue();
        this.name = sourceType.name();
    }
}
