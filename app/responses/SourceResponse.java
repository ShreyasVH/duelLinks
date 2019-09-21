package responses;

import enums.SourceType;
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
public class SourceResponse
{
    private Long id;
    private String name;
    private SourceType type;
    private Integer quantity;
    private Date expiry;
    private Date createdDate;
    private List<SourceCardMap> cards;

    public SourceResponse(Source source, List<SourceCardMap> sourceCardMaps)
    {
        this.id = source.getId();
        this.name = source.getName();
        this.type = source.getType();
        this.quantity = source.getQuantity();
        this.expiry = source.getExpiry();
        this.createdDate = source.getCreatedAt();
        this.cards = sourceCardMaps;
    }
}
