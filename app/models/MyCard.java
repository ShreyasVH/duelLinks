package models;

import enums.CardGlossType;
import enums.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "my_cards")
@Getter
@Setter
@NoArgsConstructor
public class MyCard
{
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "card_id")
    private Long cardId;

    @Column(name = "gloss_type")
    private CardGlossType cardGlossType;

    @Column(name = "status")
    private Status status;

    @Column(name = "obtained_date")
    private Date obtainedDate;
}
