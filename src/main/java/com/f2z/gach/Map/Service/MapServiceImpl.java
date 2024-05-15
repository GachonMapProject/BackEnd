package com.f2z.gach.Map.Service;

import com.f2z.gach.EnumType.College;
import com.f2z.gach.EnumType.Departments;
import com.f2z.gach.EnumType.PlaceCategory;
import com.f2z.gach.Map.DTO.NavigationResponseDTO;
import com.f2z.gach.Map.DTO.PlaceResponseDTO;
import com.f2z.gach.Map.Entity.*;
import com.f2z.gach.Map.Repository.*;
import com.f2z.gach.Response.ResponseEntity;
import com.f2z.gach.Response.ResponseListEntity;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class MapServiceImpl implements MapService{
    private final PlaceSourceRepository placeSourceRepository;
    private final BuildingFloorRepository buildingFloorRepository;
    private final BuildingKeywordRepository buildingKeywordRepository;
    private final MapLineRepository mapLineRepository;
    private final MapNodeRepository mapNodeRepository;

    private final String routeTypeShortest = "SHORTEST";
    private final String routeTypeOptimal = "OPTIMAL";
    private final String routeBus = "busRoute";


    @Override
    public ResponseEntity<PlaceResponseDTO.respondPlaceList> getBuildingInfoList() {
        List<PlaceSource> buildingInfoList = placeSourceRepository.findAllByPlaceCategory(PlaceCategory.BUILDING);
        if(buildingInfoList.isEmpty()){
            return ResponseEntity.notFound(null);
        }else {
            return ResponseEntity.requestSuccess(PlaceResponseDTO
                    .toRespondPlaceList(buildingInfoList));
        }
    }


    @Override
    public ResponseEntity<PlaceResponseDTO.toRespondBuildingInfo> getBuildingInfo(Integer placeId) {
        PlaceSource buildingInfo = placeSourceRepository.findByPlaceId(placeId);
        if(buildingInfo != null){
            List<BuildingFloor> buildingFloors= buildingFloorRepository.findAllByBuildingCode(placeId);
            return ResponseEntity.requestSuccess(PlaceResponseDTO.toRespondBuildingInfo(
                    buildingInfo,PlaceResponseDTO.toBuildingInfoStructureList(buildingFloors)));
        }else {
            return ResponseEntity.notFound(null);
        }
    }


    @Override
    public ResponseEntity<List<PlaceResponseDTO.respondKeywordList>> getKeywordResult(String target) {
        // 1. 장소 이름 자체를 검색하는 경우
        // 1-2. 카테고리로 검색
        try {placeSourceRepository.findPlaceSourcesByPlaceCategoryContaining(PlaceCategory.valueOf(target));
            return ResponseEntity.requestSuccess(PlaceResponseDTO.toKeywordList(placeSourceRepository.findPlaceSourcesByPlaceCategoryContaining(PlaceCategory.valueOf(target))));
        } catch (IllegalArgumentException e) {
            log.info("Not a category");
        }
        // 1-1. 건물 이름으로 검색
        if(!placeSourceRepository.findPlaceSourcesByPlaceNameContaining(target).isEmpty()){
            List<PlaceSource> targetList = placeSourceRepository.findPlaceSourcesByPlaceNameContaining(target);
            return ResponseEntity.requestSuccess(PlaceResponseDTO.toKeywordList(targetList));
        }

        // 2. 키워드로 검색하는 경우
        // 2-1. 학과로 검색
        try {
            BuildingKeyword keyword = buildingKeywordRepository.findByDepartmentContaining(Departments.valueOf(target));
            PlaceSource targetPlace = placeSourceRepository.findByPlaceId(keyword.getPlaceSource().getPlaceId());
            return ResponseEntity.requestSuccess(Collections.singletonList(PlaceResponseDTO.toKeywordList(targetPlace)));
        } catch (IllegalArgumentException e){
            log.info("Not a department");
        }
        // 2-2. 단과대학으로 검색
        try{
            BuildingKeyword keyword = buildingKeywordRepository.findByCollegeContaining(College.valueOf(target));
            PlaceSource targetPlace = placeSourceRepository.findByPlaceId(keyword.getPlaceSource().getPlaceId());
            return ResponseEntity.requestSuccess(Collections.singletonList(PlaceResponseDTO.toKeywordList(targetPlace)));
        } catch (IllegalArgumentException e){
            log.info("Not a college");
        }

        // 2-3. 교수님 성함으로 검색
        if (buildingKeywordRepository.findByProfessorNameContaining(target) != null) {
            BuildingKeyword keyword = buildingKeywordRepository.findByProfessorNameContaining(target);
            PlaceSource targetPlace = placeSourceRepository.findByPlaceId(keyword.getPlaceSource().getPlaceId());
            targetPlace.setPlaceSummary(keyword.getProfessorClass());
            return ResponseEntity.requestSuccess(Collections.singletonList(PlaceResponseDTO.toKeywordList(targetPlace)));
        }
        else {
            return ResponseEntity.saveButNoContent(null);
        }
    }

    @Override
    public ResponseEntity<PlaceResponseDTO.placeLocationDTO> getKeywordDetailResult(Integer placeId) {
        PlaceSource place = placeSourceRepository.findByPlaceId(placeId);
        if(place == null){
            return ResponseEntity.notFound(null);
        }else{
            return ResponseEntity.requestSuccess(PlaceResponseDTO.toPlaceLocationDTO(place));
        }
    }

    @Override
    public ResponseEntity<List<PlaceResponseDTO.placeLocationDTO>> getPlaceListByCategory(String placeCategory) {
        List<PlaceSource> placeList = placeSourceRepository.findAllByPlaceCategory(PlaceCategory.valueOf(placeCategory));
        if(placeList.isEmpty()) {
            return ResponseEntity.notFound(null);
        }else{
            return ResponseEntity.requestSuccess(PlaceResponseDTO.toPlaceLocationDTOList(placeList));
        }
    }

    @Override
    public ResponseListEntity<NavigationResponseDTO> getNowRoute(Integer placeId, Double latitude, Double longitude, Double altitude) {
        Integer departuresId = getNearestNodeId(latitude, longitude, altitude);
        PlaceSource arrivalsPlace = placeSourceRepository.findByPlaceId(placeId);
        Integer arrivalsId = getNearestNodeId(arrivalsPlace.getPlaceLatitude(),
                                arrivalsPlace.getPlaceLongitude(),
                                arrivalsPlace.getPlaceAltitude());

        return getNavigationResponseDTOResponseListEntity(departuresId, arrivalsId);
    }



    @Override
    public ResponseListEntity<NavigationResponseDTO> getRoute(Integer departures, Integer arrivals) {
        PlaceSource departuresPlace = placeSourceRepository.findByPlaceId(departures);
        departures = getNearestNodeId(departuresPlace.getPlaceLatitude(),
                departuresPlace.getPlaceLongitude(),
                departuresPlace.getPlaceAltitude());

        PlaceSource arrivalsPlace = placeSourceRepository.findByPlaceId(arrivals);
        arrivals = getNearestNodeId(arrivalsPlace.getPlaceLatitude(),
                arrivalsPlace.getPlaceLongitude(),
                arrivalsPlace.getPlaceAltitude());

        return getNavigationResponseDTOResponseListEntity(departures, arrivals);
    }

    private ResponseListEntity<NavigationResponseDTO> getNavigationResponseDTOResponseListEntity(Integer departures, Integer arrivals) {
        if(departures == 0 || arrivals == 0 || departures == null || arrivals == null){
            return ResponseListEntity.notFound(null);
        }
        if(Objects.equals(departures, arrivals)){
            return ResponseListEntity.sameNode(null);
        }

        NavigationResponseDTO shortestRoute  = calculateRoute(routeTypeShortest, departures, arrivals);
        NavigationResponseDTO optimalRoute = calculateRoute(routeTypeOptimal, departures, arrivals);
        List<NavigationResponseDTO> routes = Arrays.asList(shortestRoute, optimalRoute);

        return ResponseListEntity.requestListSuccess(routes.toArray(new NavigationResponseDTO[0]));
    }


    private Integer getNearestNodeId(Double placeLatitude, Double placeLongitude, Double placeAltitude) {
        int resultId = 0;
        final int R = 6371; // 지구의 반지름 (킬로미터)
        
        double minDistance= Double.MAX_VALUE;
        List<MapNode> nodeList = mapLineRepository.findAll().stream().map(MapLine::getNodeFirst).toList();
        for(MapNode node : nodeList){
            double c = getHaversine(placeLatitude, placeLongitude, node);
            double distance = R * c; // 거리 (킬로미터)

            if (minDistance > distance) {
                minDistance = distance;
                resultId = node.getNodeId();
            }
        }
        return resultId;
    }

    private static double getHaversine(Double placeLatitude, Double placeLongitude, MapNode node) {
        double latDistance = Math.toRadians(node.getNodeLatitude() - placeLatitude);
        double lonDistance = Math.toRadians(node.getNodeLongitude() - placeLongitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(placeLatitude)) * Math.cos(Math.toRadians(node.getNodeLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return c;
    }

    private NavigationResponseDTO calculateRoute(String routeType, Integer departuresId, Integer arrivalsId) {
        List<NavigationResponseDTO.NodeDTO> nodeList = new ArrayList<>();
        Map<Integer, Double> distances = new HashMap<>();
        Map<Integer, Integer> previousNodes = new HashMap<>();
        Set<Integer> visited = new HashSet<>();
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>(Comparator.comparingDouble(distances::get));

        distances.put(departuresId, 0.0);
        priorityQueue.offer(departuresId);

        while (!priorityQueue.isEmpty()) {
            int currentNodeId = priorityQueue.poll();

            if (currentNodeId == arrivalsId) {
                break;
            }

            if (visited.contains(currentNodeId)) {
                continue;
            }

            visited.add(currentNodeId);

            for (MapLine edge : mapLineRepository.findAllByNodeFirst_NodeId(currentNodeId)) {
                double weight = routeType.equals(routeTypeShortest) ? edge.getWeightShortest() : edge.getWeightOptimal() + 180;
                int neighborNodeId = edge.getNodeFirst().getNodeId() == currentNodeId ? edge.getNodeSecond().getNodeId() : edge.getNodeFirst().getNodeId();
                double distanceThroughCurrent = distances.getOrDefault(currentNodeId, Double.MAX_VALUE) + weight;

                if (distanceThroughCurrent < distances.getOrDefault(neighborNodeId, Double.MAX_VALUE)) {
                    distances.put(neighborNodeId, distanceThroughCurrent);
                    previousNodes.put(neighborNodeId, currentNodeId);
                    priorityQueue.offer(neighborNodeId);
                }
            }
        }

        // 경로 역추적
        int currentNodeId = arrivalsId;
        while (previousNodes.containsKey(currentNodeId)) {
            MapNode node = mapNodeRepository.findById(currentNodeId).orElseThrow(() -> new NoSuchElementException("Node not found"));
            nodeList.add(NavigationResponseDTO.NodeDTO.builder()
                    .nodeId(node.getNodeId())
                    .latitude(node.getNodeLatitude())
                    .longitude(node.getNodeLongitude())
                    .altitude(node.getNodeAltitude())
                    .build());
            currentNodeId = previousNodes.get(currentNodeId);
        }
        nodeList.add(NavigationResponseDTO.NodeDTO.builder()
                .nodeId(departuresId)
                .latitude(mapNodeRepository.findByNodeId(departuresId).getNodeLatitude())
                .longitude(mapNodeRepository.findByNodeId(departuresId).getNodeLongitude())
                .altitude(mapNodeRepository.findByNodeId(departuresId).getNodeAltitude())
                .build());
        Collections.reverse(nodeList);

        return NavigationResponseDTO.toNavigationResponseDTO(routeType, 0, nodeList);
    }

    private NavigationResponseDTO getBusRoute(Integer departuresId, Integer arrivalsId){
        List<NavigationResponseDTO.NodeDTO> nodeList = new ArrayList<>();


        return NavigationResponseDTO.toNavigationResponseDTO(routeBus,0,nodeList);
    }
}








