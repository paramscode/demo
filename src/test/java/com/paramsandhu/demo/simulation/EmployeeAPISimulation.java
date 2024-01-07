package com.paramsandhu.demo.simulation;


import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class EmployeeAPISimulation extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    private ScenarioBuilder scenario = scenario("Employee API Scenario")
            .exec(http("Get all Employees")
                    .get("/api/employees")
                    .check(status().is(200)));


    {
        setUp(
                scenario.injectOpen(rampUsers(100).during(10))
        ).protocols(httpProtocol);
    }


}
