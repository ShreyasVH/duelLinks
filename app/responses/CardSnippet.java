package responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.Date;
import java.util.List;
import java.util.Map;


@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardSnippet
{
    private Long id;

    private String name;

    private String description;

    private Integer level;

    private AttributeSnippet attribute;

    private TypeSnippet type;

    private Integer attack;

    private Integer defense;

    private CardTypeSnippet cardType;

    private List<CardSubTypeSnippet> cardSubTypes;

    private RaritySnippet rarity;

    private LimitTypeSnippet limitType;

    private String imageUrl;

    private Map<String, Integer> glossTypeStats;

    private Date firstObtainedDate;

    private Date lastObtainedDate;

    private List<MyCardSnippet> individualCards;

    private List<SourceSnippet> sources;
}
