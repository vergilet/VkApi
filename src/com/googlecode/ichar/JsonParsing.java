/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.ichar;

import com.google.gson.Gson;
import java.util.List;

/**
 *
 * @author Marika's Computer
 */
public class JsonParsing {

    public List<Result> serJosn(String text) {
        String somejson=text;
        Gson gson = new Gson();
        CashGamesContainer cashResponse = gson.fromJson(somejson, CashGamesContainer.class);
        System.out.println(gson.toJson(cashResponse));

        List<Result> results = cashResponse.response;
        for (Result cashResult : results) {
            System.out.println(cashResult.first_name);
        }
        return results;
    }
}

class CashGamesContainer {

    List<Result> response;
}

class Result {

    long uid;
    String first_name;
    String last_name;
    String photo;
    char online;
}