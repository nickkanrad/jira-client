package net.rcarz.kantime;

import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import java.util.List;
import org.testng.annotations.Test;

public class closeSubTasksAndLiveCases {

  @SuppressWarnings({})
  @Test()
  public void closeSubTasksAndLiveCase() throws JiraException {

    BasicCredentials creds = new BasicCredentials("nick@kanrad.com", "WORF3aXXRSjXmejgZL7A9EF2");
    JiraClient jira = new JiraClient("https://kanrad.atlassian.net", creds);

    DBconnections db = new DBconnections();

    /*
     ******** CLOSE THE SUBTASKS ***********
     */
    try {
      /* Search for sub tasks and close */
      Issue.SearchResult sr = jira.searchIssues(
          "\"HH Team\" in (\"Dev - Phoenix\", \"Dev - ICE\", \"Dev - Mobile\", Nitish, Rasheed, Shaji, \"Shruthi (SSRS)\", Syed, Praful, Nithya) AND Sprint in openSprints() AND issuetype = Sub-task AND status = Completed");
      System.out.println("Total sub task to close: " + sr.total);
      for (Issue subissue : sr.issues) {
        System.out.println("Closed sub task : " + subissue);
        /* Now let's start progress on this issue and close */
        subissue.transition().execute("Closed");
      }
    } catch (JiraException ex) {
      System.err.println(ex.getMessage());
      if (ex.getCause() != null)
        System.err.println(ex.getCause().getMessage());
    }

    /*
     ******** CLOSE THE LIVE CASE TASK IF THE SUBTASKS ARE CLOSED ***********
     */
    try {
      /* Search for live case issues where the parent task is completed */
      Issue.SearchResult liveCaseParentTask = jira.searchIssues(
          "\"HH Team\" in (\"Dev - Phoenix\", \"Dev - ICE\", \"Dev - Mobile\", Nitish, Rasheed, Shaji, \"Shruthi (SSRS)\", Syed, Praful, Nithya) AND Sprint in openSprints() AND issuetype = Task  and labels = LiveCase and status in (Completed)");
      System.out.println("Total: " + liveCaseParentTask.total);
      for (Issue issue : liveCaseParentTask.issues) {
        int closedSubtask = 0;
        System.out.println("Parent Live case issue: " + issue);

        // get the subtasks
        List<Issue> subtasks = issue.getSubtasks();
        System.out.println("Total sub task for issue: " + issue + " is " + subtasks.size());
        for (Issue subtaskIssue : subtasks) {
          System.out.println("subtask status: " + subtaskIssue.getStatus());
          // get all the subtask status
          if (subtaskIssue.getStatus().toString().equals("Closed")) {
            closedSubtask++;
          }
        }
        // if the count of closed subtasks is equal to the total sub task count, then close the
        // parent
        if (subtasks.size() == closedSubtask) {
          issue.transition().execute("Closed");
          System.out.println("Closed parent: " + issue);
        } else {
          System.out.println("Not Closed parent: " + issue + " since there are unclosed sub tasks");
        }
        System.out.println("*****************************************");
      }
    } catch (JiraException ex) {
      System.err.println(ex.getMessage());
      if (ex.getCause() != null)
        System.err.println(ex.getCause().getMessage());
    }

    /*
     ******** CLOSE THE LIVE CASE TASK AND SUBTASK IF CLOSED IN KPC ***********
     */
    try {
      /* Search for live case issues where the parent task is completed */
      Issue.SearchResult liveCaseParentTask = jira.searchIssues(
          "\"HH Team\" in (\"Dev - Phoenix\", \"Dev - ICE\", \"Dev - Mobile\", Nitish, Rasheed,Shaji, \"Shruthi (SSRS)\", Syed, Praful, Nithya) AND labels=LiveCase and issuetype =Task and status not in (Closed)");
      // "Key=HH-18563");
      System.out.println("Total Parent Live Case Task: " + liveCaseParentTask.total);

      for (Issue issue : liveCaseParentTask.issues) {

        System.out.println("Parent Live case issue: " + issue);

        try {
          String ticket = (String) issue.getField("customfield_10046");
          System.out.println("Ticket#: " + ticket);

          String[][] issuesFromKpc = (String[][]) db.readDataFromDatabaseUsingQuery(ticket);

          for (String[] kpcIssue : issuesFromKpc) {
            // get the subtasks
            List<Issue> subtasks = issue.getSubtasks();
            System.out.println("Total sub task for issue: " + issue + " is " + subtasks.size());

            for (Issue subtaskIssue : subtasks) {
              System.out.println("subtask status: " + subtaskIssue.getStatus());
              subtaskIssue.transition().execute("Closed");
              System.out.println("closed subtask: " + subtaskIssue);
            } // end of for loop subtasks

            issue.transition().execute("Closed");
            System.out.println("closed task: " + issue);
          } // end of for loop issuesFromKpc

          System.out.println("************************************");
        } catch (Exception e) {
          System.out.println(" ######### Ticket# not available in case #############");
          System.out.println("************************************");
        }

      } // end of for loop liveCaseParentTask
    } catch (JiraException ex) {
      System.err.println(ex.getMessage());
      if (ex.getCause() != null)
        System.err.println(ex.getCause().getMessage());
    }
  }
}
