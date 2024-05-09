package com.f2z.gach.Map.DTO;

import com.f2z.gach.EnumType.PlaceCategory;
import com.f2z.gach.Map.Entity.BuildingFloor;
import com.f2z.gach.Map.Entity.MapLine;
import com.f2z.gach.Map.Entity.MapNode;
import com.f2z.gach.Map.Entity.PlaceSource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.domain.Page;

import java.util.List;


public class MapDTO {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Setter
    @ToString
    @DynamicUpdate
    public static class MapNodeDTO{
        private Integer nodeId;
        @NotBlank(message = "노드 이름을 입력해주세요.")
        private String nodeName;

        @NotNull(message = "위도를 입력해주세요.")
        private Double nodeLatitude;
        @NotNull(message = "경도를 입력해주세요.")
        private Double nodeLongitude;
        @NotNull(message = "고도를 입력해주세요.")
        private Double nodeAltitude;

        public MapNode toEntity() {
            return MapNode.builder()
                    .nodeId(nodeId)
                    .nodeName(nodeName)
                    .nodeLatitude(nodeLatitude)
                    .nodeLongitude(nodeLongitude)
                    .nodeAltitude(nodeAltitude)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Setter
    public static class MapLineDTO {
        private Integer lineId;
        @NotBlank(message = "간선 이름을 입력해주세요.")
        private String lineName;
        private Integer nodeCodeFirst;
        @NotBlank(message = "노드를 지정해주세요.")
        private String nodeNameFirst;
        private Integer nodeCodeSecond;
        @NotBlank(message = "노드를 지정해주세요.")
        private String nodeNameSecond;
        private Double weightShortest;
        private Double weightOptimal;

        public MapLine toEntity() {
            return MapLine.builder()
                    .lineName(lineName)
                    .nodeNameFirst(nodeNameFirst)
                    .nodeNameSecond(nodeNameSecond)
                    .build();
        }

        public MapLine toSaveEntity(String division, MapNode nodeFirst, MapNode nodeSecond) {
            Double weightShortest = getDistance(nodeFirst, nodeSecond);
            double deltaAltitude = (nodeSecond.getNodeAltitude() - nodeFirst.getNodeAltitude());

            Double weightOptimal = Math.toDegrees(Math.atan2(deltaAltitude, weightShortest));

            return MapLine.builder()
                    .lineName(lineName + division)
                    .nodeNameFirst(nodeFirst.getNodeName())
                    .nodeCodeFirst(nodeFirst.getNodeId())
                    .nodeNameSecond(nodeSecond.getNodeName())
                    .nodeCodeSecond(nodeSecond.getNodeId())
                    .weightShortest(weightShortest)
                    .weightOptimal(weightOptimal)
                    .build();
        }

        public static double getDistance(MapNode nodeFirst, MapNode nodeSecond){
            final double R = 6371.01;
            double dLat = Math.toRadians(nodeSecond.getNodeLatitude() - nodeFirst.getNodeLatitude());
            double dLon = Math.toRadians(nodeSecond.getNodeLongitude() - nodeFirst.getNodeLongitude());
            double a = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(Math.toRadians(nodeFirst.getNodeLatitude())) * Math.cos(Math.toRadians(nodeSecond.getNodeLatitude())) * Math.pow(Math.sin(dLon / 2), 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            return R * c;
        }


    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class MapLineListStructure {
        private Integer lineId;
        private String lineName;
        private String nodeNameFirst;
        private String nodeNameSecond;
        private Double weightShortest;
        private Double weightOptimal;

        public static MapLineListStructure toMapLineListStructure(MapLine mapLine) {
            return MapLineListStructure.builder()
                    .lineId(mapLine.getLineId())
                    .lineName(mapLine.getLineName())
                    .nodeNameFirst(mapLine.getNodeNameFirst())
                    .nodeNameSecond(mapLine.getNodeNameSecond())
                    .weightShortest(mapLine.getWeightShortest())
                    .weightOptimal(mapLine.getWeightOptimal())
                    .build();
        }
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class MapLineList {
        List<MapLineListStructure> lineList;
        Integer totalPage;
        Long totalElements;
    }

    public static MapLineList toMapLineList(Page<MapLine> linePage, List<MapLineListStructure> lineList){
        return MapLineList.builder()
                .lineList(lineList)
                .totalPage(linePage.getTotalPages())
                .totalElements(linePage.getTotalElements())
                .build();
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class MapNodeListStructure {
        private Integer nodeId;
        private String nodeName;
        private Double nodeLatitude;
        private Double nodeLongitude;
        private Double nodeAltitude;

        public static MapNodeListStructure toMapNodeListStructure(MapNode mapNode) {
            return MapNodeListStructure.builder()
                    .nodeId(mapNode.getNodeId())
                    .nodeName(mapNode.getNodeName())
                    .nodeLatitude(mapNode.getNodeLatitude())
                    .nodeLongitude(mapNode.getNodeLongitude())
                    .nodeAltitude(mapNode.getNodeAltitude())
                    .build();
        }
    }
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class MapNodeList {
        List<MapNodeListStructure> nodeList;
        Integer totalPage;
        Long totalElements;
    }

    public static MapNodeList toMapNodeList(Page<MapNode> nodePage, List<MapNodeListStructure> nodeList){
        return MapNodeList.builder()
                .nodeList(nodeList)
                .totalPage(nodePage.getTotalPages())
                .totalElements(nodePage.getTotalElements())
                .build();
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class PlaceSourceDTO {
        private Integer placeId;
        private String placeName;
        private String placeCategory;
        private Double placeLatitude;
        private Double placeLongitude;
        private Double placeAltitude;
        private String placeSummary;
        private Double buildingHeight;
        private List<BuildingFloor> buildingFloors;
        private String mainImageName;
        private String mainImagePath;
        private String thumbnailImageName;
        private String thumbnailImagePath;
        private String arImageName;
        private String arImagePath;

        public static PlaceSource toEntity(MapDTO.PlaceSourceDTO placeSourceDTO) {
            return PlaceSource.builder()
                    .placeId(placeSourceDTO.getPlaceId())
                    .placeName(placeSourceDTO.getPlaceName())
                    .placeCategory(PlaceCategory.valueOf(placeSourceDTO.getPlaceCategory()))
                    .placeLatitude(placeSourceDTO.getPlaceLatitude())
                    .placeLongitude(placeSourceDTO.getPlaceLongitude())
                    .placeAltitude(placeSourceDTO.getPlaceAltitude())
                    .placeSummary(placeSourceDTO.getPlaceSummary())
                    .buildingHeight(placeSourceDTO.getBuildingHeight())
                    .buildingFloors(placeSourceDTO.getBuildingFloors())
                    .mainImageName(placeSourceDTO.getMainImageName())
                    .mainImagePath(placeSourceDTO.getMainImagePath())
                    .thumbnailImageName(placeSourceDTO.getThumbnailImageName())
                    .thumbnailImagePath(placeSourceDTO.getThumbnailImagePath())
                    .arImageName(placeSourceDTO.getArImageName())
                    .arImagePath(placeSourceDTO.getArImagePath())
                    .build();
        }
    }




}
