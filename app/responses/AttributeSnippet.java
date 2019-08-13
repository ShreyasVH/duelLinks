package responses;

import enums.Attribute;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AttributeSnippet
{
    private Integer id;
    private String name;

    public AttributeSnippet(Attribute attribute)
    {
        this.id = attribute.getValue();
        this.name = attribute.name();
    }
}
