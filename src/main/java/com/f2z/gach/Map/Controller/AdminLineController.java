package com.f2z.gach.Map.Controller;

import com.f2z.gach.Admin.Repository.AdminRepository;
import com.f2z.gach.EnumType.Authorization;
import com.f2z.gach.Inquiry.Repository.InquiryRepository;
import com.f2z.gach.Map.DTO.MapDTO;
import com.f2z.gach.Map.Entity.MapLine;
import com.f2z.gach.Map.Repository.MapLineRepository;
import com.f2z.gach.Map.Repository.MapNodeRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
@SessionAttributes
@Transactional
public class AdminLineController {
    private final MapLineRepository mapLineRepository;
    private final MapNodeRepository mapNodeRepository;
    private final AdminRepository adminRepository;
    private final InquiryRepository inquiryRepository;

    @ModelAttribute
    public void setAttributes(Model model){
        model.addAttribute("waiterListSize", adminRepository.findByAdminAuthorization(Authorization.WAITER).size());
        model.addAttribute("inquiryWaitSize", inquiryRepository.countByInquiryProgressIsFalse());
        model.addAttribute("lineSize", mapLineRepository.count());
    }

    @GetMapping("/line/list/{page}")
    public String lineListPage(Model model, @PathVariable Integer page){
        Pageable pageable = Pageable.ofSize(10).withPage(page);
        Page<MapLine> linePage = mapLineRepository.findAll(pageable);
        List<MapDTO.MapLineListStructure> lineList = linePage.getContent().stream()
                .sorted(Comparator.comparing(MapLine::getLineId).reversed())
                .map(MapDTO.MapLineListStructure::toMapLineListStructure).toList();
        model.addAttribute("lineList", MapDTO.toMapLineList(linePage, lineList));
        return "line/line-manage";
    }

    @GetMapping("/line/add")
    public String addLinePage(Model model){
        model.addAttribute("lineDto", new MapDTO.MapLineDTO());
        model.addAttribute("nodeList", mapNodeRepository.findAll());
        return "line/line-add";
    }

    @PostMapping("/line")
    public String addLine(@Valid @ModelAttribute("lineDto") MapDTO.MapLineDTO mapLineDTO,
                          BindingResult result){
        if(result.hasErrors()){
            return "line/line-add";
        }
        mapLineDTO.setNodeFirst(mapNodeRepository.findByNodeId(mapLineDTO.getNodeFirstId()));
        mapLineDTO.setNodeSecond(mapNodeRepository.findByNodeId(mapLineDTO.getNodeSecondId()));
        mapLineRepository.save(mapLineDTO.toSaveEntity("A",
                mapLineDTO.getNodeFirst(), mapLineDTO.getNodeSecond()));
        mapLineRepository.save(mapLineDTO.toSaveEntity("B",
                mapLineDTO.getNodeSecond(), mapLineDTO.getNodeFirst()));
        return "redirect:/admin/line/list/0";
    }

    @GetMapping("/line/{lineId}")
    public String deleteLine(@PathVariable Integer lineId,
                             Model model)throws Exception{
        if(mapLineRepository.existsByLineId(lineId)){
            mapLineRepository.deleteByLineId(lineId);
            if(lineId%2==0) lineId-=1;
            else lineId+=1;
            mapLineRepository.deleteByLineId(lineId);
            model.addAttribute("message", "간선이 삭제되었습니다.");
            model.addAttribute("lineList", mapLineRepository.findAll());
            return "redirect:/admin/line/list/0";
        }
        throw new Exception();
    }
}
