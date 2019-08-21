package responses;

import enums.CardGlossType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashMap;

@Getter
@Setter
public class MyCardIndividualSnippet
{
    private Long id;
    private CardGlossType glossType;
    private Date obtainedDate;
}
