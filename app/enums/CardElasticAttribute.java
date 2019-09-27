package enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CardElasticAttribute
{
    ID("id", FieldType.NORMAL),
    NAME("name", FieldType.NORMAL),
    LEVEL("level", FieldType.RANGE),
    ATTRIBUTE("attribute", FieldType.NESTED, "attribute.id", "attribute"),
    TYPE("type", FieldType.NESTED, "type.id", "type"),
    ATTACK("attack", FieldType.RANGE),
    DEFENSE("defense", FieldType.RANGE),
    CARD_TYPE("cardType", FieldType.NESTED, "cardType.id", "cardType"),
    CARD_SUB_TYPES("cardSubTypes", FieldType.NESTED, "cardSubTypes.id", "cardSubTypes"),
    RARITY("rarity", FieldType.NESTED, "rarity.id", "rarity"),
    LIMIT_TYPE("limitType", FieldType.NESTED, "limitType.id", "limitType"),
    SOURCES("sources", FieldType.NESTED, "sources.id", "sources");

    @Getter
    private String name;

    @Getter
    private FieldType type;

    @Getter
    private String nestedTerm;

    @Getter
    private String nestedLevel;

    CardElasticAttribute(String name, FieldType fieldType)
    {
        this.name = name;
        this.type = fieldType;
    }

    public static CardElasticAttribute fromString(String label)
    {
        CardElasticAttribute attribute = null;

        for(CardElasticAttribute currentAttribute: CardElasticAttribute.values())
        {
            if(currentAttribute.name.equals(label))
            {
                attribute = currentAttribute;
                break;
            }
        }

        return attribute;
    }
}
