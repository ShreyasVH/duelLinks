package enums;

import io.ebean.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public enum Status
{
    @EnumValue("0")
    DEFAULT(0),

    @EnumValue("1")
    ENABLED(1),

    @EnumValue("2")
    DISABLED(2),

    @EnumValue("3")
    DELETED(3);

    @Getter
    @Setter
    private int value;
}
