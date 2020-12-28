package com.canu.repositories;

import com.canu.model.CanIModel;
import com.canu.model.CityModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<CityModel, Long> {

    List<CityModel> findByCountryId(String countryId);
}
