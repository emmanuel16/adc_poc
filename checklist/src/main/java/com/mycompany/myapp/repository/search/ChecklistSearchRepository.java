package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.Checklist;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Checklist entity.
 */
public interface ChecklistSearchRepository extends ElasticsearchRepository<Checklist, Long> {
}
