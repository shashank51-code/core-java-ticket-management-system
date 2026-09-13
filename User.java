
import java.util.InputMismatchException;
import java.util.Scanner;

class User {

    int employeeId;
    String username;
    String password;
  
    
    static Scanner sc = new Scanner(System.in);

    public User(int employeeId,String username,String password)
    {
        this.employeeId=employeeId;
        this.username=username;
        this.password=password;
    }

    void signup(Application app)
    {
        System.out.println("================signup==============");
        System.out.println();
        System.out.println();
        System.out.println();
       
            System.out.println("username: ");
            String username=sc.next();
            boolean   usernameExists = false;
            for(int i=0;i<=app.users.length-1;i++)
            {
                if(app.users[i] !=null)
                {
                    if(username.equals(app.users[i].username))
                    {
                          usernameExists = true;
                          break;
                       
                    } 
                }
            }
            if(usernameExists==true)
            {
                 System.out.println("username already exists");
            }
            else
            {
                System.out.println("password: ");
                String userpassword=sc.next();

                System.out.println("Name: ");
                String name=sc.next();

                System.out.println("Email: ");
                String useremail=sc.next();

                System.out.println("phone: ");
                String phone=sc.next();

                System.out.println("Department: ");
                String departent=sc.next();
                int n=0; String role="";
                while(true)
                {

                    
                    System.out.println("Select Role \n 1. Employee \n 2. Support Employee \n 3. Manager ");
                    try
                    {
                        n = sc.nextInt();

                        if(n == 1)
                        {
                            role = "EMPLOYEE";
                            break;
                        }
                        else if(n == 2)
                        {
                            role = "SUPPORT";
                            break;
                        }
                        else if(n == 3)
                        
                        {
                            role = "MANAGER";
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
                if(n>=1 && n<=3)
                {
                    Employee employee=new Employee(name,useremail,phone,departent,role);
                    for(int i=0;i<app.employees.length;i++)
                    {
                        
                        if(app.employees[i]==null)
                        {
                            app.employees[i]=employee;
                            for(Manager manager:app.managers)
                            {
                                if(manager !=null)
                                {
                                    
                                    if(manager.department.equals(app.employees[i].getDepartment()))
                                    {
                                        manager.addEmployee(employee);
                                        
                                        break;
                                    }
                                }
                            }
                            break;  
                        }
                    }
                    
                        if(role.equals("SUPPORT"))
                        {
                            for(int i=0;i<=app.supportTeam.length-1;i++)
                            {
                                 if(app.supportTeam[i]==null)
                                {

                                    app.supportTeam[i]=employee;
                                    for(Manager manager:app.managers)
                                    {
                                        if(manager !=null)
                                        {
                                            if(manager.department.equals(employee.getDepartment()))
                                            {
                                                manager.addSupportEmployee(employee);                                                break;
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                           
                        }
                        else if(role.equals("MANAGER"))
                        {
                           
                            int employeeId=employee.getEmployeeId();
                            Manager manager=new Manager(employeeId,name,useremail,departent,role);
                            for(int i=0;i<app.managers.length;i++)
                            {
                                if(app.managers[i]==null)
                                {
                                    app.managers[i]=manager;
                                    for(Employee emp:app.employees)
                                    {
                                        if(emp !=null)
                                        {

                                        
                                            if(manager.department.equals(emp.getDepartment()))
                                            {
                                                if(emp.getRole().equals("SUPPORT"))
                                                {
                                                    manager.addSupportEmployee(emp);
                                                }
                                                else
                                                {
                                                    manager.addEmployee(emp);
                                                }
                                                
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                            
                        }
                    int employeeId=employee.getEmployeeId();
                    User user=new User(employeeId, username, userpassword);
                    for(int i=0;i<=app.users.length-1;i++)
                    {
                        if(app.users[i] ==null)
                        {
                            app.users[i]=user;
                            break;
                        }
                    }
                    System.out.println("Signup successful!");
                }
            }
    }
    void login(Application app)
    {
        boolean loginSuccess=false;
        System.out.println("enter username");
        String username=sc.next();
        System.out.println("enter password");
        String password=sc.next();
        for(int i=0;i<=app.users.length-1;i++)
        {
            if(app.users[i] !=null)
            {
                if(username.equals(app.users[i].username) && password.equals(app.users[i].password))
                {
                    
                    int employeeId=app.users[i].employeeId;
                    for(Employee emp:app.employees)
                    {
                        if(emp !=null)
                        {           

                            if(emp.getEmployeeId()==employeeId)
                            {
                                loginSuccess=true;
                                String role=emp.getRole();
                                if(role.equals("EMPLOYEE"))
                                {
                                    emp.showEmployeeMenu();
                                    break;
                                }
                                else if(role.equals("SUPPORT"))
                                {
                                    for(Manager manager : app.managers)
                                    {
                                        if(manager != null)
                                        {
                                            if(manager.department.equals(emp.getDepartment()))
                                            {
                                                emp.showSupportMenu(manager);
                                                break;
                                            }
                                           
                                        }
                                    }
                                }
                            
                                else if(role.equals("MANAGER"))
                                {
                                    for(Manager manager : app.managers)
                                        {
                                            if(manager != null)
                                            {
                                                if(manager.managerId == employeeId)
                                                {
                                                    manager.showManagerMenu(app);
                                                    break;
                                                }
                                            }
                                        }
                                
                                }
                            }
                        }    
                    }
                }
               
            }
        }
        if(!loginSuccess)
        {
            System.out.println("Invalid username or password");
        }
    }
}
