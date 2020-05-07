package enums;

import lombok.Getter;
import io.ebean.annotation.EnumValue;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public enum Type
{
    @EnumValue("0")
    DRAGON(0),

    @EnumValue("1")
    ZOMBIE(1),

    @EnumValue("2")
    FIEND(2),

    @EnumValue("3")
    PYRO(3),

    @EnumValue("4")
    SEA_SERPENT(4),

    @EnumValue("5")
    ROCK(5),

    @EnumValue("6")
    MACHINE(6),

    @EnumValue("7")
    FISH(7),

    @EnumValue("8")
    DINOSAUR(8),

    @EnumValue("9")
    INSECT(9),

    @EnumValue("10")
    BEAST(10),

    @EnumValue("11")
    BEAST_WARRIOR(11),

    @EnumValue("12")
    PLANT(12),

    @EnumValue("13")
    AQUA(13),

    @EnumValue("14")
    WARRIOR(14),

    @EnumValue("15")
    WINGED_BEAST(15),

    @EnumValue("16")
    FAIRY(16),

    @EnumValue("17")
    SPELLCASTER(17),

    @EnumValue("18")
    THUNDER(18),

    @EnumValue("19")
    REPTILE(19),

    @EnumValue("20")
    PSYCHIC(20),

    @EnumValue("21")
    WYRM(21),

    @EnumValue("22")
    DIVINE_BEAST(22),

    @EnumValue("23")
    CYBERSE(23);

    @Getter
    private int value;
}