package responses;

import enums.CardType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CardTypeSnippet
{
    private Integer id;
    private String name;

    public CardTypeSnippet(CardType cardType)
    {
        this.id = cardType.getValue();
        this.name = cardType.name();
    }
}
