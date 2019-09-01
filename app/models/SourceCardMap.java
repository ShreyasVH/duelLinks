package models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "source_card_map")
public class SourceCardMap extends BaseModel
{
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "card_id")
    private Long cardId;

    @Column(name = "source_id")
    private Long sourceId;
}
