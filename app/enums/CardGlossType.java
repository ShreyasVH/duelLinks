package enums;

import io.ebean.annotation.EnumValue;
import lombok.AllArgsConstructor;



@AllArgsConstructor
public enum CardGlossType
{
    @EnumValue("0")
    NORMAL(0),

    @EnumValue("1")
    GLOSSY(1),

    @EnumValue("2")
    PRISMATIC(2);

    private int value;
}
