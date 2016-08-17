package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.AuditChecklist;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the AuditChecklist entity.
 */
@SuppressWarnings("unused")
public interface AuditChecklistRepository extends JpaRepository<AuditChecklist,Long> {

}
