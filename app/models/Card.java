package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Column;

import enums.Attribute;
import enums.Type;
import enums.CardType;
import enums.CardSubType;
import enums.Rarity;
import enums.LimitType;

@Getter
@Setter
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "cards")
public class Card extends BaseModel
{
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "level")
    private Integer level;

    @Column(name = "attribute")
    private Attribute attribute;

    @Column(name = "type")
    private Type type;

    @Column(name = "attack")
    private Integer attack;

    @Column(name = "defense")
    private Integer defense;

    @Column(name = "card_type")
    private CardType cardType;

    @Column(name = "rarity")
    private Rarity rarity;

    @Column(name = "limit_type")
    private LimitType limitType;

    @Column(name = "image_url")
    private String imageUrl = "";
}