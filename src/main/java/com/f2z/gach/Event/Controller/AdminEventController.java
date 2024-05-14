package com.f2z.gach.Event.Controller;

import com.f2z.gach.Admin.Repository.AdminRepository;
import com.f2z.gach.EnumType.Authorization;
import com.f2z.gach.Event.DTO.*;
import com.f2z.gach.Event.Entity.Event;
import com.f2z.gach.Event.Entity.EventLocation;
import com.f2z.gach.Event.Repository.EventLocationRepository;
import com.f2z.gach.Event.Repository.EventRepository;
import com.f2z.gach.Inquiry.Repository.InquiryRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/event")
@SessionAttributes
public class AdminEventController {
    private final EventRepository eventRepository;
    private final AdminRepository adminRepository;
    private final EventLocationRepository eventLocationRepository;
    private final InquiryRepository inquiryRepository;

    @Value("${gach.img.dir}")
    String fdir;

    @ModelAttribute
    public void setAttributes(Model model){
        model.addAttribute("waiterListSize", adminRepository.findByAdminAuthorization(Authorization.WAITER).size());
        model.addAttribute("inquiryWaitSize", inquiryRepository.countByInquiryProgressIsFalse());

    }

    @GetMapping("/list/{page}")
    public String eventListPage(Model model, @PathVariable Integer page){
        Pageable pageable = Pageable.ofSize(10).withPage(page);
        Page<Event> eventPage = eventRepository.findAllBy(pageable);
        List<EventResponseDTO.AdminEventListStructure> eventResponseDTOList = eventPage.getContent().stream()
                .map(EventResponseDTO.AdminEventListStructure::toAdminEventListStructure).toList();
        model.addAttribute("eventList", EventResponseDTO.toAdminEventList(eventPage, eventResponseDTOList));

        return "event/event-manage";
    }

    @GetMapping("/add")
    public String addEventPage(Model model){
        model.addAttribute("eventDto", new AdminEventRequestDTO(new Event()));
        return "event/event-add";
    }

    @GetMapping("/{eventId}")
    public String updateEventPage(@PathVariable Integer eventId, Model model){
        model.addAttribute("eventDto", AdminEventRequestDTO
                .toEventRequestDTO(eventRepository.findByEventId(eventId),
                eventLocationRepository.findAllByEvent_EventId(eventId)));

        // AdminEventRequestDTO(event=Event(eventId=1, eventName=가천대학교 축구리그, eventLink=https://www.instagram.com/p/C4zwuIOv4ga/?utm_source=ig_web_copy_link&igsh=MzRlODBiNWFlZA==, eventStartDate=2024-04-11, eventEndDate=2024-05-29, eventInfo=새로운 에너지가 충만한 2024년, 우리의 열정이 폭발하는 이곳 [2024 가천대학교 축구리그: G-LEAGUE], eventImageName=SoccerEvent.png, eventImagePath=http://ceprj.gachon.ac.kr:60002/image/SoccerEvent.png),
        // locations=[EventLocation(eventLocationId=1, eventName=가천대학교 축구 리그, eventPlaceName=경기장, eventAltitude=75.28107668, eventLatitude=37.455084, eventLongitude=127.135115, event=Event(eventId=1, eventName=가천대학교 축구리그, eventLink=https://www.instagram.com/p/C4zwuIOv4ga/?utm_source=ig_web_copy_link&igsh=MzRlODBiNWFlZA==, eventStartDate=2024-04-11, eventEndDate=2024-05-29, eventInfo=새로운 에너지가 충만한 2024년, 우리의 열정이 폭발하는 이곳 [2024 가천대학교 축구리그: G-LEAGUE], eventImageName=SoccerEvent.png, eventImagePath=http://ceprj.gachon.ac.kr:60002/image/SoccerEvent.png)),
        // EventLocation(eventLocationId=2, eventName=가천대학교 축구 리그, eventPlaceName=유니폼 배부 장소, eventAltitude=83.5, eventLatitude=37.454586, eventLongitude=127.13504, event=Event(eventId=1, eventName=가천대학교 축구리그, eventLink=https://www.instagram.com/p/C4zwuIOv4ga/?utm_source=ig_web_copy_link&igsh=MzRlODBiNWFlZA==, eventStartDate=2024-04-11, eventEndDate=2024-05-29, eventInfo=새로운 에너지가 충만한 2024년, 우리의 열정이 폭발하는 이곳 [2024 가천대학교 축구리그: G-LEAGUE], eventImageName=SoccerEvent.png, eventImagePath=http://ceprj.gachon.ac.kr:60002/image/SoccerEvent.png)),
        // EventLocation(eventLocationId=3, eventName=가천대학교 축구 리그, eventPlaceName=음료수 프로모션, eventAltitude=74.0, eventLatitude=37.454792, eventLongitude=127.135404, event=Event(eventId=1, eventName=가천대학교 축구리그, eventLink=https://www.instagram.com/p/C4zwuIOv4ga/?utm_source=ig_web_copy_link&igsh=MzRlODBiNWFlZA==, eventStartDate=2024-04-11, eventEndDate=2024-05-29, eventInfo=새로운 에너지가 충만한 2024년, 우리의 열정이 폭발하는 이곳 [2024 가천대학교 축구리그: G-LEAGUE], eventImageName=SoccerEvent.png, eventImagePath=http://ceprj.gachon.ac.kr:60002/image/SoccerEvent.png))],
        // file=null)

        // FIXME 여기서 객체가 전달되어야, location이 전송되는지 확인 가능함.
        // 전송하는 객체의 log.info가 정상적으로 나오면 푸시해줘. html 안 만져도 돼ㅠㅠ 고맙다.
        return "event/event-detail";
    }

