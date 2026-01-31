package at.atypicalbacklog.management.repository;

import at.atypicalbacklog.management.domain.Console;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsoleRepository
    extends JpaRepository<Console, String> {

    List<Console> findAllByOrderByNameAsc();
}
