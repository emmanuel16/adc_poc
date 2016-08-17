package com.mycompany.myapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mycompany.myapp.domain.PublishedChecklist;
import com.mycompany.myapp.repository.PublishedChecklistRepository;
import com.mycompany.myapp.repository.search.PublishedChecklistSearchRepository;
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
 * REST controller for managing PublishedChecklist.
 */
@RestController
@RequestMapping("/api")
public class PublishedChecklistResource {

    private final Logger log = LoggerFactory.getLogger(PublishedChecklistResource.class);
        
    @Inject
    private PublishedChecklistRepository publishedChecklistRepository;
    
    @Inject
    private PublishedChecklistSearchRepository publishedChecklistSearchRepository;
    
    /**
     * POST  /published-checklists : Create a new publishedChecklist.
     *
     * @param publishedChecklist the publishedChecklist to create
     * @return the ResponseEntity with status 201 (Created) and with body the new publishedChecklist, or with status 400 (Bad Request) if the publishedChecklist has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/published-checklists",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PublishedChecklist> createPublishedChecklist(@RequestBody PublishedChecklist publishedChecklist) throws URISyntaxException {
        log.debug("REST request to save PublishedChecklist : {}", publishedChecklist);
        if (publishedChecklist.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("publishedChecklist", "idexists", "A new publishedChecklist cannot already have an ID")).body(null);
        }
        PublishedChecklist result = publishedChecklistRepository.save(publishedChecklist);
        publishedChecklistSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/published-checklists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("publishedChecklist", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /published-checklists : Updates an existing publishedChecklist.
     *
     * @param publishedChecklist the publishedChecklist to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated publishedChecklist,
     * or with status 400 (Bad Request) if the publishedChecklist is not valid,
     * or with status 500 (Internal Server Error) if the publishedChecklist couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/published-checklists",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PublishedChecklist> updatePublishedChecklist(@RequestBody PublishedChecklist publishedChecklist) throws URISyntaxException {
        log.debug("REST request to update PublishedChecklist : {}", publishedChecklist);
        if (publishedChecklist.getId() == null) {
            return createPublishedChecklist(publishedChecklist);
        }
        PublishedChecklist result = publishedChecklistRepository.save(publishedChecklist);
        publishedChecklistSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("publishedChecklist", publishedChecklist.getId().toString()))
            .body(result);
    }

    /**
     * GET  /published-checklists : get all the publishedChecklists.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of publishedChecklists in body
     */
    @RequestMapping(value = "/published-checklists",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<PublishedChecklist> getAllPublishedChecklists() {
        log.debug("REST request to get all PublishedChecklists");
        List<PublishedChecklist> publishedChecklists = publishedChecklistRepository.findAll();
        return publishedChecklists;
    }

    /**
     * GET  /published-checklists/:id : get the "id" publishedChecklist.
     *
     * @param id the id of the publishedChecklist to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the publishedChecklist, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/published-checklists/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PublishedChecklist> getPublishedChecklist(@PathVariable Long id) {
        log.debug("REST request to get PublishedChecklist : {}", id);
        PublishedChecklist publishedChecklist = publishedChecklistRepository.findOne(id);
        return Optional.ofNullable(publishedChecklist)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /published-checklists/:id : delete the "id" publishedChecklist.
     *
     * @param id the id of the publishedChecklist to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/published-checklists/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deletePublishedChecklist(@PathVariable Long id) {
        log.debug("REST request to delete PublishedChecklist : {}", id);
        publishedChecklistRepository.delete(id);
        publishedChecklistSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("publishedChecklist", id.toString())).build();
    }

    /**
     * SEARCH  /_search/published-checklists?query=:query : search for the publishedChecklist corresponding
     * to the query.
     *
     * @param query the query of the publishedChecklist search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/published-checklists",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<PublishedChecklist> searchPublishedChecklists(@RequestParam String query) {
        log.debug("REST request to search PublishedChecklists for query {}", query);
        return StreamSupport
            .stream(publishedChecklistSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
