package edu.unh.cs.cs619.bulletzone.web;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ClientStub {
    public String address;
    public long tankID;
    public int location;
    public byte direction;
    public int vehicleType;

    // tank/shared
    public int allowedMoveInterval;
    public int allowedFireInterval;
    public int allowedNumberOfBullets;
    public int allowedTurnInterval;
    public int life;

    // soldier specific
    public int allowedReEnterTime;
    public int bulletDamage;

    // ship specific
    public int allowedMainFireInterval;
    public int allowedSideFireInterval;

    private MockMvc mockMvc;

    /* To match the server, keeping a global
     *  current next ID that increments with
     *  each successful join*/
    private static int nextAddressBase = 0;

    /* The only weird one as it takes a postProcessRequest
     *  to pass the server a fake ip address*/
    public int joinGame() throws Exception {
        try{
            JSONObject response = new JSONObject(
                    mockMvc.perform(post("/games/" + vehicleType)
                    .with(new RequestPostProcessor() {
                        @Override
                        public MockHttpServletRequest
                        postProcessRequest(MockHttpServletRequest request) {
                            request.setRemoteAddr(address);
                            return request;
                        }
                    })).andReturn().getResponse().getContentAsString());

            tankID = Long.valueOf(response.getString("result"));
        }catch(Exception e){
            return 405;
        }
        return 201;
    }


    public int leaveGame() throws Exception {
        MockHttpServletResponse response =
                mockMvc.perform(delete("/games/" + tankID + "/leave"))
                        .andReturn().getResponse();

        return response.getStatus();
    }

    public int ejectSoldier() throws Exception{
        MockHttpServletResponse response =
                mockMvc.perform(put("/games/" + tankID + "/ejectSoldier/1"))
                        .andReturn().getResponse();

        return response.getStatus();
    }

    public long whoAmI() throws Exception {
        JSONObject response;
        try {
            response = new JSONObject(mockMvc.perform(get("/games/whoAmI")
                    .with(new RequestPostProcessor() {
                        @Override
                        public MockHttpServletRequest
                        postProcessRequest(MockHttpServletRequest request) {
                            request.setRemoteAddr(address);
                            return request;
                        }
                    })).andExpect(status().is2xxSuccessful())
                    .andReturn()
                    .getResponse()
                    .getContentAsString());
        }catch(Exception | AssertionError e){
            return -1;
        }

        return Long.valueOf(response.getString("result"));
    }

    public int whereAmI() throws Exception {
        JSONObject response;
        try {
            response = new JSONObject(mockMvc.perform(get("/games/whereAmI")
                    .with(new RequestPostProcessor() {
                        @Override
                        public MockHttpServletRequest
                        postProcessRequest(MockHttpServletRequest request) {
                            request.setRemoteAddr(address);
                            return request;
                        }
                    })).andExpect(status().is2xxSuccessful())
                    .andReturn()
                    .getResponse()
                    .getContentAsString());
        }catch(Exception | AssertionError e){
            return -1;
        }

        return Integer.valueOf(response.getString("result"));
    }

    public byte whichWay() throws Exception {
        JSONObject response;
        try {
            response = new JSONObject(mockMvc.perform(get("/games/whichWay")
                    .with(new RequestPostProcessor() {
                        @Override
                        public MockHttpServletRequest
                        postProcessRequest(MockHttpServletRequest request) {
                            request.setRemoteAddr(address);
                            return request;
                        }
                    })).andExpect(status().is2xxSuccessful())
                    .andReturn()
                    .getResponse()
                    .getContentAsString());
        }catch(Exception | AssertionError e){
            return -1;
        }

        return Byte.valueOf(response.getString("result"));
    }

    public int howMuchAmmo() throws Exception {
        JSONObject response;
        try {
            response = new JSONObject(mockMvc.perform(get("/games/howManyBullets")
                    .with(new RequestPostProcessor() {
                        @Override
                        public MockHttpServletRequest
                        postProcessRequest(MockHttpServletRequest request) {
                            request.setRemoteAddr(address);
                            return request;
                        }
                    })).andExpect(status().is2xxSuccessful())
                    .andReturn()
                    .getResponse()
                    .getContentAsString());
        }catch(Exception | AssertionError e){
            return -1;
        }

        return Integer.valueOf(response.getString("result"));
    }

    public int turn(byte direction) throws Exception {
        MockHttpServletResponse response =
                mockMvc.perform(put(
                        "/games/" + tankID + "/turn/" + String.valueOf(direction)))
                        .andReturn().getResponse();

        return response.getStatus();
    }

    public boolean move(int direction) throws Exception {
        JSONObject response;
        try {
            response = new JSONObject(mockMvc.perform(
                    put("/games/" + tankID + "/move/" + String.valueOf(direction)))
                    .andExpect(status().is2xxSuccessful())
                    .andReturn()
                    .getResponse()
                    .getContentAsString());
    }catch(Exception | AssertionError e){
            return false;
        }
        return Boolean.valueOf(response.getString("result"));
    }

    public boolean fire(int direction) throws Exception {
        JSONObject response;
        try {
            response = new JSONObject(mockMvc.perform(
                    put("/games/" + tankID + "/fire/" + String.valueOf(direction)))
                    .andExpect(status().is2xxSuccessful())
                    .andReturn()
                    .getResponse()
                    .getContentAsString());
        }catch(Exception | AssertionError e){
            return false;
        }
        return Boolean.valueOf(response.getString("result"));
    }

    ClientStub(MockMvc curMockMvc,int type){
        mockMvc = curMockMvc;
        address = "192.168.0." + nextAddressBase++;

        vehicleType = type;

        switch(type){
            case 1:
                allowedMoveInterval = 500;
                allowedFireInterval = 500;
                allowedNumberOfBullets = 2;
                life = 100;
                allowedReEnterTime = 3000;
                break;
            case 2:
                allowedMoveInterval = 1000;
                allowedFireInterval = 250;
                allowedNumberOfBullets = 6;
                allowedTurnInterval = 0;
                life = 25;
                bulletDamage = 5;
                break;
            case 3:
                allowedMoveInterval = 750;
                allowedTurnInterval = 250;
                allowedMainFireInterval = 300;
                allowedSideFireInterval = 250;
                allowedNumberOfBullets = 8;
                life = 100;
                allowedReEnterTime = 3000;
        }
    }
}
