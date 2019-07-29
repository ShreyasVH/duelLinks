package enums;

import io.ebean.annotation.EnumValue;
import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public enum Rarity
{
    @EnumValue("0")
    NORMAL(0),

    @EnumValue("1")
    RARE(1),

    @EnumValue("2")
    SUPER_RARE(2),

    @EnumValue("3")
    ULTRA_RARE(3);

    private int value;
}