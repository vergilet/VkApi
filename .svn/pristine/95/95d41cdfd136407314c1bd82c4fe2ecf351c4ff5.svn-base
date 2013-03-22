/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.ichar;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Marika's Computer
 */
public class ReqMethods {

    HttpClient client = new DefaultHttpClient();
    HttpGet get;
    HttpResponse response;
    JsonParsing jp;
    List<Result> friendsList;
    //messages.send

    public String messagesSend(String token, String uid, String message) throws ClientProtocolException, IOException, URISyntaxException {
        String mess = message;
        String command = "https://api.vkontakte.ru/method/messages.send?uid=" + uid + "&message=" + mess + "&title=Відправлено з мобільного клієнта&access_token=" + token;
        URL url = new URL(command);
        URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
        get = new HttpGet(uri);
        response = client.execute(get);
        String all = EntityUtils.toString(response.getEntity());
        return all;
    }

    public String messagesGetHistory(String token, String uid) throws ClientProtocolException, IOException {
        String command = "https://api.vkontakte.ru/method/messages.getHistory?uid=" + uid + "&access_token=" + token;
        get = new HttpGet(command);
        response = client.execute(get);
        String all = EntityUtils.toString(response.getEntity());
        System.out.println(all);
        return all;
    }

    public String friendsGet(String token) throws ClientProtocolException, IOException {
        String command = "https://api.vkontakte.ru/method/friends.get?fields=" + "uid,first_name,last_name,photo" + "&access_token=" + token;
        get = new HttpGet(command);
        response = client.execute(get);
        String all = EntityUtils.toString(response.getEntity());
        System.out.println(command);
        JsonParsing js = new JsonParsing();
        friendsList =   js.serJosn(all);
        return all;
    }
}
