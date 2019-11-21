package com.example.cseproject.Service;

import com.example.cseproject.Enum.Election;
import com.example.cseproject.Model.CompositeKeys.VoteId;
import com.example.cseproject.Model.Party;
import com.example.cseproject.Model.Vote;
import com.example.cseproject.Repository.VotesRepository;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.Optional;

@Service
public class VotesService {
    private VotesRepository votesRepository;

    private Iterable<Vote> getAllVotes(){ return votesRepository.findAll(); }

    public Optional<Vote> getVote(Integer id, Election election){
        return votesRepository.findById(new VoteId(id, election));
    }

    public String addVote(Integer id, Set<Party> parties){
        Vote vote = new Vote();
        vote.setId(id);
        Party winningParty = null;
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
