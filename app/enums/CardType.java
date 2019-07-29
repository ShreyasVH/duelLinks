package enums;

import io.ebean.annotation.EnumValue;
import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public enum CardType
{
    @EnumValue("0")
    MONSTER(0),

    @EnumValue("1")
    SPELL(1),

    @EnumValue("2")
    TRAP(2);

    private int value;
}