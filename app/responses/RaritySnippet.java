package responses;

import enums.Rarity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
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
