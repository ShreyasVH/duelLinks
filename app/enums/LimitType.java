package enums;

import io.ebean.annotation.EnumValue;
import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public enum LimitType
{
    @EnumValue("0")
    UNLIMITED(0),

    @EnumValue("2")
    LIMITED_1(1),

    @EnumValue("2")
    LIMITED_2(2),

    @EnumValue("3")
    LIMITED_3(3);

    private int value;
}