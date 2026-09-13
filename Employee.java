
import java.util.InputMismatchException;
import java.util.Scanner;


class Employee {

    private int employeeId;
    private String employeeName;
    private String email;
    private String phone;
    private String department;
    private String role;
    Ticket[] tickets = new Ticket[5];
    static int counter;
    
    
    static Scanner sc = new Scanner(System.in);


    public Employee(String employeeName, String email,String phone, String department, String role) {
        this.employeeId = counter++;
        this.employeeName = employeeName;
        this.email = email;
        this.phone=phone;
        this.department = department;
        this.role = role;
    }


    void displayEmployee() {
        System.out.println("===== Employee Details =====");
        System.out.println("Employee ID: " + employeeId);
        System.out.println("Name: " + employeeName);
        System.out.println("Email: " + email);
        System.out.println("phone: "+phone);
        System.out.println("Department: " + department);
        System.out.println("Role: " + role);

    }

    void raiseTicket() {
        boolean added=false;
        int ticketId =0;
        while(true)
        {
            try
            {
                System.out.println("Enter Ticket ID:");
                ticketId = sc.nextInt();
                break;
            }
            catch(InputMismatchException e)
            {
                System.out.println("please enter number only");
                sc.nextLine();
            }
        }
        
        System.out.println("Ticket Title");
        String ticketTitle = sc.next();
        System.out.println("Description");
        String ticketDescription = sc.next();
        System.out.println("Priority ");
        String ticketPriority = sc.next();
        System.out.println("createdBy: " + employeeName);
        Ticket newTicket = new Ticket(ticketId, ticketTitle, ticketDescription, ticketPriority, employeeName);
        for(int i=0;i<5;i++)
        {
            if(tickets[i]==null)
            {
                tickets[i]=newTicket;
                //Employee.history.add=newTicket;
                added=true;
                break;
                
            }
           
        }
        if(added)
        {
            newTicket.displayTicket();

        }
        else{
            System.out.println("Maximum ticket limit reached.");
        }
         
    }

    void viewAssignedTicket(Ticket ticket) {
        if (ticket.assignedTo == this) {
            ticket.displayTicket();

        } else {
            System.out.println("No tickets assigned");
        }
    }

    void workOnTicket(Ticket ticket) {
        while(true)
        {

        
        try
           {
            if (ticket.assignedTo==this)
            {
                System.out.println("Ticket ID: " + ticket.ticketId);
                System.out.println("Current Status: " + ticket.status);
                
            

                
                if(ticket.status.equals("ASSIGNED"))
                {
                    System.out.println("1.Start Work");
                    int inputToStart=sc.nextInt();
                    if(inputToStart==1)
                    {
                        ticket.updateStatus("IN_PROGRESS",this.employeeName);
                        break;
                    }
                    
                }
                else if(ticket.status.equals("IN_PROGRESS"))
                {
                    System.out.println("1.Resolve Ticket");
                    int inputToProgess=sc.nextInt();
                    if(inputToProgess==1)
                    {
                        ticket.updateStatus("RESOLVED",this.employeeName);
                        break;
                    }
                    
                }
                else if(ticket.status.equals("RESOLVED"))
                {
                    System.out.println("1.Close Ticket");
                    int inputresolved=sc.nextInt();
                    if(inputresolved==1)
                    {
                        ticket.updateStatus("CLOSED",this.employeeName);
                        break;
                    }
                    
                }
                else if(ticket.status.equals("CLOSED"))
                {
                    
                    System.out.println("Ticket is already closed.");
                    break;
                }
                else{
                    System.out.println("invalid input");
                }
                
            }
            else 
            {
                System.out.println("You are not assigned to this ticket.");
                break;
            }
            }
            catch(InputMismatchException e)
            {
                System.out.println("please enter number only");
                sc.nextLine();
            }
        }
    }
    void viewMyTickets()
    {
        boolean found=false;
        for(int i=0;i<5;i++)
        {
            if(tickets[i] != null)
            {
                found=true;
                tickets[i].displayTicket();
            }
        }
        if(!found)
        {
            System.out.println("No tickets found.");
        }
    }
  
    void viewTicketsByPriority(String priority)
    {
        boolean found=false;
        
        for(int i=0;i<5;i++)
        {
            if(tickets[i] !=null)
            {
                if(tickets[i].priority.equalsIgnoreCase(priority))
                {
                        tickets[i].displayTicket();
                        found=true;
                }
            }
        }
        if(!found)
        {
            System.out.println("No "+priority+" priority tickets found.");
        }
    }
    void viewAssignedTickets(Employee employee)
    {
        boolean found=false;
        for(int i=0;i<5;i++)
        {
            if(employee.tickets[i] != null)
            {
                if(employee.tickets[i].assignedTo == this)
                {
                    employee.tickets[i].displayTicket();
                    found = true;
                }
            }
        }
        if(!found)
        {
            System.out.println("No tickets assigned to you.");
        }
    }
    void supportWorkLoad(Manager manager)
    {
        int count=0;
        
        for(Employee emp :manager.employee)
        {
            if(emp !=null)
            {
                for(Ticket ticket:emp.tickets)
                {
                    if(ticket != null)
                    {
                        if(ticket.assignedTo==this)
                        {
                            if(!ticket.status.equals("CLOSED"))
                            {
                                count++;
                            }
                        }
                    }
                }
            }
            
        }
        System.out.println("My Active Tickets: "+count);
    }
   void showSupportMenu(Manager manager)
   {
        while(true)
        {

            
            System.out.println("===== SUPPORT EMPLOYEE MENU =====");
            try
            {

                
                System.out.println("1. View Assigned Tickets");
                System.out.println("2. Work On Ticket \n 3.View My Workload");
                System.out.println("4. Exit");
                System.out.println("Enter choice:");
                int input=sc.nextInt();
                if(input==1)
                {
                // viewAssignedTickets(employee);
                    manager.assignedTicket(this);
                }
                else if(input==2)
                {
                    System.out.println("Enter Ticket ID: ");
                    int ticketId=sc.nextInt();
                    Ticket ticket = manager.findTicket(ticketId);
                    if(ticket == null)
                    {
                        System.out.println("Ticket not found.");
                    }
                    else{
                        workOnTicket(ticket);

                    }
                }
                else if(input==3)
                {
                    supportWorkLoad(manager);
                }
                else if(input==4)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid Input.");
                }
            }
            catch(InputMismatchException e)
            {
                System.out.println("please enter only numbers");
                sc.nextLine();
            }

        
        }
   }
   void showEmployeeMenu()
   {
    System.out.println("===== EMPLOYEE MENU =====");
    while(true)
    {
        try
        {

        
            System.out.println("1. Raise Ticket\n2. View My Tickets \n 3. View Tickets By Priority \n 4. Exit");   
            System.out.println("Enter choice: ");
            int n=sc.nextInt();
            if(n==1)
            {
                raiseTicket();
            }
            else if(n==2)
            {
                viewMyTickets();
            }
            else if(n==3)
            {
                System.out.println("enter priority: ");
                String priority=sc.next();
                viewTicketsByPriority(priority);
            }
            else if(n==4)
            {
                break;
            }
            else
            {
                System.out.println("Invalid input");
            }
        }
        catch(InputMismatchException e)
        {
            System.out.println("please enter only numbers.");
            sc.nextLine();
        }
    }
   }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getRole() {
        return role;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getDepartment() {
        return department;
    }


}
