package requests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SourceCardMapFilterRequest
{
    private Long id;
    private Long cardId;
}
