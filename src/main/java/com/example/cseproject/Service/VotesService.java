package com.example.cseproject.Service;

import com.example.cseproject.Enum.Election;
import com.example.cseproject.Model.CompositeKeys.VoteId;
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

    public Optional<Vote> getVote(Election election, Integer precinct_id){
        return votesRepository.findById(new VoteId(election,precinct_id));
    }

    public String addVote(Election election, Integer precinct_id, Integer totalVotes,
                          Integer winningPartyId, List<Party> parties){
        Vote vote = new Vote();
        vote.setElection(election);
        vote.setPrecinct_id(precinct_id);
        vote.setTotalVotes(totalVotes);
        vote.setWinningPartyId(winningPartyId);
        vote.setParties(parties);
        votesRepository.save(vote);
        return "Saved";
    }

    public String deleteVote(Vote vote){
        votesRepository.delete(vote);
        return "Deleted";
    }
}
