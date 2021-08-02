package com.vvs.jambackend.route;

import com.vvs.jambackend.handler.TeamHandler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class TeamRouter {
  
  @Bean
  public RouterFunction<ServerResponse> route(TeamHandler teamHandler) {
    return RouterFunctions
            .route(GET("/teams"), teamHandler::getTeams)
            .andRoute(GET("/teams/watch"), teamHandler::watchTeams)
            .andRoute(GET("/team/{name}"), teamHandler::watchTeam)
            .andRoute(GET("/update/{name}/{scoreChange}"), teamHandler::updatePlayerScore)
            .andRoute(GET("/update/{count}"), teamHandler::rendomizeScore)
            .andRoute(GET("/zero"), teamHandler::allZero)
            .andRoute(POST("/team"), teamHandler::createTeam);
  }

  public CorsWebFilter corsFilter() {

    CorsConfiguration config = new CorsConfiguration();

    config.setAllowCredentials(true);
    config.addAllowedOrigin("http://localhost:4200");
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);

    return new CorsWebFilter((CorsConfigurationSource) source);
  }

}
