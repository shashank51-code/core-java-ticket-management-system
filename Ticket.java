
import java.util.Scanner;

class Ticket {

    int ticketId;
    String title;
    String description;
    String priority;
    String status;
    String createdBy;
    Employee assignedTo;
    String []history=new String[100];

    Ticket(int ticketId, String title, String description, String priority, String createdBy) {
        this.ticketId = ticketId;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = "OPEN";
        this.createdBy = createdBy;
        addHistory(status, createdBy);

    }

    void displayTicket() {

        System.out.println("Ticket ID: " + ticketId);
        System.out.println("Title: " + title);
        System.out.println("Description: " + description);
        System.out.println("Priority: " + priority);
        System.out.println("Status: " + status);
        System.out.println("created by: " + createdBy);
        if (assignedTo == null) {
            System.out.println("Assigned To: Not Assigned");
        } else {
            System.out.println("assignedTo: " + assignedTo.getEmployeeName());
        }

    }

    boolean updateStatus(String status,String person) {

        boolean found = false;

        if (this.status.equals("OPEN") && status.equals("ASSIGNED")) {
            found = true;
        } else if (this.status.equals("ASSIGNED") && status.equals("IN_PROGRESS")) {
            found = true;
        } else if (this.status.equals("IN_PROGRESS") && status.equals("RESOLVED")) {
            found = true;
        } else if (this.status.equals("RESOLVED") && status.equals("CLOSED")) {

            found = true;
        }

        if (found) {

            this.status = status;
            System.out.println("Status updated to: " + this.status);
             addHistory(status,person);
        }
        return found;

    }

    boolean updatePriority(String priority) {
        boolean found = false;

        if (priority.equals("LOW")) {
            found = true;
        } else if (priority.equals("MEDIUM")) {
            found = true;
        } else if (priority.equals("HIGH")) {
            found = true;
        } else if (priority.equals("CRITICAL")) {
            found = true;
        }
        if (found == true) {

            this.priority = priority;

        }

        return found;
    }

    void displaySummary()
    {
        System.out.println("Ticket ID: "+ticketId+
                            "\n Title: "+title+
                            "\n Priority: "+priority+
                            "\n Status: "+status+
                            "\n Created By: "+createdBy);
                            if(assignedTo==null)
                            {
                                System.out.println("Assigned To: Not Assigned");
                            }
                            else{
                                System.out.println("Assigned To: " + assignedTo.getEmployeeName());
                            }
          
    }

    void addHistory(String status,String person)
    {
        String historyView;
        for(int i=0;i<history.length;i++)
        {
            if(history[i]==null)
            {
                historyView=status+" - "+person;
                history[i]=historyView;
                break;
            }
        }
    }
    void displayHistory()
    {
        boolean found=false;
        System.out.println("===== TICKET HISTORY =====");
        for(String h:history) {
            if(h != null) {
                System.out.println(h);
                found=true;
            }
        }
        if(!found)
        {
            System.out.println("No history found.");
        }
    }
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

    }
}
