package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.SourceType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "sources")
public class Source extends BaseModel
{
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private SourceType type;

    @Column(name = "quantity")
    private Integer quantity = 1;

    @Column(name = "expiry")
    private Date expiry;

    @Column(name = "created_at")
    private Date createdDate;
}
