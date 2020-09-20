package com.devserbyn.isalive.model;

import com.devserbyn.isalive.model.enums.EndpointCheckStatus;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table (name = "check_endpoint",
        indexes = {@Index(name = "ui_endpoint_link", columnList = "endpointurl,user_id,archived")})
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

    private LocalDateTime dateCreated = LocalDateTime.now();

    private LocalDateTime dateModified;

    @ManyToOne
    private User user;

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
