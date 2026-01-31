package at.atypicalbacklog.management.repository;

import at.atypicalbacklog.management.domain.Input;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InputRepository
    extends JpaRepository<Input, String> {

    List<Input> findAllByOrderByNameAsc();
}
