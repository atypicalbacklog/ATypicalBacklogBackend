package at.atypicalbacklog.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import at.atypicalbacklog.management.domain.Game;

public interface GameRepository extends JpaRepository<Game, String> {

}
