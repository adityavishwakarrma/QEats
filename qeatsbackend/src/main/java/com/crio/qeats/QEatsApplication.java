/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats;

import com.crio.qeats.globals.GlobalConstants;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@SpringBootApplication
@Log4j2
public class QEatsApplication {

  public static void main(String[] args) {
    SpringApplication.run(QEatsApplication.class, args);

    // TIP:MODULE_RESTAPI: If your server starts successfully,
    // you can find the following message in the logs.
    log.info("Congrats! Your QEatsApplication server has started");
  }

  /**
   * Fetches a ModelMapper instance.
   *
   * @return ModelMapper
   */
  @Bean // Want a new obj every time
  @Scope("prototype")
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

}



//  {
//   "id": "10",
//   "restaurantId": "10",
//   "name": "AndhraSpice",
//   "city": "Hsr Layout",
//   "imageUrl": "https://i.guim.co.uk/img/media/00d44c05221beceb1ebf5d25448fe879f8fde296/0_330_3500_2099/master/3500.jpg?width=1200&height=900&quality=85&auto=format&fit=crop&s=b3b82b2ad8b42ed42c26a52c46abd8a9",
//   "latitude": 20.027,
//   "longitude": 30.0,
//   "attributes": [
//     "Tamil",
//     "South Indian"
//   ]
// }
