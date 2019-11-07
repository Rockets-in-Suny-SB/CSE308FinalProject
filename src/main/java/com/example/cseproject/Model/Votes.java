package com.example.cseproject.Model;

import javax.persistence.*;
import java.util.List;

@Entity
public class Votes {
    @JoinColumn(name = "election")
    private String election; // should be enum type
    private Integer totalVotes;
    private Integer winningPartyId;
    private Integer precinct_id;

    @OneToMany
    @JoinColumn(name = "election", referencedColumnName = "election")
    private List<Party> parties;


}



