package responses;

import enums.Type;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
