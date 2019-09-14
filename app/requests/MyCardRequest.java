package requests;

import enums.CardGlossType;
import enums.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyCardRequest
{
    private Long id;
    private Long cardId;
    private CardGlossType glossType;
    private Status status = Status.ENABLED;
}
