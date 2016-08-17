package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.AuditChecklistApp;
import com.mycompany.myapp.domain.AuditChecklist;
import com.mycompany.myapp.repository.AuditChecklistRepository;
import com.mycompany.myapp.repository.search.AuditChecklistSearchRepository;

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
import org.springframework.util.Base64Utils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the AuditChecklistResource REST controller.
 *
 * @see AuditChecklistResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AuditChecklistApp.class)
@WebAppConfiguration
@IntegrationTest
public class AuditChecklistResourceIntTest {

    private static final String DEFAULT_PUBLISHED_CHECKLIST_ID = "AAAAA";
    private static final String UPDATED_PUBLISHED_CHECKLIST_ID = "BBBBB";

    private static final Long DEFAULT_YEAR = 1L;
    private static final Long UPDATED_YEAR = 2L;

    private static final String DEFAULT_DATA = "AAAAA";
    private static final String UPDATED_DATA = "BBBBB";
    private static final String DEFAULT_GRP = "AAAAA";
    private static final String UPDATED_GRP = "BBBBB";
    private static final String DEFAULT_DOMAIN = "AAAAA";
    private static final String UPDATED_DOMAIN = "BBBBB";
    private static final String DEFAULT_TYPE = "AAAAA";
    private static final String UPDATED_TYPE = "BBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    private static final LocalDate DEFAULT_MOD_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_MOD_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final String DEFAULT_LAST_MOD_BY = "AAAAA";
    private static final String UPDATED_LAST_MOD_BY = "BBBBB";

    @Inject
    private AuditChecklistRepository auditChecklistRepository;

    @Inject
    private AuditChecklistSearchRepository auditChecklistSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restAuditChecklistMockMvc;

    private AuditChecklist auditChecklist;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AuditChecklistResource auditChecklistResource = new AuditChecklistResource();
        ReflectionTestUtils.setField(auditChecklistResource, "auditChecklistSearchRepository", auditChecklistSearchRepository);
        ReflectionTestUtils.setField(auditChecklistResource, "auditChecklistRepository", auditChecklistRepository);
        this.restAuditChecklistMockMvc = MockMvcBuilders.standaloneSetup(auditChecklistResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        auditChecklistSearchRepository.deleteAll();
        auditChecklist = new AuditChecklist();
        auditChecklist.setPublishedChecklistId(DEFAULT_PUBLISHED_CHECKLIST_ID);
        auditChecklist.setYear(DEFAULT_YEAR);
        auditChecklist.setData(DEFAULT_DATA);
        auditChecklist.setGrp(DEFAULT_GRP);
        auditChecklist.setDomain(DEFAULT_DOMAIN);
        auditChecklist.setType(DEFAULT_TYPE);
        auditChecklist.setDescription(DEFAULT_DESCRIPTION);
        auditChecklist.setModDate(DEFAULT_MOD_DATE);
        auditChecklist.setLastModBy(DEFAULT_LAST_MOD_BY);
    }

    @Test
    @Transactional
    public void createAuditChecklist() throws Exception {
        int databaseSizeBeforeCreate = auditChecklistRepository.findAll().size();

        // Create the AuditChecklist

        restAuditChecklistMockMvc.perform(post("/api/audit-checklists")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(auditChecklist)))
                .andExpect(status().isCreated());

        // Validate the AuditChecklist in the database
        List<AuditChecklist> auditChecklists = auditChecklistRepository.findAll();
        assertThat(auditChecklists).hasSize(databaseSizeBeforeCreate + 1);
        AuditChecklist testAuditChecklist = auditChecklists.get(auditChecklists.size() - 1);
        assertThat(testAuditChecklist.getPublishedChecklistId()).isEqualTo(DEFAULT_PUBLISHED_CHECKLIST_ID);
        assertThat(testAuditChecklist.getYear()).isEqualTo(DEFAULT_YEAR);
        assertThat(testAuditChecklist.getData()).isEqualTo(DEFAULT_DATA);
        assertThat(testAuditChecklist.getGrp()).isEqualTo(DEFAULT_GRP);
        assertThat(testAuditChecklist.getDomain()).isEqualTo(DEFAULT_DOMAIN);
        assertThat(testAuditChecklist.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testAuditChecklist.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testAuditChecklist.getModDate()).isEqualTo(DEFAULT_MOD_DATE);
        assertThat(testAuditChecklist.getLastModBy()).isEqualTo(DEFAULT_LAST_MOD_BY);

