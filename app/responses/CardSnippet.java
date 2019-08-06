package responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import enums.Attribute;
import enums.CardSubType;
import enums.CardType;
import enums.LimitType;
import enums.Rarity;
import enums.Type;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardSnippet
{
    private Long id;

    private String name;

    private Integer level;

    private Attribute attribute;

    private Type type;

    private Integer attack;

    private Integer defense;

    private CardType cardType;

    private List<CardSubType> cardSubTypes;

    private Rarity rarity;

    private LimitType limitType;

    private String imageUrl;
}
