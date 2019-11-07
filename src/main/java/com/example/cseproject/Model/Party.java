package com.example.cseproject.Model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Party {
    @Column(name = "party_id")
    private Integer id;
    private String name;
    private Integer votes;

    @Column(name = "election")
    private String election;
}
