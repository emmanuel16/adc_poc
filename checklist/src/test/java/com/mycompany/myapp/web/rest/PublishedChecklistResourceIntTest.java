package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.ChecklistApp;
import com.mycompany.myapp.domain.PublishedChecklist;
import com.mycompany.myapp.repository.PublishedChecklistRepository;
import com.mycompany.myapp.repository.search.PublishedChecklistSearchRepository;

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
 * Test class for the PublishedChecklistResource REST controller.
 *
 * @see PublishedChecklistResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ChecklistApp.class)
@WebAppConfiguration
@IntegrationTest
public class PublishedChecklistResourceIntTest {


    private static final Long DEFAULT_YEAR = 1L;
    private static final Long UPDATED_YEAR = 2L;

    private static final String DEFAULT_DATA = "";
    private static final String UPDATED_DATA = "";
    private static final String DEFAULT_GRP = "AAAAA";
    private static final String UPDATED_GRP = "BBBBB";
    private static final String DEFAULT_DOMAIN = "AAAAA";
    private static final String UPDATED_DOMAIN = "BBBBB";
    private static final String DEFAULT_TYPE = "AAAAA";
    private static final String UPDATED_TYPE = "BBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    private static final Boolean DEFAULT_CURRENT = false;
    private static final Boolean UPDATED_CURRENT = true;

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final LocalDate DEFAULT_PUBLISED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PUBLISED_DATE = LocalDate.now(ZoneId.systemDefault());

    @Inject
    private PublishedChecklistRepository publishedChecklistRepository;

    @Inject
    private PublishedChecklistSearchRepository publishedChecklistSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restPublishedChecklistMockMvc;

    private PublishedChecklist publishedChecklist;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PublishedChecklistResource publishedChecklistResource = new PublishedChecklistResource();
        ReflectionTestUtils.setField(publishedChecklistResource, "publishedChecklistSearchRepository", publishedChecklistSearchRepository);
        ReflectionTestUtils.setField(publishedChecklistResource, "publishedChecklistRepository", publishedChecklistRepository);
        this.restPublishedChecklistMockMvc = MockMvcBuilders.standaloneSetup(publishedChecklistResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        publishedChecklistSearchRepository.deleteAll();
        publishedChecklist = new PublishedChecklist();
        publishedChecklist.setYear(DEFAULT_YEAR);
        publishedChecklist.setData(DEFAULT_DATA);
        publishedChecklist.setGrp(DEFAULT_GRP);
        publishedChecklist.setDomain(DEFAULT_DOMAIN);
        publishedChecklist.setType(DEFAULT_TYPE);
        publishedChecklist.setDescription(DEFAULT_DESCRIPTION);
        publishedChecklist.setCurrent(DEFAULT_CURRENT);
        publishedChecklist.setActive(DEFAULT_ACTIVE);
        publishedChecklist.setPublisedDate(DEFAULT_PUBLISED_DATE);
    }

    @Test
    @Transactional
    public void createPublishedChecklist() throws Exception {
        int databaseSizeBeforeCreate = publishedChecklistRepository.findAll().size();

        // Create the PublishedChecklist

        restPublishedChecklistMockMvc.perform(post("/api/published-checklists")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(publishedChecklist)))
                .andExpect(status().isCreated());

        // Validate the PublishedChecklist in the database
        List<PublishedChecklist> publishedChecklists = publishedChecklistRepository.findAll();
        assertThat(publishedChecklists).hasSize(databaseSizeBeforeCreate + 1);
        PublishedChecklist testPublishedChecklist = publishedChecklists.get(publishedChecklists.size() - 1);
        assertThat(testPublishedChecklist.getYear()).isEqualTo(DEFAULT_YEAR);
        assertThat(testPublishedChecklist.getData()).isEqualTo(DEFAULT_DATA);
        assertThat(testPublishedChecklist.getGrp()).isEqualTo(DEFAULT_GRP);
        assertThat(testPublishedChecklist.getDomain()).isEqualTo(DEFAULT_DOMAIN);
        assertThat(testPublishedChecklist.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testPublishedChecklist.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPublishedChecklist.isCurrent()).isEqualTo(DEFAULT_CURRENT);
        assertThat(testPublishedChecklist.isActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testPublishedChecklist.getPublisedDate()).isEqualTo(DEFAULT_PUBLISED_DATE);

