package responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.SourceCardMap;
import models.Source;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SourceSnippet
{
    private Long id;
    private String name;
    private Integer quantity;
    private Date expiry;
    private Date createdDate;
    private List<SourceCardMap> cards;

    public SourceSnippet(Source source, List<SourceCardMap> sourceCardMaps)
    {
        this.id = source.getId();
        this.name = source.getName();
        this.quantity = source.getQuantity();
        this.expiry = source.getExpiry();
        this.createdDate = source.getCreatedDate();
        this.cards = sourceCardMaps;
    }
}
