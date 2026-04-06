package com.wafa.assurance.repository;

import com.wafa.assurance.model.NoteHonoraire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteHonoraireRepository extends JpaRepository<NoteHonoraire, Long> {
    List<NoteHonoraire> findByMissionIdOrderByCreatedAtDesc(Long missionId);
    Optional<NoteHonoraire> findById(Long id);
    Optional<NoteHonoraire> findByNumeroNote(String numeroNote);
    void deleteById(Long id);
}
