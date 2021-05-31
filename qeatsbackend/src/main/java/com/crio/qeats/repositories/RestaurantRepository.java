/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositories;

import com.crio.qeats.models.RestaurantEntity;
import com.mongodb.connection.Stream;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


public interface RestaurantRepository extends MongoRepository<RestaurantEntity, String> {

  @Query("{name: {$regex: ?0}}")
  List<RestaurantEntity> findingByName(String name);

  @Query("{attributes: {$regex: ?0}}")
  // @Query("{attributes: ?0}")
  List<RestaurantEntity> findingRestaurantsAttributes(String attributes);


  @Query("{name: ?0}")
  Optional<List<RestaurantEntity>> findRestaurantsByNameExact(String name);

}