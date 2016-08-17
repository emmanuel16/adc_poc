package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.PublishedChecklist;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the PublishedChecklist entity.
 */
@SuppressWarnings("unused")
public interface PublishedChecklistRepository extends JpaRepository<PublishedChecklist,Long> {

}