    @PostMapping()
    public String addEvent(@Valid @ModelAttribute AdminEventRequestDTO requestDTO,
                           BindingResult result){
        try{
            File dest = new File(fdir+"/"+requestDTO.getFile().getOriginalFilename());
            requestDTO.getFile().transferTo(dest);
            requestDTO.getEvent().setEventImageName(dest.getName());
            requestDTO.getEvent().setEventImagePath("/image/"+dest.getName());
        } catch (Exception e){
            e.printStackTrace();
        }
        if(result.hasErrors()) {
            return "event/event-add";
        }

        Event event = requestDTO.getEvent();

        eventRepository.save(event);
        requestDTO.getLocations().stream().forEach(i -> {
            if(i.getEventLatitude() != null){
                eventLocationRepository.save(EventLocation.updateEventLocation(i, event));
            }
            log.info(i.toString());
        });
        return "redirect:/admin/event/list/0";
    }

    @PostMapping("/update")
    public String updateEvent(@Valid @ModelAttribute("eventDto") AdminEventRequestDTO requestDTO,
                              BindingResult result){
        if(result.hasErrors()){
            return "event/event-detail";
        }
        log.info(requestDTO.toString());
        Event event = eventRepository.findByEventId(requestDTO.getEvent().getEventId());
        event.update(requestDTO.getEvent());
        Event updatedEvent = eventRepository.save(event);
        requestDTO.getLocations().stream().forEach(i -> {
            if(i.getEventLatitude() != null){
                if(i.getEventLocationId() == null){
                    eventLocationRepository.save(EventLocation.updateEventLocation(i, updatedEvent));
                }else{
                    EventLocation eventLocation = eventLocationRepository.findByEventLocationId(i.getEventLocationId());
                    eventLocation.update(i.updateEventLocation(i, updatedEvent));
                    eventLocationRepository.save(eventLocation);
                }
            }
            log.info(i.toString());
        });
        return "redirect:/admin/event/list/0";
    }

    @GetMapping("/delete/{eventId}")
    public String deleteEvent(@PathVariable Integer eventId){
        Event event = eventRepository.findByEventId(eventId);
        if(event != null){
            eventLocationRepository.deleteEventLocationsByEvent_EventId(eventId);
            eventRepository.delete(event);
            return "redirect:/admin/event/list/0";
        }else{
            return "redirect:/admin/event/{eventId}";
        }
    }
}
