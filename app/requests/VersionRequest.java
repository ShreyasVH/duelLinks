package requests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VersionRequest
{
    private String name;
    private String imageUrl;
}
