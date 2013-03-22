import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Marika's Computer
 */
public class Main {

    private Main() {
    }

    public static void main(String args[]) {
        Main ts = new Main();
        try {
            ts.doInBackground();
        } catch (Exception ex) {
            Logger.getLogger(Testclass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //Наші статичні дані (Ід додатку, доступ, та й усе)
    static String APP_ID = "2783462";
    static String settings = "4098";
    static String redirect_uri = "http://api.vk.com/blank.html";
    static String login = "";
    static String pass = "";
    //А ще робимо статичний ХТТП клієнт щоб кукі зберігались
    //І да, це важливо
    static DefaultHttpClient client = new DefaultHttpClient();
    static String[] answer = null;

    protected String[] doInBackground() throws Exception {
        answer = getAccessToken(login, pass);
        return answer;
    }

    public String[] getAccessToken(String _login, String _pass) throws URISyntaxException, IOException {
        String body = "l";
        HttpPost post = null;
        HttpResponse response = null;
        HttpParams params = new BasicHttpParams();
        HttpClientParams.setRedirecting(params, false);
        client = new DefaultHttpClient(params);

        //Формуєм параметри запиту з ід-шкою додатку, настройками доступу,
        //редірект урлом, методом отримання відповіді і що варіант відповіді(код чи токен)
        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("client_id", APP_ID));
        qparams.add(new BasicNameValuePair("scope", settings));
        qparams.add(new BasicNameValuePair("redirect_uri", redirect_uri));
        qparams.add(new BasicNameValuePair("display", "wap"));
        qparams.add(new BasicNameValuePair("response_type", "token"));

        //Формуєм сам запит і додаєм параметри ^
        URI uri = null;
        try {
            uri = URIUtils.createURI("http", "oauth.vk.com", -1, "/oauth/authorize",
                    URLEncodedUtils.format(qparams, "UTF-8"), null);
        } catch (URISyntaxException e) {
            return null;
        }
        System.out.println(uri);
        post = new HttpPost(uri);
        try {
            response = client.execute(post);
        } catch (IOException e) {
            return null;  //To change body of catch statement use File | Settings | File Templates.
        }
        post.abort();

        //З відповіді сервера дістаєм заголовок з шляхом
        //        
        body = EntityUtils.toString(response.getEntity());
        System.out.println(body);
        
        //Парсим сторінку і дістаєм ip_h i to
        String form_action = "<input type=\"hidden\" name=\"ip_h\" value=\"";
        int uri_start_index = body.indexOf(form_action) + form_action.length();
        int uri_end_index = uri_start_index + body.substring(uri_start_index).indexOf('"');
        String ip_h = body.substring(uri_start_index, uri_end_index);
        
        form_action = "<input type=\"hidden\" name=\"to\" value=\"";
        uri_start_index = body.indexOf(form_action) + form_action.length();
        uri_end_index = uri_start_index + body.substring(uri_start_index).indexOf('"');
        String to_h = body.substring(uri_start_index, uri_end_index);
        
        System.out.println(ip_h);
        System.out.println(to_h);

        //Задаєм параметри для авторизації( деякі параметри константи )
        //ми додаємо тільки іпх, тох, логін, і пасс
        List<NameValuePair> postform = new ArrayList<NameValuePair>();
        postform.add(new BasicNameValuePair("q", "1"));
        postform.add(new BasicNameValuePair("ip_h", ip_h));
        postform.add(new BasicNameValuePair("from_host", "api.vk.com"));
        postform.add(new BasicNameValuePair("to", to_h));
        postform.add(new BasicNameValuePair("expire", "0"));
        postform.add(new BasicNameValuePair("email", login));
        postform.add(new BasicNameValuePair("pass", pass));

        //Формуєм основний запит авторизації( тут пoходу треба захищене
        //з'єднання через HTTPS
        URI uri2 = null;
        try {
            uri2 = URIUtils.createURI("https", "login.vk.com", -1, "/?act=login&soft=1&utf8=1",
                    URLEncodedUtils.format(postform, "UTF-8"), null);
        } catch (URISyntaxException e) {
            return null;  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println(uri2);
        post = new HttpPost(uri2);
        try {
            response = client.execute(post);
        } catch (Exception e) {
            return null;  //To change body of catch statement use File | Settings | File Templates.
        }
        post.abort();

       


        //Дістаємо код сторінки на якій маємо дати дозвіл додатку
        //String body = null;
        try {
            body = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            return null;
        }
        
        //Перевіряємо доступ і парсим акшо його нема
        if (body.contains("<form method=\"POST\" action=\"")) {

            //Парсим сторінку і дістаєм силку підтвердження доступу
            form_action = "<form method=\"POST\" action=\"";
            uri_start_index = body.indexOf(form_action) + form_action.length();
            uri_end_index = uri_start_index + body.substring(uri_start_index).indexOf('>') - 1;
            String grant_access_uri = body.substring(uri_start_index, uri_end_index);
            System.out.println(grant_access_uri);   
            //Дістали код дозволу, відправляєм
            post = new HttpPost(grant_access_uri);
            try {
                response = client.execute(post);
            } catch (IOException e) {
                return null;
            }
            post.abort();
            body = EntityUtils.toString(response.getEntity());
            System.out.println(body);



        } else {
            //Якщо ми вже дозволили доступ до додатку то юзаєм це
            String access_granted = response.getFirstHeader("location").getValue();//.split("?")[1].split("&")[0].split("=")[1];
            System.out.println("zz"+ access_granted);
            post = new HttpPost(access_granted);
            try {
                response = client.execute(post);
            } catch (IOException e) {
                return null;
            }
            post.abort();
            
            
            post = new HttpPost(response.getFirstHeader("location").getValue());
            try {
                response = client.execute(post);
            } catch (IOException e) {
                return null;
            }
            post.abort();
            
            
        }

        //І ось він, ось він код моєї мрії
        //Це буде або Токен або Код, в залежності від першого запиту
        String[] access_token = new String[2];
        access_token[0] = response.getFirstHeader("location").getValue().split("#")[1].split("&")[0].split("=")[1];
        System.out.println("token"+access_token[0]);
        System.out.println("token" + access_token[1]);
        access_token[1] = response.getFirstHeader("location").getValue().split("user_id=")[1];//.split("&")[0].split("=")[2]; //length-1//
        return null;
    }
}
