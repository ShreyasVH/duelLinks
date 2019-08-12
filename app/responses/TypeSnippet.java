package responses;

import enums.Type;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TypeSnippet
{
    private Integer id;
    private String name;

    public TypeSnippet(Type type)
    {
        this.id = type.getValue();
        this.name = type.name();
    }
}
