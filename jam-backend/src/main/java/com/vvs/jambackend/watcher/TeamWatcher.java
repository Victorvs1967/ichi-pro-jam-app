package com.vvs.jambackend.watcher;

import com.vvs.jambackend.model.Team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import reactor.core.publisher.Flux;

@Slf4j
@Service
public class TeamWatcher {
  
  private ReactiveMongoTemplate reactiveMongoTemplate;

  @Autowired
  public TeamWatcher(ReactiveMongoTemplate reactiveMongoTemplate) {
    this.reactiveMongoTemplate = reactiveMongoTemplate;
  }

  public Flux<Team> watchForTeamChanges(String teamName) {
    ChangeStreamOptions options = ChangeStreamOptions.builder()
                          .filter(Aggregation.newAggregation(Team.class, Aggregation.match(Criteria.where("operationType").is("replace")
                                  )
                          )).returnFullDocumentOnUpdate().build();

    return reactiveMongoTemplate.changeStream("teams", options, Team.class)
            .map(ChangeStreamEvent::getBody)
            .filter(team -> team.getName().equals(teamName))
            .doOnError(throwable -> log.error("Error with the team changestream event: " + throwable.getMessage(), throwable));
  }

  public Flux<Team> watchForTeamCollectionChange() {
    ChangeStreamOptions options = ChangeStreamOptions.builder()
                          .filter(Aggregation.newAggregation(Team.class, Aggregation.match(Criteria.where("operationType").is("replace")
                              )
                          )).returnFullDocumentOnUpdate().build();
    return reactiveMongoTemplate.changeStream("teams", options, Team.class)
            .map(ChangeStreamEvent::getBody)
            .doOnError(throwable -> log.error("Error with the teams changestream event: " + throwable.getMessage(), throwable));
  }
}
