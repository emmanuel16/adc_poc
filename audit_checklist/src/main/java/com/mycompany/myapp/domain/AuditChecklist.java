package com.mycompany.myapp.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A AuditChecklist.
 */
@Entity
@Table(name = "audit_checklist")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "auditchecklist")
public class AuditChecklist implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "published_checklist_id")
    private String publishedChecklistId;

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

    @Column(name = "mod_date")
    private LocalDate modDate;

    @Column(name = "last_mod_by")
    private String lastModBy;

    @ManyToOne
    private ClientInfo clientInfo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPublishedChecklistId() {
        return publishedChecklistId;
    }

    public void setPublishedChecklistId(String publishedChecklistId) {
        this.publishedChecklistId = publishedChecklistId;
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

    public LocalDate getModDate() {
        return modDate;
    }

    public void setModDate(LocalDate modDate) {
        this.modDate = modDate;
    }

    public String getLastModBy() {
        return lastModBy;
    }

    public void setLastModBy(String lastModBy) {
        this.lastModBy = lastModBy;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuditChecklist auditChecklist = (AuditChecklist) o;
        if(auditChecklist.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, auditChecklist.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "AuditChecklist{" +
            "id=" + id +
            ", publishedChecklistId='" + publishedChecklistId + "'" +
            ", year='" + year + "'" +
            ", data='" + data + "'" +
            ", grp='" + grp + "'" +
            ", domain='" + domain + "'" +
            ", type='" + type + "'" +
            ", description='" + description + "'" +
            ", modDate='" + modDate + "'" +
            ", lastModBy='" + lastModBy + "'" +
            '}';
    }
}
