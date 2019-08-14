package requests;

import enums.Attribute;
import enums.CardSubType;
import enums.CardType;
import enums.LimitType;
import enums.Rarity;
import enums.Type;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CardRequest
{
    private Long id;

    private String name;

    private String description;

    private Integer level;

    private Attribute attribute;

    private Type type;

    private Integer attack;

    private Integer defense;

    private CardType cardType;

    private List<CardSubType> cardSubTypes;

    private Rarity rarity;

    private LimitType limitType;

    private String imageUrl = "";

    public void validate()
    {

    }
}
