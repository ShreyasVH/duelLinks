package requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.Attribute;
import enums.CardSubType;
import enums.CardType;
import enums.LimitType;
import enums.Rarity;
import enums.Type;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

import exceptions.BadRequestException;
import enums.ErrorCode;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardRequest
{
    private Long id;

    private String name;

    private String description;

    private Integer level;

    private Attribute attribute;

    private Type type;

    private Integer attack;

    private Integer defense;

    private CardType cardType;

    private List<CardSubType> cardSubTypes;

    private Rarity rarity;

    private LimitType limitType;

    private String imageUrl;

    private String releaseDate;

    public void validate()
    {
        if(null != this.releaseDate)
        {
            try
            {
                Date releaseDate = (new SimpleDateFormat("yyyy-MM-dd").parse(this.releaseDate));
            }
            catch(Exception ex)
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid Release Date");
            }
        }
    }
}
