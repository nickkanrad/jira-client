package net.rcarz.kantime;

import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.Test;

public class closeSubTasksAndLiveCases {

  @SuppressWarnings({})
  @Test()
  public void closeSubTasksAndLiveCase() throws JiraException {

    BasicCredentials creds = new BasicCredentials("nick@kanrad.com", "WORF3aXXRSjXmejgZL7A9EF2");
    JiraClient jira = new JiraClient("https://kanrad.atlassian.net", creds);
    BOT autobot = new BOT();
    DBconnections db = new DBconnections();

    /*
     * GET THE LIST OF UI COMPLETED TASKS
     */

    List<String> uiCompletedForms = new ArrayList<String>();
    /* Search for issues */
    Issue.SearchResult srr = jira.searchIssues("filter=10415");
    System.out.println("Total UI completed takss: " + srr.total);
    for (int start = 0; start < srr.total; start++) {
      for (Issue i : srr.issues) {
        uiCompletedForms.add(i.getKey());
      }
    }

    /*
     ******** CLOSE THE SUBTASKS ***********
     */
    try {
      /* Search for sub tasks and close */
      Issue.SearchResult sr = jira.searchIssues("filter=10377");
      System.out.println("Total sub task to close: " + sr.total);
      for (Issue subissue : sr.issues) {
        System.out.println("Closed sub task : " + subissue);
        /* Now let's start progress on this issue and close */
        subissue.transition().execute("Closed");

        /* if this closed one in ui completed list, send notification */
        for (String uiCompleted : uiCompletedForms) {
          if (uiCompleted.toString().equals(subissue.getKey())) {
            try {
              autobot.sendPost(
                  "https://chat.googleapis.com/v1/spaces/AAAABOpM1tU/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=sQ_qcFnpdBfaRlIEexzmkFeRVk8hhPdYeLf9T4vsiVs%3D",
                  "UI completed: " + subissue.getParent().toString());
            } catch (IOException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            System.out.println("***********************");
            System.out.println("UI completed task: " + uiCompleted.toString());
            System.out.println("***********************");
          }
        }
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
      Issue.SearchResult liveCaseParentTask = jira.searchIssues("filter=10416");
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
      Issue.SearchResult liveCaseParentTask = jira.searchIssues("filter=10417");
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
