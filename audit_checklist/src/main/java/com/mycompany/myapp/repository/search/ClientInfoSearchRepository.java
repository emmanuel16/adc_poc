package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.ClientInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ClientInfo entity.
 */
public interface ClientInfoSearchRepository extends ElasticsearchRepository<ClientInfo, Long> {
}
