package com.mycompany.myapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mycompany.myapp.domain.ClientInfo;
import com.mycompany.myapp.repository.ClientInfoRepository;
import com.mycompany.myapp.repository.search.ClientInfoSearchRepository;
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
 * REST controller for managing ClientInfo.
 */
@RestController
@RequestMapping("/api")
public class ClientInfoResource {

    private final Logger log = LoggerFactory.getLogger(ClientInfoResource.class);
        
    @Inject
    private ClientInfoRepository clientInfoRepository;
    
    @Inject
    private ClientInfoSearchRepository clientInfoSearchRepository;
    
    /**
     * POST  /client-infos : Create a new clientInfo.
     *
     * @param clientInfo the clientInfo to create
     * @return the ResponseEntity with status 201 (Created) and with body the new clientInfo, or with status 400 (Bad Request) if the clientInfo has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/client-infos",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ClientInfo> createClientInfo(@RequestBody ClientInfo clientInfo) throws URISyntaxException {
        log.debug("REST request to save ClientInfo : {}", clientInfo);
        if (clientInfo.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("clientInfo", "idexists", "A new clientInfo cannot already have an ID")).body(null);
        }
        ClientInfo result = clientInfoRepository.save(clientInfo);
        clientInfoSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/client-infos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("clientInfo", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /client-infos : Updates an existing clientInfo.
     *
     * @param clientInfo the clientInfo to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated clientInfo,
     * or with status 400 (Bad Request) if the clientInfo is not valid,
     * or with status 500 (Internal Server Error) if the clientInfo couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/client-infos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ClientInfo> updateClientInfo(@RequestBody ClientInfo clientInfo) throws URISyntaxException {
        log.debug("REST request to update ClientInfo : {}", clientInfo);
        if (clientInfo.getId() == null) {
            return createClientInfo(clientInfo);
        }
        ClientInfo result = clientInfoRepository.save(clientInfo);
        clientInfoSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("clientInfo", clientInfo.getId().toString()))
            .body(result);
    }

    /**
     * GET  /client-infos : get all the clientInfos.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of clientInfos in body
     */
    @RequestMapping(value = "/client-infos",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ClientInfo> getAllClientInfos() {
        log.debug("REST request to get all ClientInfos");
        List<ClientInfo> clientInfos = clientInfoRepository.findAll();
        return clientInfos;
    }

    /**
     * GET  /client-infos/:id : get the "id" clientInfo.
     *
     * @param id the id of the clientInfo to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the clientInfo, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/client-infos/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ClientInfo> getClientInfo(@PathVariable Long id) {
        log.debug("REST request to get ClientInfo : {}", id);
        ClientInfo clientInfo = clientInfoRepository.findOne(id);
        return Optional.ofNullable(clientInfo)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /client-infos/:id : delete the "id" clientInfo.
     *
     * @param id the id of the clientInfo to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/client-infos/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteClientInfo(@PathVariable Long id) {
        log.debug("REST request to delete ClientInfo : {}", id);
        clientInfoRepository.delete(id);
        clientInfoSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("clientInfo", id.toString())).build();
    }

    /**
     * SEARCH  /_search/client-infos?query=:query : search for the clientInfo corresponding
     * to the query.
     *
     * @param query the query of the clientInfo search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/client-infos",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ClientInfo> searchClientInfos(@RequestParam String query) {
        log.debug("REST request to search ClientInfos for query {}", query);
        return StreamSupport
            .stream(clientInfoSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
