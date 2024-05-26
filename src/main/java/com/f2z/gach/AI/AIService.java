package com.f2z.gach.AI;

import com.f2z.gach.DataGetter.dataEntity;
import com.f2z.gach.History.Entity.HistoryLineTime;
import com.f2z.gach.History.Repository.HistoryLineTimeRepository;
import com.f2z.gach.Map.DTO.NavigationResponseDTO;
import com.f2z.gach.Map.Entity.MapLine;
import com.f2z.gach.Map.Repository.MapLineRepository;
import com.f2z.gach.Map.Service.MapServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIService {
    Random random = new Random();
    private final HistoryLineTimeRepository lineTimeRepo;
    private final MapLineRepository mapLineRepository;
    private final AiModelRepository aiRepo;
    private ProcessBuilder processBuilder;

    final String localPythonPath = "python3";
    final String tempOutputPath = "/home/t24102/AI/python/tree_output.py";
    final String localReModelPath = "/home/t24102/AI/python/re_learn.py";
    final String csvFilePath = "/home/t24102/AI/dataset/data.csv";
    String modelPath = "/home/t24102/AI/model/";

    private List<dataEntity> augmentData(dataEntity row, int augmentCnt) {
        List<dataEntity> augmentedRows = new ArrayList<>();
        for (int i = 0; i < augmentCnt; i++) {
            dataEntity augmentedRow = row;
            row.setWeight(augmentedRow.getWeight() + random.nextDouble() * 4 - 2);
            row.setHeight(augmentedRow.getHeight() + random.nextDouble() * 4 - 2);
            row.setTemperature(augmentedRow.getTemperature() + random.nextDouble() * 4 - 2);
            row.setTakeTime((int) (augmentedRow.getTakeTime() + random.nextDouble() * 4 - 2));
            augmentedRows.add(row);
        }
        return augmentedRows;
    }

    public long filterAndAugmentData(int min, int max, int augment, long startIndex) {


        List<HistoryLineTime> originalList = lineTimeRepo.findAll();
        List<HistoryLineTime> sublist = originalList.subList(Math.toIntExact(Math.max(originalList.size() - startIndex, 0)), originalList.size());
        log.info(String.valueOf(sublist.size()));
        // 필터링 과정
        List<dataEntity> filteredList = sublist.stream()
                .filter(data -> data.getLineTime() != null && data.getLineTime() > (double) min && data.getLineTime() < (double) max)
                .map(dataEntity::parseHistory).toList();

        // 증식 과정
        List<dataEntity> augmentedList = new ArrayList<>();
        for (dataEntity data : filteredList) {
            augmentedList.addAll(augmentData(data, augment));
        }

        // CSV 파일 작성 과정
        try (FileWriter writer = new FileWriter(csvFilePath)) {
            writer.append("birthYear,gender,height,weight,walkSpeed,temperature,precipitationProbability,precipitation,weightShortest,weightOptimal,takeTime\n");

            for (dataEntity data : augmentedList) {
                writer.append(String.valueOf(data.getBirthYear())).append(",");
                writer.append(String.valueOf(data.getGender())).append(",");
                writer.append(String.valueOf(data.getHeight())).append(",");
                writer.append(String.valueOf(data.getWeight())).append(",");
                writer.append(String.valueOf(data.getWalkSpeed())).append(",");
                writer.append(String.valueOf(data.getTemperature())).append(",");
                writer.append(String.valueOf(data.getPrecipitationProbability())).append(",");
                writer.append(String.valueOf(data.getPrecipitation())).append(",");
                writer.append(String.valueOf(data.getWeightOptimal())).append(",");
                writer.append(String.valueOf(data.getWeightShortest())).append(",");
                writer.append(String.valueOf(data.getTakeTime())).append("\n");
            }

            log.info("CSV 파일이 성공적으로 생성되었습니다.");

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 필터링 && 증식한 데이터 개수
        return augmentedList.size();
    }

    public String reLearnModel(String modelName) throws Exception{
        String tempPath = modelPath + modelName + ".pkl";
        processBuilder = new ProcessBuilder(localPythonPath, localReModelPath,
                aiRepo.findAiModelWithMaxId().orElseThrow().getAiModelPath(), csvFilePath, tempPath);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        StringBuilder sb = new StringBuilder();
        log.info("재학습 시작");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            log.info(line);
            sb.append(line).append("\n");
        }
        reader.close();
        return tempPath;
    }

    public int calculateTime(List<NavigationResponseDTO.NodeDTO> list, MapServiceImpl.AIData data){
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()/3);

        List<CompletableFuture<Integer>> futures = new ArrayList<>();
        for(int i = 0; i < list.size()-1; i++){
            MapLine shortMapLine = mapLineRepository.findLineIdByNodeFirst_NodeIdAndNodeSecond_NodeId(list.get(i).getNodeId(), list.get(i+1).getNodeId());
            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return modelOutput(shortMapLine, data);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, executor);
            futures.add(future);
        }

        return futures.stream()
                .map(CompletableFuture::join)
                .reduce(0, Integer::sum);
    }

    public Integer modelOutput(MapLine line, MapServiceImpl.AIData data) throws Exception{
        processBuilder = new ProcessBuilder(localPythonPath, tempOutputPath,
                String.valueOf(data.getBirthYear()), String.valueOf(data.getGender().ordinal()),
                String.valueOf(data.getHeight()), String.valueOf(data.getWeight()),
                String.valueOf(data.getWalkSpeed().ordinal()), String.valueOf(data.getTemperature()),
                String.valueOf(data.getPrecipitationProbability()), String.valueOf(data.getPrecipitation()),
                String.valueOf(line.getWeightShortest()), String.valueOf(line.getWeightOptimal()), aiRepo.findAiModelWithMaxId().orElseThrow().getAiModelPath());
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        String takeTime = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String printLine;
            while ((printLine = reader.readLine()) != null) {
                takeTime = printLine;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        process.destroy();

        takeTime = takeTime.substring(1, takeTime.length() - 1);
        double number = Double.parseDouble(takeTime);

        return (int) Math.round(number);
    }
}
