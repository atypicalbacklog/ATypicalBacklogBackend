package at.atypicalbacklog.management.reference.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import at.atypicalbacklog.management.reference.domain.Input;

import java.util.List;

public interface InputRepository
    extends JpaRepository<Input, String> {

    List<Input> findAllByHideInChoicesFalseOrderByNameAsc();
}
