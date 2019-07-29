package enums;

import io.ebean.annotation.EnumValue;
import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public enum Attribute
{
    @EnumValue("0")
    LIGHT(0),

    @EnumValue("1")
    DARK(1),

    @EnumValue("2")
    WATER(2),

    @EnumValue("3")
    FIRE(3),

    @EnumValue("4")
    EARTH(4),

    @EnumValue("5")
    WIND(5),

    @EnumValue("6")
    DIVINE(6);

    private int value;

}