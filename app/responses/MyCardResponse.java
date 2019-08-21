package responses;

import lombok.Getter;
import lombok.Setter;
import models.MyCard;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class MyCardResponse
{
    private CardSnippet cardSnippet;
    private Map<String, Integer> glossTypeStats;
    private List<MyCard> individualCards;
}
