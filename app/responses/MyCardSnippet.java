package responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class MyCardSnippet
{
    private Long id;
    private CardSnippet card;
    private Date firstObtainedDate;
    private Date lastObtainedDate;
    private Map<String, Integer> glossTypeStats = new HashMap<>();
}
