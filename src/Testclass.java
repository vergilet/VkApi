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
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Marika's Computer
 */
public class Testclass {

    private Testclass() {
    }

    public static void main(String args[]) {
        Testclass ts = new Testclass();
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
    static String answer = null;

    protected String doInBackground() throws Exception {
        answer = getAccessToken(login, pass);
        return answer;
    }

    public String getAccessToken(String _login, String _pass) throws URISyntaxException, IOException {
        String login = _login;
        String pass = _pass;
        HttpPost post = null;
        HttpResponse response = null;

        //Формуєм параметри запиту з ід-шкою додатку, настройками доступу,
        //редірект урлом, методом отримання відповіді і що варіант відповіді(код чи токен)
        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("client_id", APP_ID));
        qparams.add(new BasicNameValuePair("scope", settings));
        qparams.add(new BasicNameValuePair("redirect_uri", redirect_uri));
        qparams.add(new BasicNameValuePair("display", "wap"));
        qparams.add(new BasicNameValuePair("response_type", "token"));

        //Формуєм сам запит і додаєм параметри ^
        URI uri = URIUtils.createURI("http", "oauth.vk.com", -1, "/oauth/authorize",
                URLEncodedUtils.format(qparams, "UTF-8"), null);
        //System.out.println(uri);
        post = new HttpPost(uri);
        response = client.execute(post);
        post.abort();

        //З відповіді сервера дістаєм заголовок з шляхом
        String HeaderLocation = response.getFirstHeader("location").getValue();

        //З отриманого локейшена дістаємо два параметри "іп_х" і "то_х",
        //Вони потрібні для авторизації
        URI RedirectUri = new URI(HeaderLocation);
        String ip_h = RedirectUri.getQuery().split("&")[2].split("=")[1];
        String to_h = RedirectUri.getQuery().split("&")[4].split("=")[1];

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

        //Формуєм основний запит авторизації( тут паходу треба захищене
        //з'єднання через HTTPS
        URI uri2 = URIUtils.createURI("https", "login.vk.com", -1, "/?act=login&soft=1&utf8=1",
                URLEncodedUtils.format(postform, "UTF-8"), null);
        post = new HttpPost(uri2);
        response = client.execute(post);
        post.abort();

        //Знову дістаємо локейшн з хедера і знову відправляємо
        //по суті, це ми руцями проходимо по редіректах
        //Тут ми відправляєм запит на сторінку дозволу
        HeaderLocation = response.getFirstHeader("location").getValue();
        post = new HttpPost(HeaderLocation);
        response = client.execute(post);
        post.abort();




        //Дістаємо код сторінки на якій маємо дати дозвіл додатку
        String body = EntityUtils.toString(response.getEntity());
        //Перевіряємо доступ і парсим акшо його нема
        if (body.contains("<form method=\"POST\" action=\"")) {

            //Парсим сторінку і дістаєм силку підтвердження доступу
            String form_action = "<form method=\"POST\" action=\"";
            int uri_start_index = body.indexOf(form_action) + form_action.length();
            int uri_end_index = uri_start_index + body.substring(uri_start_index).indexOf('>') - 1;
            String grant_access_uri = body.substring(uri_start_index, uri_end_index);

            //Дістали код дозволу, відправляєм
            post = new HttpPost(grant_access_uri);
            response = client.execute(post);
            post.abort();



        } else {
            //Якщо ми вже дозволили доступ до додатку то юзаєм це
            String access_granted = response.getFirstHeader("location").getValue();//.split("?")[1].split("&")[0].split("=")[1];
            post = new HttpPost(access_granted);
            response = client.execute(post);
            post.abort();
        }


        //І ось він, ось він код моєї мрії
        //Це буде або Токен або Код, в залежності від першого запиту
        String access_token = response.getFirstHeader("location").getValue().split("#")[1].split("&")[0].split("=")[1];
        System.out.println(access_token);
        return access_token;
        //return null;
    }
}
