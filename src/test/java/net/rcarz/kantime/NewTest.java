package net.rcarz.kantime;

import org.testng.annotations.Test;

import com.google.api.client.util.Lists;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.CustomFieldOption;
import net.rcarz.jiraclient.Field;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import net.rcarz.jiraclient.Version;

public class NewTest {

  @Test
  public void f() throws JiraException, ParseException {
    BasicCredentials creds = new BasicCredentials("nick@kanrad.com", "WORF3aXXRSjXmejgZL7A9EF2");
    JiraClient jira = new JiraClient("https://kanrad.atlassian.net", creds);

    Issue issue = jira.getIssue("HH-23443");

    /* Pretend customfield_1234 is a text field. Get the raw field value... */
    Object cfvalue = issue.getField(Field.CUSTOMFIELD_PRODUCTION_DELIVERY);

    /* ... Convert it to a string and then print the value. */
    String cfstring = Field.getString(cfvalue);
    System.out.println(cfstring);

    issue.update().field(Field.CUSTOMFIELD_PRODUCTION_DELIVERY, null).execute();

    // String dateStart = "2012/01/14";
    // String dateStop = "2012/01/15";
    //
    // // HH converts hour in 24 hours format (0-23), day calculation
    // SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
    //
    // Date d1 = null;
    // Date d2 = null;
    //
    // d1 = format.parse(dateStart);
    // d2 = format.parse(dateStop);
    //
    // // in milliseconds
    // long diff = d2.getTime() - d1.getTime();
    //
    // long diffDays = diff / (24 * 60 * 60 * 1000);
    //
    // System.out.print(diffDays + " days, ");

  }
}
