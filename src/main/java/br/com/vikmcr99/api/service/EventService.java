package br.com.vikmcr99.api.service;

import br.com.vikmcr99.api.domain.event.Event;
import br.com.vikmcr99.api.domain.event.EventRequestDTO;
import br.com.vikmcr99.api.domain.event.EventResponseDTO;
import br.com.vikmcr99.api.repositories.EventRepository;
import com.amazonaws.services.s3.AmazonS3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class EventService {

    @Value("${aws.bucket.name}")
    private String bucketName;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AmazonS3 s3Client;

    public Event createEvent(EventRequestDTO data){
        String imageUrl = null;

        if(data.image() != null){
            imageUrl = this.uploadImg(data.image());
        }

        Event event = new Event();
        event.setTitle(data.title());
        event.setDescription(data.description());
        event.setImageUrl(imageUrl);
        event.setEventUrl(data.eventUrl());
        event.setDate(new Date(data.date()));
        event.setImageUrl(imageUrl);
        event.setRemote(data.remote());

        eventRepository.save(event);

        return event;
    }

    private String uploadImg(MultipartFile multipartFile){
        String fileName = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();

        try{
            File file = this.convertMultipartToFile(multipartFile);
            s3Client.putObject(bucketName, fileName, file);
            file.delete();
            return s3Client.getUrl(bucketName, fileName).toString();

        } catch (Exception e){
            System.out.println("Erro ao subir o arquivo: " + e.getMessage());
            return null;
        }
    }

    private File convertMultipartToFile(MultipartFile multipartFile) throws IOException {
        File convertFile =  new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convertFile);
        fos.write(multipartFile.getBytes());
        fos.close();

        return convertFile;
    }

    public List<EventResponseDTO> getAllEvents(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventsPage = eventRepository.findAll(pageable);
        return eventsPage.map(event -> new EventResponseDTO(event.getId(), event.getTitle(), event.getDescription(), event.getDate(), "", "",
                event.getRemote(), event.getEventUrl(), event.getImageUrl())).stream().toList();
    }
}
