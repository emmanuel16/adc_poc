package com.mycompany.myapp.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A PublishedChecklist.
 */
@Entity
@Table(name = "published_checklist")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "publishedchecklist")
public class PublishedChecklist implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "year")
    private Long year;

    @Lob
    @Column(name = "data")
    private String data;

    @Column(name = "grp")
    private String grp;

    @Column(name = "domain")
    private String domain;

    @Column(name = "type")
    private String type;

    @Column(name = "description")
    private String description;

    @Column(name = "current")
    private Boolean current;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "publised_date")
    private LocalDate publisedDate;

    @ManyToOne
    private Checklist checklist;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getGrp() {
        return grp;
    }

    public void setGrp(String grp) {
        this.grp = grp;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isCurrent() {
        return current;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDate getPublisedDate() {
        return publisedDate;
    }

    public void setPublisedDate(LocalDate publisedDate) {
        this.publisedDate = publisedDate;
    }

    public Checklist getChecklist() {
        return checklist;
    }

    public void setChecklist(Checklist checklist) {
        this.checklist = checklist;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PublishedChecklist publishedChecklist = (PublishedChecklist) o;
        if(publishedChecklist.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, publishedChecklist.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "PublishedChecklist{" +
            "id=" + id +
            ", year='" + year + "'" +
            ", data='" + data + "'" +
            ", grp='" + grp + "'" +
            ", domain='" + domain + "'" +
            ", type='" + type + "'" +
            ", description='" + description + "'" +
            ", current='" + current + "'" +
            ", active='" + active + "'" +
            ", publisedDate='" + publisedDate + "'" +
            '}';
    }
}
