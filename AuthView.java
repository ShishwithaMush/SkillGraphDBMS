package com.dbms.model;
import java.time.LocalDateTime;
public class ProgressInfo { private final int placementScore; private final LocalDateTime lastUpdated; public ProgressInfo(int placementScore,LocalDateTime lastUpdated){this.placementScore=placementScore;this.lastUpdated=lastUpdated;} public int getPlacementScore(){return placementScore;} public LocalDateTime getLastUpdated(){return lastUpdated;} }
