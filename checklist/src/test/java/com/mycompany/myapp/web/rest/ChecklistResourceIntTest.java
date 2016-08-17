package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.ChecklistApp;
import com.mycompany.myapp.domain.Checklist;
import com.mycompany.myapp.repository.ChecklistRepository;
import com.mycompany.myapp.repository.search.ChecklistSearchRepository;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the ChecklistResource REST controller.
 *
 * @see ChecklistResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ChecklistApp.class)
@WebAppConfiguration
@IntegrationTest
public class ChecklistResourceIntTest {


    private static final Long DEFAULT_YEAR = 1L;
    private static final Long UPDATED_YEAR = 2L;
    private static final String DEFAULT_TYPE = "AAAAA";
    private static final String UPDATED_TYPE = "BBBBB";

    private static final String DEFAULT_DATA = "AAAAA";
    private static final String UPDATED_DATA = "BBBBB";
    private static final String DEFAULT_DOMAIN = "AAAAA";
    private static final String UPDATED_DOMAIN = "BBBBB";
    private static final String DEFAULT_GRP = "AAAAA";
    private static final String UPDATED_GRP = "BBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    @Inject
    private ChecklistRepository checklistRepository;

    @Inject
    private ChecklistSearchRepository checklistSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restChecklistMockMvc;

    private Checklist checklist;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ChecklistResource checklistResource = new ChecklistResource();
        ReflectionTestUtils.setField(checklistResource, "checklistSearchRepository", checklistSearchRepository);
        ReflectionTestUtils.setField(checklistResource, "checklistRepository", checklistRepository);
        this.restChecklistMockMvc = MockMvcBuilders.standaloneSetup(checklistResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        checklistSearchRepository.deleteAll();
        checklist = new Checklist();
        checklist.setYear(DEFAULT_YEAR);
        checklist.setType(DEFAULT_TYPE);
        checklist.setData(DEFAULT_DATA);
        checklist.setDomain(DEFAULT_DOMAIN);
        checklist.setGrp(DEFAULT_GRP);
        checklist.setDescription(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createChecklist() throws Exception {
        int databaseSizeBeforeCreate = checklistRepository.findAll().size();

        // Create the Checklist

        restChecklistMockMvc.perform(post("/api/checklists")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(checklist)))
                .andExpect(status().isCreated());

        // Validate the Checklist in the database
        List<Checklist> checklists = checklistRepository.findAll();
        assertThat(checklists).hasSize(databaseSizeBeforeCreate + 1);
        Checklist testChecklist = checklists.get(checklists.size() - 1);
        assertThat(testChecklist.getYear()).isEqualTo(DEFAULT_YEAR);
        assertThat(testChecklist.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testChecklist.getData()).isEqualTo(DEFAULT_DATA);
        assertThat(testChecklist.getDomain()).isEqualTo(DEFAULT_DOMAIN);
        assertThat(testChecklist.getGrp()).isEqualTo(DEFAULT_GRP);
        assertThat(testChecklist.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the Checklist in ElasticSearch
        Checklist checklistEs = checklistSearchRepository.findOne(testChecklist.getId());
        assertThat(checklistEs).isEqualToComparingFieldByField(testChecklist);
    }

    @Test
    @Transactional
    public void getAllChecklists() throws Exception {
        // Initialize the database
        checklistRepository.saveAndFlush(checklist);

        // Get all the checklists
        restChecklistMockMvc.perform(get("/api/checklists?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(checklist.getId().intValue())))
                .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR.intValue())))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].data").value(hasItem(DEFAULT_DATA.toString())))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())))
                .andExpect(jsonPath("$.[*].grp").value(hasItem(DEFAULT_GRP.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getChecklist() throws Exception {
        // Initialize the database
        checklistRepository.saveAndFlush(checklist);

        // Get the checklist
        restChecklistMockMvc.perform(get("/api/checklists/{id}", checklist.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(checklist.getId().intValue()))
            .andExpect(jsonPath("$.year").value(DEFAULT_YEAR.intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.data").value(DEFAULT_DATA.toString()))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()))
            .andExpect(jsonPath("$.grp").value(DEFAULT_GRP.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingChecklist() throws Exception {
        // Get the checklist
        restChecklistMockMvc.perform(get("/api/checklists/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateChecklist() throws Exception {
        // Initialize the database
        checklistRepository.saveAndFlush(checklist);
        checklistSearchRepository.save(checklist);
        int databaseSizeBeforeUpdate = checklistRepository.findAll().size();

        // Update the checklist
        Checklist updatedChecklist = new Checklist();
        updatedChecklist.setId(checklist.getId());
        updatedChecklist.setYear(UPDATED_YEAR);
        updatedChecklist.setType(UPDATED_TYPE);
        updatedChecklist.setData(UPDATED_DATA);
        updatedChecklist.setDomain(UPDATED_DOMAIN);
        updatedChecklist.setGrp(UPDATED_GRP);
        updatedChecklist.setDescription(UPDATED_DESCRIPTION);

        restChecklistMockMvc.perform(put("/api/checklists")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedChecklist)))
                .andExpect(status().isOk());

        // Validate the Checklist in the database
        List<Checklist> checklists = checklistRepository.findAll();
        assertThat(checklists).hasSize(databaseSizeBeforeUpdate);
        Checklist testChecklist = checklists.get(checklists.size() - 1);
        assertThat(testChecklist.getYear()).isEqualTo(UPDATED_YEAR);
        assertThat(testChecklist.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testChecklist.getData()).isEqualTo(UPDATED_DATA);
        assertThat(testChecklist.getDomain()).isEqualTo(UPDATED_DOMAIN);
        assertThat(testChecklist.getGrp()).isEqualTo(UPDATED_GRP);
        assertThat(testChecklist.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the Checklist in ElasticSearch
        Checklist checklistEs = checklistSearchRepository.findOne(testChecklist.getId());
        assertThat(checklistEs).isEqualToComparingFieldByField(testChecklist);
    }

    @Test
    @Transactional
    public void deleteChecklist() throws Exception {
        // Initialize the database
        checklistRepository.saveAndFlush(checklist);
        checklistSearchRepository.save(checklist);
        int databaseSizeBeforeDelete = checklistRepository.findAll().size();

        // Get the checklist
        restChecklistMockMvc.perform(delete("/api/checklists/{id}", checklist.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean checklistExistsInEs = checklistSearchRepository.exists(checklist.getId());
        assertThat(checklistExistsInEs).isFalse();

        // Validate the database is empty
        List<Checklist> checklists = checklistRepository.findAll();
        assertThat(checklists).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchChecklist() throws Exception {
        // Initialize the database
        checklistRepository.saveAndFlush(checklist);
        checklistSearchRepository.save(checklist);

        // Search the checklist
        restChecklistMockMvc.perform(get("/api/_search/checklists?query=id:" + checklist.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(checklist.getId().intValue())))
            .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR.intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].data").value(hasItem(DEFAULT_DATA.toString())))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())))
            .andExpect(jsonPath("$.[*].grp").value(hasItem(DEFAULT_GRP.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }
}
