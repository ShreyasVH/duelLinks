package responses;

import enums.Rarity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RaritySnippet
{
    private Integer id;
    private String name;

    public RaritySnippet(Rarity rarity)
    {
        this.id = rarity.getValue();
        this.name = rarity.name();
    }
}
