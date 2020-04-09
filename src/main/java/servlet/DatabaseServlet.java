package servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.net.URISyntaxException;


import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import javax.servlet.annotation.WebServlet;
@WebServlet(name = "DBPersistence", urlPatterns = {"/database"})

public class DatabaseServlet extends HttpServlet{
  static enum Data {AGE, NAME};

  static String Domain  = "";
  static String Path    = "";
  static String Servlet = "database";

  // Button labels
  static String OperationAdd = "Add";

  // Other strings.
  static String Style ="https://www.cs.gmu.edu/~offutt/classes/432/432-style.css";

  private static Connection connection = null;

  private class EntriesManager{
      private Connection getConnection()
        throws URISyntaxException, SQLException {
          String dbUrl = System.getenv("JDBC_DATABASE_URL");
          return DriverManager.getConnection(dbUrl);
      }

      public boolean save(String name, int age){
        PreparedStatement statement = null;
        try {
          connection = connection == null ? getConnection() : connection;
          statement = connection.prepareStatement(
          "INSERT INTO entries (name, age) values (?, ?)"
          );
          statement.setString(1, name);
          statement.setInt(2, age);
          statement.executeUpdate();
        }catch(URISyntaxException uriSyntaxException){
          uriSyntaxException.printStackTrace();
        }
        catch (Exception exception) {
          exception.printStackTrace();
        }

        return false;
      }
      public String [] getAll(){
        return null;
      }
    }

  /** *****************************************************
   *  Overrides HttpServlet's doPost().
   *  Converts the values in the form, performs the operation
   *  indicated by the submit button, and sends the results
   *  back to the client.
  ********************************************************* */
  @Override
  public void doPost (HttpServletRequest request, HttpServletResponse response)
     throws ServletException, IOException
  {
     String name = request.getParameter(Data.NAME.name());
     String rawAge = request.getParameter(Data.AGE.name());
     Integer age  = null;

     String error = "";
     if(name == null){
       error= "<li>Name is required</li>";
       name = "";
     }

     if(rawAge == null){
       error+= "<li>Age is required.<li>";
       rawAge = "";
     }else{
          try{
            age =new Integer(rawAge);
            if(age<1){
                error+= "<li>Age must be an integer greater than 0.</li>";
            }else{
              if(age>1000){
                  error+= "<li>Age must be an integer less than 1000.</li>";
              }
            }
          }catch (Exception e) {
            error+= "<li>Age must be an integer greater than 0.</li>";
          }finally{
            rawAge = "";
          }
     }

     response.setContentType("text/html");
     PrintWriter out = response.getWriter();

     if (error.length() == 0){
       EntriesManager entriesManager = new EntriesManager();

       entriesManager.save(name,age);

       PrintHead(out);
       //PrintEntriesBody(out, resourcePath);
       PrintTail(out);
     }else{
       PrintHead(out);
       PrintBody(out, name, rawAge, error);
       PrintTail(out);
     }


  }  // End doPost

  /** *****************************************************
   *  Overrides HttpServlet's doGet().
   *  Prints an HTML page with a blank form.
  ********************************************************* */
  @Override
  public void doGet (HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException
  {
     response.setContentType("text/html");
     PrintWriter out = response.getWriter();
     PrintHead(out);
     PrintBody(out, "", "", "");
     PrintTail(out);
  } // End doGet

  /** *****************************************************
   *  Prints the <head> of the HTML page, no <body>.
  ********************************************************* */
  private void PrintHead (PrintWriter out)
  {
     out.println("<html>");
     out.println("");

     out.println("<head>");
     out.println("<title>File Persistence Example</title>");
     out.println(" <link rel=\"stylesheet\" type=\"text/css\" href=\"" + Style + "\">");
     out.println("</head>");
     out.println("");
  } // End PrintHead

  /** *****************************************************
   *  Prints the <BODY> of the HTML page
  ********************************************************* */
  private void PrintBody (PrintWriter out, String name, String age, String error)
  {
     out.println("<body>");
     out.println("<p>");
     out.println("A simple example that demonstrates how to keep data in a file");
     out.println("</p>");

     if(error != null && error.length() > 0){
       out.println("<p style=\"color:red;\"> We encounter the following issues:</p>");
       out.println("<ol>");
       out.println(error);
       out.println("</ol>");
     }

     out.print  ("<form method=\"post\"");
     out.println(" action=\"/" + Servlet + "\">");
     out.println("");
     out.println(" <table>");
     out.println("  <tr>");
     out.println("   <td>Name:");
     out.println("   <td><input type=\"text\" name=\""+Data.NAME.name()+"\" value=\""+name+"\" size=30 required>");
     out.println("  </tr>");
     out.println("  <tr>");
     out.println("   <td>Age:");
     out.println("   <td><input type=\"text\" oninput=\"this.value=this.value.replace(/[^0-9]/g,'');\" name=\""+Data.AGE.name()+"\" value=\""+age+"\" size=3 required>");
     out.println("  </tr>");
     out.println(" </table>");
     out.println(" <br>");
     out.println(" <br>");
     out.println(" <input type=\"submit\" value=\"" + OperationAdd + "\" name=\"Operation\">");
     out.println(" <input type=\"reset\" value=\"Reset\" name=\"reset\">");
     out.println("</form>");
     out.println("");
     out.println("</body>");
  } // End PrintBody

  /** *****************************************************
   *  Prints the <BODY> of the HTML page
  ********************************************************* */
  private void PrintEntriesBody (PrintWriter out, String resourcePath)
  {
     out.println("<body>");
     out.println("<p>");
     out.println("A simple example that shows entries persisted on a file");
     out.println("</p>");
     out.println("");
     out.println(" <table>");

      try {

          out.println("  <tr>");
          out.println("   <td>Name");
          out.println("   <td>Age");
          out.println("  </tr>");
          File file = new File(resourcePath);
          if(!file.exists()){
            out.println("  <tr>");
            out.println("   <td>No entries persisted yet.");
            out.println("   <td>");
            out.println("  </tr>");
            return;
          }

          BufferedReader BufferedReader = new BufferedReader(new FileReader(file));
          String line ;
          while ((line = BufferedReader.readLine()) != null) {
            String []  entry= line.split(" ");
            out.println("  <tr>");
            for(String value: entry){
                out.println("   <td>"+value);
            }
            out.println("  </tr>");
          }
        } catch (FileNotFoundException ex) {
              ex.printStackTrace();
          } catch (IOException ex) {
              ex.printStackTrace();
          }
     out.println(" </table>");
     out.println("");
     out.println("</body>");
  } // End PrintBody


  /** *****************************************************
   *  Prints the bottom of the HTML page.
  ********************************************************* */
  private void PrintTail (PrintWriter out)
  {
     out.println("");
     out.println("</html>");
  } // End PrintTail

}  // End twoButtons
