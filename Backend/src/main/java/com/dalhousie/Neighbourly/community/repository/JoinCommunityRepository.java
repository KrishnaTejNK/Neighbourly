package com.dalhousie.Neighbourly.community.repository;

import com.dalhousie.Neighbourly.helprequest.model.HelpRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JoinCommunityRepository extends JpaRepository<HelpRequest, Integer> {
}
