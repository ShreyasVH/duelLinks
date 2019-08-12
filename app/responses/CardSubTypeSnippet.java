package responses;

import enums.CardSubType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardSubTypeSnippet
{
    private Integer id;
    private String name;

    public CardSubTypeSnippet(CardSubType cardSubType)
    {
        this.id = cardSubType.getValue();
        this.name = cardSubType.name();
    }
}
