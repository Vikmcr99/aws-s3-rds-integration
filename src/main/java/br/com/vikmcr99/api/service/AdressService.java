package br.com.vikmcr99.api.service;

import br.com.vikmcr99.api.domain.adress.Adress;
import br.com.vikmcr99.api.domain.event.Event;
import br.com.vikmcr99.api.domain.event.EventRequestDTO;
import br.com.vikmcr99.api.repositories.AdressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service

public class AdressService {

    @Autowired
    private  AdressRepository adressRepository;

    public Adress createAdress(EventRequestDTO data, Event event) {
        Adress adress = new Adress();
        adress.setCity(data.city());
        adress.setUf(data.state());
        adress.setEvent(event);

        return adressRepository.save(adress);
    }

    public Optional<Adress> findByEventId(UUID eventId) {
        return adressRepository.findById(eventId);
    }
}
