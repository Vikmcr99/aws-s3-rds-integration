package br.com.vikmcr99.api.domain.adress;

import br.com.vikmcr99.api.domain.event.Event;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Table(name = "adress")
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Adress {
    @Id
    @GeneratedValue
    private UUID id;

    private String city;
    private String uf;

    @OneToOne
    @JoinColumn(name = "event_id")
    private Event event;
}
