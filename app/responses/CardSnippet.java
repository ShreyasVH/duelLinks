package responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import enums.CardSubType;
import enums.LimitType;
import enums.Rarity;

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

    private AttributeSnippet attribute;

    private TypeSnippet type;

    private Integer attack;

    private Integer defense;

    private CardTypeSnippet cardType;

    private List<CardSubTypeSnippet> cardSubTypes;

    private RaritySnippet rarity;

    private LimitType limitType;

    private String imageUrl;
}
