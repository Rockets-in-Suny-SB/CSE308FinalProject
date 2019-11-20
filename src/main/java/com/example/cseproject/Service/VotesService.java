package com.example.cseproject.Service;

import com.example.cseproject.Enum.Election;
import com.example.cseproject.Model.Party;
import com.example.cseproject.Model.Vote;
import com.example.cseproject.Repository.VotesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VotesService {
    private VotesRepository votesRepository;

    private Iterable<Vote> getAllVotes(){ return votesRepository.findAll(); }

    public Optional<Vote> getVote(Integer id){
        return votesRepository.findById(id);
    }

    public String addVote(Integer id, List<Party> parties){
        Vote vote = new Vote();
        vote.setId(id);
        Party winningParty = parties.get(0);
        Integer totalVotes = 0;
        for (Party party : parties){
            totalVotes += party.getVotes();
            if (party.getVotes() > winningParty.getVotes())
                winningParty = party;
        }
        vote.setWinningPartyName(winningParty.getName());
        vote.setTotalVotes(totalVotes);
        vote.setWinningVotes(winningParty.getVotes());
        vote.setParties(parties);
        votesRepository.save(vote);
        return "Saved";
    }

    public String deleteVote(Vote vote){
        votesRepository.delete(vote);
        return "Deleted";
    }
}
