package servlet;

import java.util.List;
import java.util.ArrayList;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;

@WebServlet(name = "JSONPersistence", urlPatterns = {"/json"})
public class JSONPersistenceServlet extends HttpServlet{
  static enum Data {AGE, NAME};
  static String RESOURCE_FILE = "entries.json";
  static final String VALUE_SEPARATOR = ";";

  static String Domain  = "";
  static String Path    = "/";
  static String Servlet = "json";

  // Button labels
  static String OperationAdd = "Add";

  // Other strings.
  static String Style =
    "https://www.cs.gmu.edu/~offutt/classes/432/432-style.css";

  public class Entry {
    String name;
    Integer age;
  }

  public class Entries{
    List<Entry> entries;
  }

  public class EntryManager{
    private String filePath = null;

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public boolean save(String name, Integer age){
      try{
        Entries entries = getAll();
        Entry newEntry = new Entry();
        newEntry.name = name;
        newEntry.age = age;
        entries.entries.add(newEntry);
        FileWriter fileWriter = new FileWriter(filePath);
        new Gson().toJson(entries, fileWriter);
        fileWriter.flush();
        fileWriter.close();
      }catch(IOException ioException){
        ioException.printStackTrace();
        return false;
      }

      return true;
    }

    private Entries getAll(){
      Entries entries =  entries = new Entries();
      entries.entries = new ArrayList();

      try{
        File file = new File(filePath);
        if(!file.exists()){
          return entries;
        }

        BufferedReader bufferedReader =
          new BufferedReader(new FileReader(file));
        Entries readEntries =
          new Gson().fromJson(bufferedReader, Entries.class);
          
        if(readEntries != null && readEntries.entries != null){
          entries = readEntries;
        }
        bufferedReader.close();

      }catch(IOException ioException){
        ioException.printStackTrace();
      }

      return entries;
    }

    public String getAllAsHTMLTable(){
      return "";
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
     Integer age = null;

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
            age=new Integer(rawAge);
            if(age<1){
                error+= "<li>Age must be an integer greater than 0.</li>";
                rawAge = "";
            }else{
              if(age>150){
                  error+= "<li>Age must be an integer less than 150.</li>";
                  rawAge = "";
              }
            }
          }catch (Exception e) {
            error+= "<li>Age must be an integer greater than 0.</li>";
            rawAge = "";
          }
     }

     response.setContentType("text/html");
     PrintWriter out = response.getWriter();

     if (error.length() == 0){
       EntryManager entryManager = new EntryManager();
       entryManager.setFilePath(RESOURCE_FILE);
       entryManager.save(name, age);



       PrintHead(out);
       PrintEntriesBody(out, RESOURCE_FILE);
       PrintTail(out);
     }else{
       PrintHead(out);
       PrintBody(out, name, rawAge, error);
       PrintTail(out);
     }


  }

  /** *****************************************************
   *  Overrides HttpServlet's doGet().
   *  Prints an HTML page with a blank form.
  ********************************************************* */
  @Override
  public void doGet (HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException{
     response.setContentType("text/html");
     PrintWriter out = response.getWriter();
     PrintHead(out);
     PrintBody(out, "", "", "");
     PrintTail(out);
  }

  /** *****************************************************
   *  Prints the <head> of the HTML page, no <body>.
  ********************************************************* */
  private void PrintHead (PrintWriter out){
     out.println("<html>");
     out.println("");
     out.println("<head>");
     out.println("<title>File Persistence Example</title>");
     out.println(
     " <link rel=\"stylesheet\" type=\"text/css\" href=\"" + Style + "\">");
     out.println("</head>");
     out.println("");
  }

  /** *****************************************************
   *  Prints the <BODY> of the HTML page
  ********************************************************* */
  private void PrintBody (
    PrintWriter out, String name, String age, String error){
     out.println("<body>");
     out.println("<p>");
     out.println(
     "A simple example that demonstrates how to keep data in a file");
     out.println("</p>");

     if(error != null && error.length() > 0){
       out.println(
       "<p style=\"color:red;\"> We encounter the following issues:</p>");
       out.println("<ol>");
       out.println(error);
       out.println("</ol>");
     }

     out.print  ("<form method=\"post\"");
     out.println(" action=\""+Domain+Path+Servlet+"\">");
     out.println("");
     out.println(" <table>");
     out.println("  <tr>");
     out.println("   <td>Name:</td>");
     out.println("   <td><input type=\"text\" name=\""+Data.NAME.name()
      +"\" value=\""+name+"\" size=30 required></td>");
     out.println("  </tr>");
     out.println("  <tr>");
     out.println("   <td>Age:</td>");
     out.println("   <td><input type=\"text\"  name=\""+Data.AGE.name()
      +"\" oninput=\"this.value=this.value.replace(/[^0-9]/g,'');\" value=\""
      +age+"\" size=3 required></td>");
     out.println("  </tr>");
     out.println(" </table>");
     out.println(" <br>");
     out.println(" <br>");
     out.println(" <input type=\"submit\" value=\"" + OperationAdd
      + "\" name=\"Operation\">");
     out.println(" <input type=\"reset\" value=\"Reset\" name=\"reset\">");
     out.println("</form>");
     out.println("");
     out.println("</body>");
  }

  /** *****************************************************
   *  Prints the <BODY> of the HTML page
  ********************************************************* */
  private void PrintEntriesBody (PrintWriter out, String resourcePath){
    out.println("<body>");
    out.println("<p>");
    out.println("A simple example that shows entries persisted on a file");
    out.println("</p>");
    out.println("");
    out.println(" <table>");

    try {

        out.println("  <tr>");
        out.println("   <th>Name</th>");
        out.println("   <th>Age</th>");
        out.println("  </tr>");
        File file = new File(resourcePath);
        if(!file.exists()){
          out.println("  <tr>");
          out.println("   <td>No entries persisted yet.</td>");
          out.println("  </tr>");
          return;
        }

        BufferedReader BufferedReader =
          new BufferedReader(new FileReader(file));
        String line;
        while ((line = BufferedReader.readLine()) != null) {
          String []  entry= line.split(VALUE_SEPARATOR);
          out.println("  <tr>");
          for(String value: entry){
              out.println("   <td>"+value+"</td>");
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
  }

  /** *****************************************************
   *  Prints the bottom of the HTML page.
  ********************************************************* */
  private void PrintTail (PrintWriter out){
     out.println("");
     out.println("</html>");
  }
}
