package responses;

import enums.CardGlossType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardGlossTypeSnippet
{
    private Integer id;
    private String name;

    public CardGlossTypeSnippet(CardGlossType cardGlossType)
    {
        this.id = cardGlossType.getValue();
        this.name = cardGlossType.name();
    }

}
