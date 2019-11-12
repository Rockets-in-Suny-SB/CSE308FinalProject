package com.example.cseproject.Service;

import com.example.cseproject.Enum.Election;
import com.example.cseproject.Enum.PartyName;
import com.example.cseproject.Model.Party;
import com.example.cseproject.Repository.PartyRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PartyService {
    private PartyRepository partyRepository;

    public Iterable<Party> getAllParty(){
        return partyRepository.findAll();
    }

    public Optional<Party> getParty(Integer id){
        return partyRepository.findById(id);
    }

    public String addParty(Integer id, PartyName name, Integer votes, Election election){
        Party party = new Party();
        party.setId(id);
        party.setElection(election);
        party.setName(name);
        party.setVotes(votes);
        partyRepository.save(party);
        return "Saved";
    }

    public String deleteParty(Party party){
        partyRepository.delete(party);
        return "Deleted";
    }


}
