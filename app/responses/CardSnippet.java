package responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;


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

    private Attribute attribute;

    private Integer attributeId;

    private Type type;

    private Integer typeId;

    private Integer attack;

    private Integer defense;

    private CardType cardType;

    private Integer cardTypeId;

    private List<CardSubType> cardSubTypes;

    private List<Integer> cardSubTypeIds;

    private Rarity rarity;

    private Integer rarityId;

    private LimitType limitType;

    private Integer limitTypeId;

    private String imageUrl;

    private String glossTypeStats;

    private Date firstObtainedDate;

    private Date lastObtainedDate;

    private List<Long> sourceIds;

    private Integer version;
}
