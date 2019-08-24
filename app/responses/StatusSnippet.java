package responses;

import enums.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StatusSnippet
{
    private Integer id;
    private String name;

    public StatusSnippet(Status status)
    {
        this.id = status.getValue();
        this.name = status.name();
    }
}
