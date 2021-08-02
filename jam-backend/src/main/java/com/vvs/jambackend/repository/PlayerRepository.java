package com.vvs.jambackend.repository;

import com.vvs.jambackend.model.Player;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableReactiveMongoRepositories
public interface PlayerRepository extends ReactiveMongoRepository<Player, String> {
  
}
