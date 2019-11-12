package enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CardElasticAttribute
{
    ID("id", FieldType.NORMAL, "id"),
    NAME("name", FieldType.NORMAL, "name"),
    LEVEL("level", FieldType.RANGE, "level"),
    ATTRIBUTE("attribute", FieldType.NORMAL, "attributeId"),
    TYPE("type", FieldType.NORMAL, "typeId"),
    ATTACK("attack", FieldType.RANGE, "attack"),
    DEFENSE("defense", FieldType.RANGE, "defense"),
    CARD_TYPE("cardType", FieldType.NORMAL, "cardTypeId"),
    CARD_SUB_TYPES("cardSubTypes", FieldType.NORMAL, "cardSubTypeIds"),
    RARITY("rarity", FieldType.NORMAL, "rarityId"),
    LIMIT_TYPE("limitType", FieldType.NORMAL, "limitTypeId"),
    SOURCES("sources", FieldType.NORMAL, "sourceIds");

    @Getter
    private String name;

    @Getter
    private FieldType type;

    @Getter
    private String term;

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
