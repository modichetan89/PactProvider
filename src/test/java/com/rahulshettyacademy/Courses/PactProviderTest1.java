package com.rahulshettyacademy.Courses;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.StateChangeAction;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerAuth;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.rahulshettyacademy.controller.AllCourseData;
import com.rahulshettyacademy.repository.CoursesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.Map;
import java.util.Optional;

//We are stating Provider in its own environment instead of pact environment.
//Whereas we started Consumer in pact environment.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

//We have defined provider name in ConsumerTest class, we have to provide the same name here.
@Provider("CoursesCatalogue")

//We have to provide location where our pact contract json file is present
//@PactFolder("pacts")
@PactBroker(url = "https://alltimesoftware.pactflow.io/",
        authentication = @PactBrokerAuth(token="5ET2bcD2GiXxNb0KdLp7kg"))



public class PactProviderTest1 {
//@LocalServerPort annotation will read where our server is started
// and assign that port number to port variable
//context variable need this port information so that it knows where our service is.
//We have created setup method where we setTarget by passing url and port to my context.
    @LocalServerPort
    public int port;

//We have use @TestTemplate with our method so that it will become only template
// instead of actual test which can execute multiple times.
//We have called verifyInteraction method which interact with our contract file.
//For consumer side we have to extendWith PactConsumerTestExt at class level
//For provider side we have to extendWith PactVerificationInvocationContextProvider at method level
    @Autowired
    CoursesRepository coursesRepository;

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    public void pactVerificationTest(PactVerificationContext context){
        context.verifyInteraction();
    }


    @BeforeEach
    public void setup(PactVerificationContext context){
        context.setTarget(new HttpTestTarget("localhost", port));
    }

    //For which interaction this setup to execute we have to define
    //So we have to pass the value, this value we get from Consumer given() method of builder.
    //Value should be same to understand the interaction.
    //If we don't provide value then this method will execute for all interactions exist in consumer class
    //Implementation of these setup and teardown method is not needed.
    //But if we don't define them then it will through compile time error.
    @State(action = StateChangeAction.SETUP, value = "courses exist")
    public void coursesExistSetup(){

    }

    @State(action = StateChangeAction.TEARDOWN, value = "courses exist")
    public void coursesExistTearDown(){

    }

    @State(action = StateChangeAction.SETUP, value = "appium course exist")
    public void appiumCourseExistSetup(){

    }

    @State(action = StateChangeAction.TEARDOWN, value = "appium course exist")
    public void appiumCourseExistTearDown(){

    }


    @State(action = StateChangeAction.SETUP, value = "appium course not exist")
    public void appiumCourseNotExistSetup(Map<String, Object> params){
        String name = (String)params.get("name");
        //TO delete the appium record in database
        Optional<AllCourseData> record = coursesRepository.findById(name);
        if(record.isPresent()){
            coursesRepository.deleteById("Appium");
        }
    }

    @State(action = StateChangeAction.TEARDOWN, value = "appium course not exist")
    public void appiumCourseNotExistTearDown(Map<String, Object> params){
        String name = (String)params.get("name");
        //Add the appium record again in database
        Optional<AllCourseData> record = coursesRepository.findById(name);
        if(!record.isPresent()){
            AllCourseData allCourseData = new AllCourseData();
            allCourseData.setCourse_name("Appium");
            allCourseData.setCategory("mobile");
            allCourseData.setId("12");
            allCourseData.setPrice(13);
            coursesRepository.save(allCourseData);
        }
    }

}
