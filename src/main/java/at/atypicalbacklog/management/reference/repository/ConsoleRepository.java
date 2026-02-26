package at.atypicalbacklog.management.reference.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import at.atypicalbacklog.management.reference.domain.Console;

import java.util.List;

public interface ConsoleRepository
    extends JpaRepository<Console, String> {

    List<Console> findAllByOrderByNameAsc();
}
