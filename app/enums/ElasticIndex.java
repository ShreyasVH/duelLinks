package enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ElasticIndex
{
    CARDS("cards"),

    MY_CARDS("my_cards");

    @Getter
    private String name;
}
