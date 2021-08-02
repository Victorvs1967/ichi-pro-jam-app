package com.vvs.jambackend.model;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("teams")
public class Team {
  
  @Id
  private ObjectId id;
  private String name;

  private List<Player> players = new ArrayList<>();

  @Field("total_score")
  private Double totalScore;

  public Team(String name, List<Player> players) {
    this.name = name;
    this.players = players;
  }
}
