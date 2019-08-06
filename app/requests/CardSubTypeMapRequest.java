package requests;

import enums.CardSubType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CardSubTypeMapRequest
{
    private List<Long> cardIds = new ArrayList<>();

    private List<CardSubType> cardSubTypes = new ArrayList<>();
}
