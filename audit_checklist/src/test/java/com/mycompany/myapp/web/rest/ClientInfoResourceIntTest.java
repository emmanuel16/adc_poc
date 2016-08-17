package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.AuditChecklistApp;
import com.mycompany.myapp.domain.ClientInfo;
import com.mycompany.myapp.repository.ClientInfoRepository;
import com.mycompany.myapp.repository.search.ClientInfoSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the ClientInfoResource REST controller.
 *
 * @see ClientInfoResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AuditChecklistApp.class)
@WebAppConfiguration
@IntegrationTest
public class ClientInfoResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    @Inject
    private ClientInfoRepository clientInfoRepository;

    @Inject
    private ClientInfoSearchRepository clientInfoSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restClientInfoMockMvc;

    private ClientInfo clientInfo;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ClientInfoResource clientInfoResource = new ClientInfoResource();
        ReflectionTestUtils.setField(clientInfoResource, "clientInfoSearchRepository", clientInfoSearchRepository);
        ReflectionTestUtils.setField(clientInfoResource, "clientInfoRepository", clientInfoRepository);
        this.restClientInfoMockMvc = MockMvcBuilders.standaloneSetup(clientInfoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        clientInfoSearchRepository.deleteAll();
        clientInfo = new ClientInfo();
        clientInfo.setName(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createClientInfo() throws Exception {
        int databaseSizeBeforeCreate = clientInfoRepository.findAll().size();

        // Create the ClientInfo

        restClientInfoMockMvc.perform(post("/api/client-infos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(clientInfo)))
                .andExpect(status().isCreated());

        // Validate the ClientInfo in the database
        List<ClientInfo> clientInfos = clientInfoRepository.findAll();
        assertThat(clientInfos).hasSize(databaseSizeBeforeCreate + 1);
        ClientInfo testClientInfo = clientInfos.get(clientInfos.size() - 1);
        assertThat(testClientInfo.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the ClientInfo in ElasticSearch
        ClientInfo clientInfoEs = clientInfoSearchRepository.findOne(testClientInfo.getId());
        assertThat(clientInfoEs).isEqualToComparingFieldByField(testClientInfo);
    }

    @Test
    @Transactional
    public void getAllClientInfos() throws Exception {
        // Initialize the database
        clientInfoRepository.saveAndFlush(clientInfo);

        // Get all the clientInfos
        restClientInfoMockMvc.perform(get("/api/client-infos?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(clientInfo.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getClientInfo() throws Exception {
        // Initialize the database
        clientInfoRepository.saveAndFlush(clientInfo);

        // Get the clientInfo
        restClientInfoMockMvc.perform(get("/api/client-infos/{id}", clientInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(clientInfo.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingClientInfo() throws Exception {
        // Get the clientInfo
        restClientInfoMockMvc.perform(get("/api/client-infos/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateClientInfo() throws Exception {
        // Initialize the database
        clientInfoRepository.saveAndFlush(clientInfo);
        clientInfoSearchRepository.save(clientInfo);
        int databaseSizeBeforeUpdate = clientInfoRepository.findAll().size();

        // Update the clientInfo
        ClientInfo updatedClientInfo = new ClientInfo();
        updatedClientInfo.setId(clientInfo.getId());
        updatedClientInfo.setName(UPDATED_NAME);

        restClientInfoMockMvc.perform(put("/api/client-infos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedClientInfo)))
                .andExpect(status().isOk());

        // Validate the ClientInfo in the database
        List<ClientInfo> clientInfos = clientInfoRepository.findAll();
        assertThat(clientInfos).hasSize(databaseSizeBeforeUpdate);
        ClientInfo testClientInfo = clientInfos.get(clientInfos.size() - 1);
        assertThat(testClientInfo.getName()).isEqualTo(UPDATED_NAME);

        // Validate the ClientInfo in ElasticSearch
        ClientInfo clientInfoEs = clientInfoSearchRepository.findOne(testClientInfo.getId());
        assertThat(clientInfoEs).isEqualToComparingFieldByField(testClientInfo);
    }

    @Test
    @Transactional
    public void deleteClientInfo() throws Exception {
        // Initialize the database
        clientInfoRepository.saveAndFlush(clientInfo);
        clientInfoSearchRepository.save(clientInfo);
        int databaseSizeBeforeDelete = clientInfoRepository.findAll().size();

        // Get the clientInfo
        restClientInfoMockMvc.perform(delete("/api/client-infos/{id}", clientInfo.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean clientInfoExistsInEs = clientInfoSearchRepository.exists(clientInfo.getId());
        assertThat(clientInfoExistsInEs).isFalse();

        // Validate the database is empty
        List<ClientInfo> clientInfos = clientInfoRepository.findAll();
        assertThat(clientInfos).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchClientInfo() throws Exception {
        // Initialize the database
        clientInfoRepository.saveAndFlush(clientInfo);
        clientInfoSearchRepository.save(clientInfo);

        // Search the clientInfo
        restClientInfoMockMvc.perform(get("/api/_search/client-infos?query=id:" + clientInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clientInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
}
