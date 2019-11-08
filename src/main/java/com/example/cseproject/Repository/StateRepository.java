package com.example.cseproject.Repository;

import com.example.cseproject.Model.State;
import com.example.cseproject.Model.CompositeKeys.StateId;
import org.springframework.data.repository.CrudRepository;

public interface StateRepository extends CrudRepository <State, StateId> {
}