package edu.unh.cs.cs619.bulletzone.web;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GameStateStub {
    List<int []> states;
    public int [] initialState;
    private MockMvc mockMvc;

    GameStateStub(MockMvc refMockMvc){
        mockMvc = refMockMvc;
        states = new ArrayList<>();
    }

    public int [] getGrid() throws Exception {
        int [] grid = new int [256];

        String getGridResult = mockMvc.perform(get("/games"))
                .andExpect(status().isAccepted())
                .andReturn()
                .getResponse()
                .getContentAsString();

        DocumentContext jsonDoc = JsonPath.parse(getGridResult);

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                try {
                    String cell = jsonDoc.read("['grid']" + "[" + i + "][" + j + "]")
                            .toString();

                    grid[(i * 16) + j] = Integer.parseInt(cell);
                }catch(Exception e){
                    fail("Error in JsonArray");
                }
            }
        }

        return grid;
    }

    public int countGridDiff(int [] first, int [] second) {
        int diffCount = 0;

        for(int i=0;i<256;i++)
        {
            if(first[i] != second[i])
            {
                diffCount += 1;
            }
        }
        return diffCount;
    }
}