        // Validate the AuditChecklist in ElasticSearch
        AuditChecklist auditChecklistEs = auditChecklistSearchRepository.findOne(testAuditChecklist.getId());
        assertThat(auditChecklistEs).isEqualToComparingFieldByField(testAuditChecklist);
    }

    @Test
    @Transactional
    public void getAllAuditChecklists() throws Exception {
        // Initialize the database
        auditChecklistRepository.saveAndFlush(auditChecklist);

        // Get all the auditChecklists
        restAuditChecklistMockMvc.perform(get("/api/audit-checklists?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(auditChecklist.getId().intValue())))
                .andExpect(jsonPath("$.[*].publishedChecklistId").value(hasItem(DEFAULT_PUBLISHED_CHECKLIST_ID.toString())))
                .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR.intValue())))
                .andExpect(jsonPath("$.[*].data").value(hasItem(DEFAULT_DATA.toString())))
                .andExpect(jsonPath("$.[*].grp").value(hasItem(DEFAULT_GRP.toString())))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].modDate").value(hasItem(DEFAULT_MOD_DATE.toString())))
                .andExpect(jsonPath("$.[*].lastModBy").value(hasItem(DEFAULT_LAST_MOD_BY.toString())));
    }

    @Test
    @Transactional
    public void getAuditChecklist() throws Exception {
        // Initialize the database
        auditChecklistRepository.saveAndFlush(auditChecklist);

        // Get the auditChecklist
        restAuditChecklistMockMvc.perform(get("/api/audit-checklists/{id}", auditChecklist.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(auditChecklist.getId().intValue()))
            .andExpect(jsonPath("$.publishedChecklistId").value(DEFAULT_PUBLISHED_CHECKLIST_ID.toString()))
            .andExpect(jsonPath("$.year").value(DEFAULT_YEAR.intValue()))
            .andExpect(jsonPath("$.data").value(DEFAULT_DATA.toString()))
            .andExpect(jsonPath("$.grp").value(DEFAULT_GRP.toString()))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.modDate").value(DEFAULT_MOD_DATE.toString()))
            .andExpect(jsonPath("$.lastModBy").value(DEFAULT_LAST_MOD_BY.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAuditChecklist() throws Exception {
        // Get the auditChecklist
        restAuditChecklistMockMvc.perform(get("/api/audit-checklists/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAuditChecklist() throws Exception {
        // Initialize the database
        auditChecklistRepository.saveAndFlush(auditChecklist);
        auditChecklistSearchRepository.save(auditChecklist);
        int databaseSizeBeforeUpdate = auditChecklistRepository.findAll().size();

        // Update the auditChecklist
        AuditChecklist updatedAuditChecklist = new AuditChecklist();
        updatedAuditChecklist.setId(auditChecklist.getId());
        updatedAuditChecklist.setPublishedChecklistId(UPDATED_PUBLISHED_CHECKLIST_ID);
        updatedAuditChecklist.setYear(UPDATED_YEAR);
        updatedAuditChecklist.setData(UPDATED_DATA);
        updatedAuditChecklist.setGrp(UPDATED_GRP);
        updatedAuditChecklist.setDomain(UPDATED_DOMAIN);
        updatedAuditChecklist.setType(UPDATED_TYPE);
        updatedAuditChecklist.setDescription(UPDATED_DESCRIPTION);
        updatedAuditChecklist.setModDate(UPDATED_MOD_DATE);
        updatedAuditChecklist.setLastModBy(UPDATED_LAST_MOD_BY);

        restAuditChecklistMockMvc.perform(put("/api/audit-checklists")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedAuditChecklist)))
                .andExpect(status().isOk());

        // Validate the AuditChecklist in the database
        List<AuditChecklist> auditChecklists = auditChecklistRepository.findAll();
        assertThat(auditChecklists).hasSize(databaseSizeBeforeUpdate);
        AuditChecklist testAuditChecklist = auditChecklists.get(auditChecklists.size() - 1);
        assertThat(testAuditChecklist.getPublishedChecklistId()).isEqualTo(UPDATED_PUBLISHED_CHECKLIST_ID);
        assertThat(testAuditChecklist.getYear()).isEqualTo(UPDATED_YEAR);
        assertThat(testAuditChecklist.getData()).isEqualTo(UPDATED_DATA);
        assertThat(testAuditChecklist.getGrp()).isEqualTo(UPDATED_GRP);
        assertThat(testAuditChecklist.getDomain()).isEqualTo(UPDATED_DOMAIN);
        assertThat(testAuditChecklist.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testAuditChecklist.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAuditChecklist.getModDate()).isEqualTo(UPDATED_MOD_DATE);
        assertThat(testAuditChecklist.getLastModBy()).isEqualTo(UPDATED_LAST_MOD_BY);

        // Validate the AuditChecklist in ElasticSearch
        AuditChecklist auditChecklistEs = auditChecklistSearchRepository.findOne(testAuditChecklist.getId());
        assertThat(auditChecklistEs).isEqualToComparingFieldByField(testAuditChecklist);
    }

    @Test
    @Transactional
    public void deleteAuditChecklist() throws Exception {
        // Initialize the database
        auditChecklistRepository.saveAndFlush(auditChecklist);
        auditChecklistSearchRepository.save(auditChecklist);
        int databaseSizeBeforeDelete = auditChecklistRepository.findAll().size();

        // Get the auditChecklist
        restAuditChecklistMockMvc.perform(delete("/api/audit-checklists/{id}", auditChecklist.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean auditChecklistExistsInEs = auditChecklistSearchRepository.exists(auditChecklist.getId());
        assertThat(auditChecklistExistsInEs).isFalse();

        // Validate the database is empty
        List<AuditChecklist> auditChecklists = auditChecklistRepository.findAll();
        assertThat(auditChecklists).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchAuditChecklist() throws Exception {
        // Initialize the database
        auditChecklistRepository.saveAndFlush(auditChecklist);
        auditChecklistSearchRepository.save(auditChecklist);

        // Search the auditChecklist
        restAuditChecklistMockMvc.perform(get("/api/_search/audit-checklists?query=id:" + auditChecklist.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(auditChecklist.getId().intValue())))
            .andExpect(jsonPath("$.[*].publishedChecklistId").value(hasItem(DEFAULT_PUBLISHED_CHECKLIST_ID.toString())))
            .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR.intValue())))
            .andExpect(jsonPath("$.[*].data").value(hasItem(DEFAULT_DATA.toString())))
            .andExpect(jsonPath("$.[*].grp").value(hasItem(DEFAULT_GRP.toString())))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].modDate").value(hasItem(DEFAULT_MOD_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModBy").value(hasItem(DEFAULT_LAST_MOD_BY.toString())));
    }
}