        // Validate the PublishedChecklist in ElasticSearch
        PublishedChecklist publishedChecklistEs = publishedChecklistSearchRepository.findOne(testPublishedChecklist.getId());
        assertThat(publishedChecklistEs).isEqualToComparingFieldByField(testPublishedChecklist);
    }

    @Test
    @Transactional
    public void getAllPublishedChecklists() throws Exception {
        // Initialize the database
        publishedChecklistRepository.saveAndFlush(publishedChecklist);

        // Get all the publishedChecklists
        restPublishedChecklistMockMvc.perform(get("/api/published-checklists?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(publishedChecklist.getId().intValue())))
                .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR.intValue())))
                .andExpect(jsonPath("$.[*].data").value(hasItem(DEFAULT_DATA.toString())))
                .andExpect(jsonPath("$.[*].grp").value(hasItem(DEFAULT_GRP.toString())))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].current").value(hasItem(DEFAULT_CURRENT.booleanValue())))
                .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
                .andExpect(jsonPath("$.[*].publisedDate").value(hasItem(DEFAULT_PUBLISED_DATE.toString())));
    }

    @Test
    @Transactional
    public void getPublishedChecklist() throws Exception {
        // Initialize the database
        publishedChecklistRepository.saveAndFlush(publishedChecklist);

        // Get the publishedChecklist
        restPublishedChecklistMockMvc.perform(get("/api/published-checklists/{id}", publishedChecklist.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(publishedChecklist.getId().intValue()))
            .andExpect(jsonPath("$.year").value(DEFAULT_YEAR.intValue()))
            .andExpect(jsonPath("$.data").value(DEFAULT_DATA.toString()))
            .andExpect(jsonPath("$.grp").value(DEFAULT_GRP.toString()))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.current").value(DEFAULT_CURRENT.booleanValue()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.publisedDate").value(DEFAULT_PUBLISED_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPublishedChecklist() throws Exception {
        // Get the publishedChecklist
        restPublishedChecklistMockMvc.perform(get("/api/published-checklists/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePublishedChecklist() throws Exception {
        // Initialize the database
        publishedChecklistRepository.saveAndFlush(publishedChecklist);
        publishedChecklistSearchRepository.save(publishedChecklist);
        int databaseSizeBeforeUpdate = publishedChecklistRepository.findAll().size();

        // Update the publishedChecklist
        PublishedChecklist updatedPublishedChecklist = new PublishedChecklist();
        updatedPublishedChecklist.setId(publishedChecklist.getId());
        updatedPublishedChecklist.setYear(UPDATED_YEAR);
        updatedPublishedChecklist.setData(UPDATED_DATA);
        updatedPublishedChecklist.setGrp(UPDATED_GRP);
        updatedPublishedChecklist.setDomain(UPDATED_DOMAIN);
        updatedPublishedChecklist.setType(UPDATED_TYPE);
        updatedPublishedChecklist.setDescription(UPDATED_DESCRIPTION);
        updatedPublishedChecklist.setCurrent(UPDATED_CURRENT);
        updatedPublishedChecklist.setActive(UPDATED_ACTIVE);
        updatedPublishedChecklist.setPublisedDate(UPDATED_PUBLISED_DATE);

        restPublishedChecklistMockMvc.perform(put("/api/published-checklists")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedPublishedChecklist)))
                .andExpect(status().isOk());

        // Validate the PublishedChecklist in the database
        List<PublishedChecklist> publishedChecklists = publishedChecklistRepository.findAll();
        assertThat(publishedChecklists).hasSize(databaseSizeBeforeUpdate);
        PublishedChecklist testPublishedChecklist = publishedChecklists.get(publishedChecklists.size() - 1);
        assertThat(testPublishedChecklist.getYear()).isEqualTo(UPDATED_YEAR);
        assertThat(testPublishedChecklist.getData()).isEqualTo(UPDATED_DATA);
        assertThat(testPublishedChecklist.getGrp()).isEqualTo(UPDATED_GRP);
        assertThat(testPublishedChecklist.getDomain()).isEqualTo(UPDATED_DOMAIN);
        assertThat(testPublishedChecklist.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testPublishedChecklist.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPublishedChecklist.isCurrent()).isEqualTo(UPDATED_CURRENT);
        assertThat(testPublishedChecklist.isActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testPublishedChecklist.getPublisedDate()).isEqualTo(UPDATED_PUBLISED_DATE);

        // Validate the PublishedChecklist in ElasticSearch
        PublishedChecklist publishedChecklistEs = publishedChecklistSearchRepository.findOne(testPublishedChecklist.getId());
        assertThat(publishedChecklistEs).isEqualToComparingFieldByField(testPublishedChecklist);
    }

    @Test
    @Transactional
    public void deletePublishedChecklist() throws Exception {
        // Initialize the database
        publishedChecklistRepository.saveAndFlush(publishedChecklist);
        publishedChecklistSearchRepository.save(publishedChecklist);
        int databaseSizeBeforeDelete = publishedChecklistRepository.findAll().size();

        // Get the publishedChecklist
        restPublishedChecklistMockMvc.perform(delete("/api/published-checklists/{id}", publishedChecklist.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean publishedChecklistExistsInEs = publishedChecklistSearchRepository.exists(publishedChecklist.getId());
        assertThat(publishedChecklistExistsInEs).isFalse();

        // Validate the database is empty
        List<PublishedChecklist> publishedChecklists = publishedChecklistRepository.findAll();
        assertThat(publishedChecklists).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPublishedChecklist() throws Exception {
        // Initialize the database
        publishedChecklistRepository.saveAndFlush(publishedChecklist);
        publishedChecklistSearchRepository.save(publishedChecklist);

        // Search the publishedChecklist
        restPublishedChecklistMockMvc.perform(get("/api/_search/published-checklists?query=id:" + publishedChecklist.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(publishedChecklist.getId().intValue())))
            .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR.intValue())))
            .andExpect(jsonPath("$.[*].data").value(hasItem(DEFAULT_DATA.toString())))
            .andExpect(jsonPath("$.[*].grp").value(hasItem(DEFAULT_GRP.toString())))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].current").value(hasItem(DEFAULT_CURRENT.booleanValue())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].publisedDate").value(hasItem(DEFAULT_PUBLISED_DATE.toString())));
    }
}
