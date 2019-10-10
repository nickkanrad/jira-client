package net.rcarz.kantime;

import org.testng.annotations.Test;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.CustomFieldOption;
import net.rcarz.jiraclient.Field;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;

public class ProductionDelivery {

  @Test
  public void f() throws JiraException, ParseException {
    BasicCredentials creds = new BasicCredentials("nick@kanrad.com", "WORF3aXXRSjXmejgZL7A9EF2");
    JiraClient jira = new JiraClient("https://kanrad.atlassian.net", creds);
    BOT autobot = new BOT();
    String HHTeam = "";
    String ChatRoom = "";

    Date date = new Date();
    String dateStart = new SimpleDateFormat("yyyy-MM-dd").format(date);
    System.out.println(dateStart);

    Issue.SearchResult sr = jira.searchIssues("filter=10426");
    for (Issue i : sr.issues) {

      /* Pretend customfield_1234 is a text field. Get the raw field value... */
      Object cfvalue = i.getField(Field.CUSTOMFIELD_PRODUCTION_DELIVERY);

      /* ... Convert it to a string and then print the value. */
      String dateStop = Field.getString(cfvalue);

      List<CustomFieldOption> cfselect = Field.getResourceArray(CustomFieldOption.class,
          i.getField(Field.CUSTOMFIELD_HHTEAM), jira.getRestClient());
      for (CustomFieldOption cfo : cfselect) {
        HHTeam = cfo.getValue();
        System.out.println(HHTeam);
      }

      DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

      Date d1 = null;
      Date d2 = null;

      d1 = format.parse(dateStart);
      d2 = format.parse(dateStop);

      // in milliseconds
      long diff = d2.getTime() - d1.getTime();

      long diffDays = (diff / (24 * 60 * 60 * 1000)) - 1;

      if (diffDays > 3 || diffDays < 0)
        continue;
      System.out.println(i.getKey() + " : " + diffDays + " days, ");

      switch (HHTeam) {
        case "Nithya":
          ChatRoom =
              "https://chat.googleapis.com/v1/spaces/AAAAQeQ5cEQ/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=rnlc3qejFbERkat7KYq4uDRCskQOOkXiAyXWzMYSeiw%3D";
          break;
        case "Praful":
          break;
        case "Syed":
          ChatRoom =
              "https://chat.googleapis.com/v1/spaces/AAAAZnJIFgs/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=nUNJtbhCoTXsuPTF5G13bt3LvtAM4QVUmOlgB-GI0Xs%3D";
          break;
        case "Shruthi (SSRS)":
          ChatRoom =
              "https://chat.googleapis.com/v1/spaces/AAAAb0vTh7w/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=Uous34UOQvBVgH2z8S4zUdBTOcod8qqxf_7Rcn2bFQE%3D";
          break;
        case "Shaji":
          ChatRoom =
              "https://chat.googleapis.com/v1/spaces/AAAA7BoXAps/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=bWGPBflSePsEIa2D_zMMbJPJzooFKb4ppGmKJ0YCGSY%3D";
          break;
        case "Rasheed":
          ChatRoom =
              "https://chat.googleapis.com/v1/spaces/AAAA6iDuIgI/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=lKQi3eY_-dsvvuUg58tAwZxGFnNKvbubMsIg4w-uAZc%3D";
          break;
        case "Devender":
          break;
      }

      if (!ChatRoom.equals("")) {
        try {
          autobot.sendPost(ChatRoom,
              "Approaching Production Delivery: https://kanrad.atlassian.net/browse/" + i.getKey()
                  + " in " + diffDays + " days, ");
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
  }
}
