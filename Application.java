
import java.util.*;

class Application
{
    static Scanner sc=new Scanner(System.in);
    User[] users=new User[20];
    Employee[] employees=new Employee[20];
    Manager[] managers=new Manager[5];
    Employee[] supportTeam=new Employee[20];

  public static void main(String[] args) {
      Application x=new Application();
      User user = new User(0, "", "");
      System.out.println("============================== \n   TICKET MANAGEMENT SYSTEM \n==============================");
      while(true)
      {
        System.out.println("1. Login \n2. Signup \n3. Exit \nEnter your choice:");
        try
        {

        
            int input=sc.nextInt();
            if(input==1)
            {
                user.login(x);
            }
            else if(input==2)
            {
                user.signup(x);
            }
            else if(input==3)
            {
                System.out.println("Thank you for using Ticket Management System!");
                break;
            }  
            else
            {
                System.out.println("invalid input");
            }
        }
        catch(InputMismatchException e)
        {
            System.out.println("Please enter numbers only.");
            sc.nextLine();
        }
      
          
         
      }
        
      
  }

}