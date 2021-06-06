/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.services;

import com.crio.qeats.QEatsApplication;
import com.crio.qeats.dto.Restaurant;
import com.crio.qeats.exchanges.GetRestaurantsRequest;
import com.crio.qeats.exchanges.GetRestaurantsResponse;
import com.crio.qeats.repositoryservices.RestaurantRepositoryService;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class RestaurantServiceImpl implements RestaurantService {

  private final Double peakHoursServingRadiusInKms = 3.0;
  private final Double normalHoursServingRadiusInKms = 5.0;

  @Autowired
  private RestaurantRepositoryService restaurantRepositoryService;

  // TODO: CRIO_TASK_MODULE_RESTAURANTSAPI - Implement findAllRestaurantsCloseby.
  // Check RestaurantService.java file for the interface contract.

  // public static long timeReqData;

  @Override
  public GetRestaurantsResponse findAllRestaurantsCloseBy(
      GetRestaurantsRequest getRestaurantsRequest,
      LocalTime currentTime) {
    List<Restaurant> restaurant;
    int h = currentTime.getHour();
    int m = currentTime.getMinute();
    if ((h >= 8 && h <= 9) || (h == 10 && m == 0) || (h == 13) || (h == 14 && m == 0) 
        || (h >= 19 && h <= 20)
        || (h == 21 && m == 0)) {

      // long startTimeInMillis = System.currentTimeMillis();

      restaurant = restaurantRepositoryService.findAllRestaurantsCloseBy(
        getRestaurantsRequest.getLatitude(),
          getRestaurantsRequest.getLongitude(), currentTime, peakHoursServingRadiusInKms);

      // long endTimeInMillis = System.currentTimeMillis();
      // System.out.println("Data Layer Your function took :"
      // + (endTimeInMillis - startTimeInMillis));
      // timeReqData = endTimeInMillis - startTimeInMillis;

    } else {

      // long startTimeInMillis = System.currentTimeMillis();

      restaurant = restaurantRepositoryService.findAllRestaurantsCloseBy(
        getRestaurantsRequest.getLatitude(),
          getRestaurantsRequest.getLongitude(), currentTime, normalHoursServingRadiusInKms);

      // long endTimeInMillis = System.currentTimeMillis();
      // System.out.println("Data Layer Your function took :"
      // + (endTimeInMillis - startTimeInMillis));
      // timeReqData = endTimeInMillis - startTimeInMillis;

    }

    GetRestaurantsResponse response = new GetRestaurantsResponse(restaurant);
    log.info(response);
    return response;
  }

  

  // TODO: CRIO_TASK_MODULE_RESTAURANTSEARCH
  // Implement findRestaurantsBySearchQuery. The request object has the search
  // string.
  // We have to combine results from multiple sources:
  // 1. Restaurants by name (exact and inexact)
  // 2. Restaurants by cuisines (also called attributes)
  // 3. Restaurants by food items it serves
  // 4. Restaurants by food item attributes (spicy, sweet, etc)
  // Remember, a restaurant must be present only once in the resulting list.
  // Check RestaurantService.java file for the interface contract.
  @Override
  public GetRestaurantsResponse findRestaurantsBySearchQuery(
      GetRestaurantsRequest getRestaurantsRequest,
      LocalTime currentTime) {

    "".isEmpty();
    List<Restaurant> restaurant;
    int h = currentTime.getHour();
    int m = currentTime.getMinute();

    if (getRestaurantsRequest.getSearchFor().equals("")) {

      restaurant = new ArrayList<>();

    } else if ((h >= 8 && h <= 9) || (h == 10 && m == 0) || (h == 13) 
        || (h == 14 && m == 0) || (h >= 19 && h <= 20)
        || (h == 21 && m == 0)) {

      // List<Restaurant> restaurant1 = restaurantRepositoryService
      //     .findRestaurantsByName(getRestaurantsRequest.getLatitude(),
      //     getRestaurantsRequest.getLongitude(),
      //     getRestaurantsRequest.getSearchFor(), currentTime,
      //     peakHoursServingRadiusInKms);
      // List<Restaurant> restaurant2 = restaurantRepositoryService
      //     .findRestaurantsByAttributes(getRestaurantsRequest.getLatitude(),
      //     getRestaurantsRequest.getLongitude(),
      //     getRestaurantsRequest.getSearchFor(), currentTime,
      //     peakHoursServingRadiusInKms);

      final ExecutorService pool = Executors.newFixedThreadPool(20);
      List<Future<List<Restaurant>>> futureReturnsList = new ArrayList<Future<List<Restaurant>>>();

      Callable<List<Restaurant>> callableTask1 = () -> {
        return restaurantRepositoryService.findRestaurantsByName(
          getRestaurantsRequest.getLatitude(),
            getRestaurantsRequest.getLongitude(), getRestaurantsRequest.getSearchFor(), currentTime,
            peakHoursServingRadiusInKms);
      };
      Callable<List<Restaurant>> callableTask2 = () -> {
        return restaurantRepositoryService.findRestaurantsByAttributes(
          getRestaurantsRequest.getLatitude(),
            getRestaurantsRequest.getLongitude(), getRestaurantsRequest.getSearchFor(), currentTime,
            peakHoursServingRadiusInKms);
      };
      futureReturnsList.add(pool.submit(callableTask1));
      futureReturnsList.add(pool.submit(callableTask2));

      List<Restaurant> restaurant1 = new ArrayList<>();
      List<Restaurant> restaurant2 = new ArrayList<>();

      try {
        restaurant1 = futureReturnsList.get(0).get();

        restaurant2 = futureReturnsList.get(1).get();

      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (ExecutionException e) {
        e.printStackTrace();
      }

      

      
      restaurant = getUnionOfLists(restaurant1, restaurant2);

    } else {
      // List<Restaurant> restaurant1 = restaurantRepositoryService
      //     .findRestaurantsByName(getRestaurantsRequest.getLatitude(),
      //     getRestaurantsRequest.getLongitude(),
      //     getRestaurantsRequest.getSearchFor(), currentTime, normalHoursServingRadiusInKms);
      // List<Restaurant> restaurant2 = restaurantRepositoryService
      //     .findRestaurantsByAttributes(getRestaurantsRequest.getLatitude(),
      //     getRestaurantsRequest.getLongitude(),
      //     getRestaurantsRequest.getSearchFor(), currentTime, normalHoursServingRadiusInKms);

      final ExecutorService pool = Executors.newFixedThreadPool(20);
      List<Future<List<Restaurant>>> futureReturnsList = new ArrayList<Future<List<Restaurant>>>();

      Callable<List<Restaurant>> callableTask1 = () -> {
        return restaurantRepositoryService.findRestaurantsByName(
          getRestaurantsRequest.getLatitude(),
            getRestaurantsRequest.getLongitude(), getRestaurantsRequest.getSearchFor(), currentTime,
            normalHoursServingRadiusInKms);
      };
      Callable<List<Restaurant>> callableTask2 = () -> {
        return restaurantRepositoryService.findRestaurantsByAttributes(
          getRestaurantsRequest.getLatitude(),
            getRestaurantsRequest.getLongitude(), getRestaurantsRequest.getSearchFor(), currentTime,
            normalHoursServingRadiusInKms);
      };
      futureReturnsList.add(pool.submit(callableTask1));
      futureReturnsList.add(pool.submit(callableTask2));

      List<Restaurant> restaurant1 = new ArrayList<>();
      List<Restaurant> restaurant2 = new ArrayList<>();

      try {
        restaurant1 = futureReturnsList.get(0).get();

        restaurant2 = futureReturnsList.get(1).get();

      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (ExecutionException e) {
        e.printStackTrace();
      }

      "".isEmpty();

      restaurant = getUnionOfLists(restaurant1, restaurant2);
    }
    GetRestaurantsResponse response = new GetRestaurantsResponse(restaurant);
    log.info(response);
    "".isEmpty();
    return response;
  }

  private List<Restaurant> getUnionOfLists(List<Restaurant> list1, List<Restaurant> list2) {
 
    list1.addAll(list2);

    // convert the arraylist into a set
    Set<Restaurant> set = new LinkedHashSet<>();
    set.addAll(list1);

    // delete al elements of arraylist
    list1.clear();

    // add element from set to arraylist
    list1.addAll(set);

  
    return list1;
  }

}

