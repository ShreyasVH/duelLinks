package requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardsFilterRequest
{
    private Integer count = 0;

    private Integer offset = 0;
}