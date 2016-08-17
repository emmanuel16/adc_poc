package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.PublishedChecklist;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the PublishedChecklist entity.
 */
public interface PublishedChecklistSearchRepository extends ElasticsearchRepository<PublishedChecklist, Long> {
}
