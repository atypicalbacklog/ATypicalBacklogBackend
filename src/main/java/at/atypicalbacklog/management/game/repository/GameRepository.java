package at.atypicalbacklog.management.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import at.atypicalbacklog.management.game.domain.Game;

public interface GameRepository extends JpaRepository<Game, String> {

}
