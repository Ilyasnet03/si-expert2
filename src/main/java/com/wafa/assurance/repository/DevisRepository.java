package com.wafa.assurance.repository;

import com.wafa.assurance.model.Devis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DevisRepository extends JpaRepository<Devis, Long> {
    List<Devis> findByMissionIdOrderByDateCreationDesc(Long missionId);

    @Query("select distinct d.garage from Devis d where d.garage is not null and trim(d.garage) <> '' order by d.garage")
    List<String> findDistinctGarages();
}
