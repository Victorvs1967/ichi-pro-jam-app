package com.vvs.jambackend.handler;

import java.time.Duration;
import java.util.Collections;
import java.util.Random;

import com.vvs.jambackend.model.Player;
import com.vvs.jambackend.model.Team;
import com.vvs.jambackend.repository.TeamRepository;
import com.vvs.jambackend.watcher.TeamWatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.extern.java.Log;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Log
@Component
public class TeamHandler {

  private Random random;
  private TeamRepository teamRepository;
  private TeamWatcher teamWatcher;

  @Autowired
  public TeamHandler(TeamRepository teamRepository, TeamWatcher teamWatcher) {
    this.teamRepository = teamRepository;
    this.teamWatcher = teamWatcher;
    this.random = new Random();
  }

  public Mono<ServerResponse> getTeams(ServerRequest request) {
    return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromPublisher(teamRepository.findAll(), Team.class));
  }

  public Mono<ServerResponse> createTeam(ServerRequest request) {
    Mono<Team> team = request.bodyToMono(Team.class);
    return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromPublisher(team.flatMap(teamRepository::save), Team.class));
  }

  public Mono<ServerResponse> updatePlayerScore(ServerRequest request) {
    String playerName = request.pathVariable("name");
    String scoreChangeString = request.pathVariable("scoreChange");
    Double scoreChange = Double.valueOf(scoreChangeString);

    Mono<Team> teamMono = this.updateTeam(playerName, scoreChange);

    return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(teamMono, Team.class);
  }

  public Mono<ServerResponse> watchTeam(ServerRequest request) {
    String teamName = request.pathVariable("name");
    Flux<ServerSentEvent<Team>> sse = this.teamWatcher.watchForTeamChanges(teamName)
                                  .map(team -> ServerSentEvent
                                        .<Team>builder()
                                        .data(team)
                                        .build());
    return ServerResponse
            .ok()
            .body(BodyInserters.fromServerSentEvents(sse));
  }

  public Mono<ServerResponse> watchTeams(ServerRequest request) {
    Flux<ServerSentEvent<Team>> sse = this.teamWatcher.watchForTeamCollectionChange().log("Watch Teams")
                                  .map(team -> ServerSentEvent.<Team>builder()
                                        .data(team)
                                        .build());
    return ServerResponse
            .ok()
            .body(BodyInserters.fromServerSentEvents(sse)).log();
  }

  public Mono<ServerResponse> allZero(ServerRequest request) {
    return ServerResponse
            .ok()
            .body(this.zeroPlayers(), Team.class);
  }

  public Mono<ServerResponse> rendomizeScore(ServerRequest request) {
    String countString = request.pathVariable("count");
    int count = Integer.parseInt(countString);

    if (count < 0 || count > 40) {
      return ServerResponse
              .badRequest()
              .body(BodyInserters.fromValue("Count must be between 0 and 40"));
    }

    Flux<String> playerNames = this.teamRepository.findAll()
                  .map(Team::getPlayers)
                  .map(players -> players.stream().map(Player::getName))
                  .flatMap(Flux::fromStream)
                  .collectList()
                  .map(list -> {
                    while (list.size() < count) {
                      list.addAll(list);
                    }
                    Collections.shuffle(list);
                    return list;
                  })
                  .flatMapMany(Flux::fromIterable);

    Flux<Double> doubleFlux = Flux.interval(Duration.ofMillis(1000))
                  .map(pulse -> this.randomDouble())
                  .take(count);

    Flux<Team> updateFlux = Flux.zip(doubleFlux, playerNames)
                .flatMap(objects -> {
                  Double scoreChange = objects.getT1();
                  String name = objects.getT2();
                  return this.updateTeam(name, scoreChange);
                });

    return ServerResponse
            .ok()
            .body(BodyInserters.fromPublisher(updateFlux, Team.class));

  }

  private Double recalculateScore(Team team) {
    return team.getPlayers().stream()
            .mapToDouble(Player::getScore)
            .sum();
  }

  private Double randomDouble() {
    return this.random.doubles(1L, -2.0, 10.0).sum();
  }

  private Mono<Team> updateTeam(String playerName, Double scoreChange) {
    log.info("Player: " + playerName + ", Score Change: " + scoreChange);
    return this.teamRepository.findDistinctByPlayerName(playerName)
            .log("find by player")
            .onErrorReturn(new Team())
            .map(team -> {
              team.getPlayers().stream()
                .filter(player -> player.getName().equals(playerName))
                .forEach(player -> {
                  player.setScore(player.getScore() + scoreChange);
                  team.setTotalScore(recalculateScore(team));
                });
              return team;
            })
            .flatMap((teamRepository::save));
  }

  private Flux<Team> zeroPlayers() {
    return this.teamRepository.findAll()
            .map(team -> {
              team.getPlayers()
                .parallelStream()
                .forEach(player -> player.setScore(0.0));
              team.setTotalScore(this.recalculateScore(team));
              return team;
            })
            .flatMap(teamRepository::save);
  }

}
