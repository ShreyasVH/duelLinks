package enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ElasticIndex
{
    CARDS("cards");

    @Getter
    private String name;
}
