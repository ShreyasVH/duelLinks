package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Column;
import java.util.Date;

import enums.Attribute;
import enums.Type;
import enums.CardType;
import enums.Rarity;
import enums.LimitType;

@Getter
@Setter
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
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

    @Column(name = "version")
    private Integer version = 1;

    @Column(name = "release_date")
    private Date releaseDate;

    public Card(Card card)
    {
        this.id = card.getId();
        this.name = card.getName();
        this.description = card.getDescription();
        this.level = card.getLevel();
        this.attribute = card.getAttribute();
        this.type = card.getType();
        this.attack = card.getAttack();
        this.defense = card.getDefense();
        this.cardType = card.getCardType();
        this.rarity = card.getRarity();
        this.limitType = card.getLimitType();
        this.imageUrl = card.getImageUrl();
        this.version = card.getVersion();
        this.releaseDate = card.getReleaseDate();
    }
}