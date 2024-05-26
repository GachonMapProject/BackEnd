package com.f2z.gach.AI;

import com.f2z.gach.Admin.Repository.AdminRepository;
import com.f2z.gach.DataGetter.dataRepository;
import com.f2z.gach.EnumType.Authorization;
import com.f2z.gach.History.Repository.HistoryLineTimeRepository;
import com.f2z.gach.Inquiry.Repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/ai")
public class AdminAIController {

    private final AdminRepository adminRepository;
    private final InquiryRepository inquiryRepository;
    private final HistoryLineTimeRepository lineTimeRepository;
    private final dataRepository dataRepo;
    private final AIService aiService;
    private final AiModelRepository aiRepo;
    long dataLength;

    @ModelAttribute
    public void setAttributes(Model model){
        model.addAttribute("waiterListSize", adminRepository.findByAdminAuthorization(Authorization.WAITER).size());
        model.addAttribute("inquiryWaitSize", inquiryRepository.countByInquiryProgressIsFalse());
    }

    // List
    @GetMapping("")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_GUEST')")
    public String list(Model model) {
        model.addAttribute("aiList", aiRepo.findAll());
        return "ai/ai-manage";
    }

    @GetMapping("/check/{modelId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String checkModel(@PathVariable int modelId) {

        aiRepo.findAll().forEach(aiModel -> {
            aiModel.setIsChecked(false);
            aiRepo.save(aiModel);
        });
        AiModel aiModel = aiRepo.findById(modelId).orElseThrow();
        aiModel.setIsChecked(true);
        aiRepo.save(aiModel);
        return "redirect:/admin/ai";
    }

    // 뷰
    @GetMapping("/model/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addModel(Model model) {
        dataLength = lineTimeRepository.count() + dataRepo.count() - aiRepo.findAiModelWithMaxId().orElseThrow().getDataLength();
        model.addAttribute("dataListLength", dataLength);
        return "ai/ai-add";
    }

    // 필터링
    @GetMapping("/model/add/filter/{min}/{max}/{augment}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    public long getFilterNum(@PathVariable int min, @PathVariable int max, @PathVariable int augment){
        log.info(String.valueOf(dataLength));
        return aiService.filterAndAugmentData(min, max, augment, lineTimeRepository.count() + dataRepo.count() - aiRepo.findAiModelWithMaxId().orElseThrow().getDataLength());
    }

    // AI모델 재학습 기능
    @GetMapping("/model/learn/{name}/{version}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    public int learningModel(@PathVariable String name, @PathVariable String version) throws Exception {
        dataLength = lineTimeRepository.count() + dataRepo.count();
        AiModel aiModel = new AiModel();
        AiModel beforeModel = aiRepo.findAiModelWithMaxId().orElseThrow();
        aiModel.setAiModelPath(aiService.reLearnModel(name));
        aiModel.setAiModelName(name);
        aiModel.setAiModelVersion(version);
        aiModel.setMaxFeature(beforeModel.getMaxFeature());
        aiModel.setMinSampleLeaf(beforeModel.getMinSampleLeaf());
        aiModel.setMinSampleSplit(beforeModel.getMinSampleSplit());
        aiModel.setMaxDepth(beforeModel.getMaxDepth());
        aiModel.setDataLength(dataLength);
        aiRepo.save(aiModel);
        return 1;
    }
}
