package at.atypicalbacklog.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import at.atypicalbacklog.management.domain.Playthrough;

public interface PlaythroughRepository extends JpaRepository<Playthrough, String> {}
