package com.devserbyn.isalive.model;

import com.devserbyn.isalive.model.enums.EndpointCheckStatus;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table (name = "check_endpoint")
@Getter
@Setter
public class CheckEndpoint {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;

    private String endpointURL;

    private boolean supportsIsAlive = false;

    private EndpointCheckStatus lastCheckStatus;

    private String info;

    private boolean archived = false;

    private Date dateCreated = new Date();

    private Date dateModified;

    @ManyToOne
    private User user;

    public CheckEndpoint() { }

    public CheckEndpoint(String endpointURL, boolean supportsIsAlive) {
        this.endpointURL = endpointURL;
        this.supportsIsAlive = supportsIsAlive;
    }

    public boolean differentFrom(CheckEndpoint checkEndpoint) {
        return this.id != checkEndpoint.getId() ||
               !this.endpointURL.equals(checkEndpoint.getEndpointURL()) ||
               supportsIsAlive != checkEndpoint.isSupportsIsAlive() ||
               lastCheckStatus != checkEndpoint.getLastCheckStatus() ||
               archived != checkEndpoint.isArchived() ||
               dateCreated != checkEndpoint.getDateCreated() ||
               user.getId() != checkEndpoint.getUser().getId();
    }
}