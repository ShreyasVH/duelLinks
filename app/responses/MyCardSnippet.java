package responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.MyCard;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class MyCardSnippet
{
    private Long id;
    private Long cardId;
    private CardGlossTypeSnippet cardGlossType;
    private StatusSnippet status;
    private Date obtainedDate;

    public MyCardSnippet(MyCard myCard)
    {
        this.id = myCard.getId();
        this.cardId = myCard.getCardId();
        this.cardGlossType = new CardGlossTypeSnippet(myCard.getCardGlossType());
        this.status = new StatusSnippet(myCard.getStatus());
        this.obtainedDate = myCard.getObtainedDate();
    }
}
