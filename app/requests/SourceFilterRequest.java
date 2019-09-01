package requests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SourceFilterRequest
{
    private Long id;
    private Boolean includeQuantityCheck = true;
    private Boolean includeExpiryCheck = true;
}
