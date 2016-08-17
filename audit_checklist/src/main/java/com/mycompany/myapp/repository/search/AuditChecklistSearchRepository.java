package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.AuditChecklist;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the AuditChecklist entity.
 */
public interface AuditChecklistSearchRepository extends ElasticsearchRepository<AuditChecklist, Long> {
}
