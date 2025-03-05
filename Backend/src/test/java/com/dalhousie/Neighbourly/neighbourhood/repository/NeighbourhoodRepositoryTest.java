package com.dalhousie.Neighbourly.neighbourhood.repository;

import com.dalhousie.Neighbourly.neighbourhood.entity.Neighbourhood;
import com.dalhousie.Neighbourly.neighbourhood.service.NeighbourhoodService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class NeighbourhoodRepositoryTest {

    @Mock
    private NeighbourhoodRepository neighbourhoodRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testFindAllNeighbourhoods() {
//        // Given
//        Neighbourhood neighbourhood1 = new Neighbourhood();
//        neighbourhood1.setName("Community A");
//        neighbourhood1.setLocation("Location A");
//
//        Neighbourhood neighbourhood2 = new Neighbourhood();
//        neighbourhood2.setName("Community B");
//        neighbourhood2.setLocation("Location B");
//
//        neighbourhoodRepository.save(neighbourhood1);
//        neighbourhoodRepository.save(neighbourhood2);
//
//        // When
//        List<Neighbourhood> neighbourhoods = neighbourhoodRepository.findAll();
//
//        System.out.println(neighbourhoods);
//        // Then
//        assertThat(neighbourhoods).isNotEmpty();
//        assertThat(neighbourhoods.size()).isEqualTo(2);
    }
}
