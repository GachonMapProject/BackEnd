package com.f2z.gach.AI;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class AiModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer aiModelId;
    private String aiModelVersion;
    private int epochs;
    private int hiddenLayer;
    private int numLayer;
    private double learningRate;
    private String aiModelName;
    private String aiModelPath;
    private int mae;
    private int mse;
    private int averSatis;
}
