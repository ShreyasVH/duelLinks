package models;

import enums.CardSubType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "card_subtype_map")
public class CardSubTypeMap extends BaseModel
{
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "card_id")
    private Long cardId;

    @Column(name = "sub_type_id")
    private CardSubType cardSubType;
}
