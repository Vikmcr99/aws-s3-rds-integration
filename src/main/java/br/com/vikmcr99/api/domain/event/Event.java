package br.com.vikmcr99.api.domain.event;

import br.com.vikmcr99.api.domain.adress.Adress;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Table(name = "event")
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor

public class Event {
    @Id
    @GeneratedValue
    private UUID id;

    private String title;
    private String description;
    private String imageUrl;
    private String eventUrl;
    private Boolean remote;
    private Date date;

    @OneToOne(mappedBy = "event", cascade = CascadeType.ALL)
    private Adress adress;

}
