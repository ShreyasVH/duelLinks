package responses;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CardFilterResponse
{
    private Long totalCount = 0L;

    private List<CardSnippet> cards = new ArrayList<>();

    private Long offset = 0L;
}
