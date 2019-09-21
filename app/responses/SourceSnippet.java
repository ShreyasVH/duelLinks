package responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.Source;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class SourceSnippet
{
    private Long id;
    private String name;
    private SourceTypeSnippet sourceTypeSnippet;
    private Date expiry;
    private Date createdDate;

    public SourceSnippet(Source source)
    {
        this.id = source.getId();
        this.name = source.getName();
        this.sourceTypeSnippet = new SourceTypeSnippet(source.getType());
        this.expiry = source.getExpiry();
        this.createdDate = source.getCreatedAt();
    }
}
