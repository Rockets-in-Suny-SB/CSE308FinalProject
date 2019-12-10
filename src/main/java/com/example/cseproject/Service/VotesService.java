package com.example.cseproject.Service;

import com.example.cseproject.Enum.Election;
import com.example.cseproject.Model.CompositeKeys.VoteId;
import com.example.cseproject.Model.Vote;
import com.example.cseproject.Repository.VotesRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VotesService {
    private VotesRepository votesRepository;

    private Iterable<Vote> getAllVotes() {
        return votesRepository.findAll();
    }

    public Optional<Vote> getVote(Integer id, Election election) {
        return votesRepository.findById(new VoteId(election, id));
    }


    public String deleteVote(Vote vote) {
        votesRepository.delete(vote);
        return "Deleted";
    }
}
