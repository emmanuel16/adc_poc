package com.mycompany.myapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mycompany.myapp.domain.AuditChecklist;
import com.mycompany.myapp.repository.AuditChecklistRepository;
import com.mycompany.myapp.repository.search.AuditChecklistSearchRepository;
import com.mycompany.myapp.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing AuditChecklist.
 */
@RestController
@RequestMapping("/api")
public class AuditChecklistResource {

    private final Logger log = LoggerFactory.getLogger(AuditChecklistResource.class);
        
    @Inject
    private AuditChecklistRepository auditChecklistRepository;
    
    @Inject
    private AuditChecklistSearchRepository auditChecklistSearchRepository;
    
    /**
     * POST  /audit-checklists : Create a new auditChecklist.
     *
     * @param auditChecklist the auditChecklist to create
     * @return the ResponseEntity with status 201 (Created) and with body the new auditChecklist, or with status 400 (Bad Request) if the auditChecklist has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/audit-checklists",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AuditChecklist> createAuditChecklist(@RequestBody AuditChecklist auditChecklist) throws URISyntaxException {
        log.debug("REST request to save AuditChecklist : {}", auditChecklist);
        if (auditChecklist.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("auditChecklist", "idexists", "A new auditChecklist cannot already have an ID")).body(null);
        }
        AuditChecklist result = auditChecklistRepository.save(auditChecklist);
        auditChecklistSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/audit-checklists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("auditChecklist", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /audit-checklists : Updates an existing auditChecklist.
     *
     * @param auditChecklist the auditChecklist to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated auditChecklist,
     * or with status 400 (Bad Request) if the auditChecklist is not valid,
     * or with status 500 (Internal Server Error) if the auditChecklist couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/audit-checklists",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AuditChecklist> updateAuditChecklist(@RequestBody AuditChecklist auditChecklist) throws URISyntaxException {
        log.debug("REST request to update AuditChecklist : {}", auditChecklist);
        if (auditChecklist.getId() == null) {
            return createAuditChecklist(auditChecklist);
        }
        AuditChecklist result = auditChecklistRepository.save(auditChecklist);
        auditChecklistSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("auditChecklist", auditChecklist.getId().toString()))
            .body(result);
    }

    /**
     * GET  /audit-checklists : get all the auditChecklists.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of auditChecklists in body
     */
    @RequestMapping(value = "/audit-checklists",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<AuditChecklist> getAllAuditChecklists() {
        log.debug("REST request to get all AuditChecklists");
        List<AuditChecklist> auditChecklists = auditChecklistRepository.findAll();
        return auditChecklists;
    }

    /**
     * GET  /audit-checklists/:id : get the "id" auditChecklist.
     *
     * @param id the id of the auditChecklist to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the auditChecklist, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/audit-checklists/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AuditChecklist> getAuditChecklist(@PathVariable Long id) {
        log.debug("REST request to get AuditChecklist : {}", id);
        AuditChecklist auditChecklist = auditChecklistRepository.findOne(id);
        return Optional.ofNullable(auditChecklist)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /audit-checklists/:id : delete the "id" auditChecklist.
     *
     * @param id the id of the auditChecklist to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/audit-checklists/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteAuditChecklist(@PathVariable Long id) {
        log.debug("REST request to delete AuditChecklist : {}", id);
        auditChecklistRepository.delete(id);
        auditChecklistSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("auditChecklist", id.toString())).build();
    }

    /**
     * SEARCH  /_search/audit-checklists?query=:query : search for the auditChecklist corresponding
     * to the query.
     *
     * @param query the query of the auditChecklist search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/audit-checklists",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<AuditChecklist> searchAuditChecklists(@RequestParam String query) {
        log.debug("REST request to search AuditChecklists for query {}", query);
        return StreamSupport
            .stream(auditChecklistSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
