package com.example.cseproject.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Cluster {
    @Id
    @Column(name = "cluster_id")
    private Integer id;

    /* Should it created during phase I or II or Should we create a Cluster_Votes instead of Vote we have?
       One Entity(Vote) have multiple foreign keys(Precinct, Cluster)
    private List<Vote> votes;
    */

}
