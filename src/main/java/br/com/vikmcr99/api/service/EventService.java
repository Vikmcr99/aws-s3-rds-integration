package br.com.vikmcr99.api.service;

import br.com.vikmcr99.api.domain.adress.Adress;
import br.com.vikmcr99.api.domain.coupon.Coupon;
import br.com.vikmcr99.api.domain.event.*;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Value("${aws.bucket.name}")
    private String bucketName;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private  AdressService adressService;

    @Autowired
    private CouponService couponService;

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

        if (!data.remote()){
            adressService.createAdress(data, event);
        }

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

    public List<EventResponseDTO> getUpcomingEvents(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<EventAddressProjection> eventsPage = eventRepository.findUpComingEvents(new Date(), pageable);

        return eventsPage.map(event -> new EventResponseDTO(event.getId(), event.getTitle(), event.getDescription(), event.getDate(),
                event.getCity() != null ? event.getCity() : "",
                event.getUf() != null ? event.getUf() : "",
                event.getRemote(), event.getEventUrl(), event.getImageUrl())).stream().toList();
    }

    public List<EventResponseDTO> getFilteredEvents(int page, int size, String title, String city, String uf, Date startDate, Date endDate){
        title = (title == null) ? "" : title;
        city = (city == null) ? "" : city;
        uf = (uf == null) ? "" : uf;
        startDate = startDate == null ? new Date(0) : startDate;
        endDate = endDate == null ? new Date() : endDate;

        Pageable pageable = PageRequest.of(page, size);
        Page<EventAddressProjection> eventPage = eventRepository.findFilteredEvents(city, uf, startDate, endDate, pageable);

        return eventPage.map(event -> new EventResponseDTO(event.getId(), event.getTitle(), event.getDescription(), event.getDate(),
                event.getCity() != null ? event.getCity() : "",
                event.getUf() != null ? event.getUf() : "",
                event.getRemote(), event.getEventUrl(), event.getImageUrl())).stream().toList();
    }

    public EventDetailsDTO getEventDetails(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        Optional<Adress> address = adressService.findByEventId(eventId);

        List<Coupon> coupons = couponService.consultCoupons(eventId, new Date());

        List<EventDetailsDTO.CouponDTO> couponDTOs = coupons.stream()
                .map(coupon -> new EventDetailsDTO.CouponDTO(
                        coupon.getCode(),
                        coupon.getDiscount(),
                        coupon.getValid()))
                .collect(Collectors.toList());

        return new EventDetailsDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                address.isPresent() ? address.get().getCity() : "",
                address.isPresent() ? address.get().getUf() : "",
                event.getImageUrl(),
                event.getEventUrl(),
                couponDTOs);
    }

}
