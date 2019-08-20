package requests;

import enums.CardGlossType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyCardRequest
{
    private Long id;
    private Long cardId;
    private CardGlossType glossType;
}
