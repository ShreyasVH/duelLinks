package enums;

import io.ebean.annotation.EnumValue;
import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public enum CardSubType
{
    @EnumValue("0")
    NORMAL(0),

    @EnumValue("1")
    EFFECT(1),

    @EnumValue("2")
    RITUAL(2),

    @EnumValue("3")
    FUSION(3),

    @EnumValue("4")
    SYNCHRO(4),

    @EnumValue("5")
    TOON(5),

    @EnumValue("6")
    GEMINI(6),

    @EnumValue("7")
    UNION(7),

    @EnumValue("8")
    SPIRIT(8),

    @EnumValue("9")
    TUNER(9);

    private int value;
}