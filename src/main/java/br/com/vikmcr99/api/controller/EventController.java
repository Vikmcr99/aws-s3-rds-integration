package br.com.vikmcr99.api.controller;

import br.com.vikmcr99.api.domain.event.Event;
import br.com.vikmcr99.api.domain.event.EventRequestDTO;
import br.com.vikmcr99.api.domain.event.EventResponseDTO;
import br.com.vikmcr99.api.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/event")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Event> createEvent(@RequestParam("title") String title,
                                             @RequestParam(value= "description", required = false) String description,
                                             @RequestParam("date") Long date,
                                             @RequestParam("city") String city,
                                             @RequestParam("state") String state,
                                             @RequestParam("remote") Boolean remote,
                                             @RequestParam("eventUrl") String eventUrl,
                                             @RequestParam(value = "image", required = false)MultipartFile image) {
        EventRequestDTO eventRequestDTO = new EventRequestDTO(title, description, date, city, state, remote, eventUrl, image);
        return new ResponseEntity<>(eventService.createEvent(eventRequestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EventResponseDTO>> getAllEvents(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10" ) int size) {
        List<EventResponseDTO> events = eventService.getAllEvents(page, size);
        return new ResponseEntity<List<EventResponseDTO>>(events, HttpStatus.OK);
    }

}
