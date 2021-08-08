package enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public enum SourceType
{
    STARTER_DECK(0),
    LEVEL_UP(1),
    EVENT_DROP(2),
    GATE_DROP(3),
    TICKET(4),
    CARD_TRADER(5),
    EX_CARD_TRADER(6),
    MAIN_BOX(7),
    MINI_BOX(8),
    STRUCTURE_DECK(9),
    SPECIAL_DEAL(10),
    CAMPAIGN(11),
    SELECTION_BOX(12);

    @Getter
    @Setter
    private int value;
}
