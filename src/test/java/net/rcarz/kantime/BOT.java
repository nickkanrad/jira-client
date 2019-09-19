package net.rcarz.kantime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class BOT {

  public void sendPost(String assignee, String message) throws IOException {
    String url = "";

    switch (assignee) {
      case "alewis":
        url =
            "https://chat.googleapis.com/v1/spaces/h-23zAAAAAE/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=7TqRRaPq_54oiOB0n1f0zStwqeoGbFcDEjpd47UGWtU%3D";
        break;
      case "dwilliams":
        url =
            "https://chat.googleapis.com/v1/spaces/-6u3zAAAAAE/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=kFtCFXHPqycU9YDd7rtqmwnN9SAa3BOvxVqbU_iYGX8%3D";
        break;
    }

    final HttpClient client = new DefaultHttpClient();
    final HttpPost request = new HttpPost(url);
    // FROM HERE

    request.addHeader("Content-Type", "application/json; charset=UTF-8");

    final StringEntity params = new StringEntity("{\"text\":\" " + message + "\"}",
        ContentType.APPLICATION_FORM_URLENCODED);
    request.setEntity(params);

    // TO HERE
    final HttpResponse response = client.execute(request);

    final BufferedReader rd =
        new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

    StringBuffer result = new StringBuffer();
    String line;
    while ((line = rd.readLine()) != null) {
      result.append(line);
    }

    System.out.println(result.toString());
  }

}
