package cz.upce.fei.nnpda.repository;

import cz.upce.fei.nnpda.model.entity.Project;
import cz.upce.fei.nnpda.model.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwner(AppUser owner);
    Optional<Project> findByIdAndOwner(Long id, AppUser owner);
}
