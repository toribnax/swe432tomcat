package servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

@WebServlet(name = "XMLPersistence", urlPatterns = {"/xml"})
public class XMLPersistenceServlet extends HttpServlet{
  static enum Data {AGE, NAME};
  static String RESOURCE_FILE = "entries.xml";
  static final String VALUE_SEPARATOR = ";";

  static String Domain  = "";
  static String Path    = "/";
  static String Servlet = "xml";

  // Button labels
  static String OperationAdd = "Add";

  // Other strings.
  static String Style =
    "https://www.cs.gmu.edu/~offutt/classes/432/432-style.css";

    public class XMLWriter {
      private String filePath = null;
      public void setFilePath(String filePath) {
          this.filePath = filePath;
      }

      public void save(String name, Integer age) throws Exception {
          XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
          XMLEventWriter eventWriter = outputFactory
                  .createXMLEventWriter(new FileOutputStream(filePath));
          XMLEventFactory eventFactory = XMLEventFactory.newInstance();
          XMLEvent end = eventFactory.createDTD("\n");
          StartDocument startDocument = eventFactory.createStartDocument();
          eventWriter.add(startDocument);
          eventWriter.add(end);
          StartElement configStartElement = eventFactory.createStartElement("",
                  "", "entries");
          eventWriter.add(configStartElement);
          eventWriter.add(end);
          addEntry(eventWriter, name, age);
          eventWriter.add(eventFactory.createEndElement("", "", "entries"));
          eventWriter.add(end);
          eventWriter.add(eventFactory.createEndDocument());
          eventWriter.close();
      }

      private void addEntry(XMLEventWriter eventWriter, String name,
              Integer age) throws XMLStreamException {
          XMLEventFactory eventFactory = XMLEventFactory.newInstance();
                XMLEvent end = eventFactory.createDTD("\n");
                XMLEvent tab = eventFactory.createDTD("\t");
          StartElement entryStartElement = eventFactory.createStartElement("",
                        "", "entry");
          eventWriter.add(entryStartElement);
          eventWriter.add(end);
          createNode(eventWriter, "name", name);
          createNode(eventWriter, "age", String.valueOf(age));
          eventWriter.add(eventFactory.createEndElement("", "", "entry"));
          eventWriter.add(end);

      }
      private void createNode(XMLEventWriter eventWriter, String name,
            String value) throws XMLStreamException {

        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD("\t");
        // create Start node
        StartElement sElement = eventFactory.createStartElement("", "", name);
        eventWriter.add(tab);
        eventWriter.add(sElement);
        // create Content
        Characters characters = eventFactory.createCharacters(value);
        eventWriter.add(characters);
        // create End node
        EndElement eElement = eventFactory.createEndElement("", "", name);
        eventWriter.add(eElement);
        eventWriter.add(end);

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
            age =new Integer(rawAge);
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
       // PrintWriter entriesPrintWriter = new PrintWriter(
       //    new FileWriter(RESOURCE_FILE, true), true
       // );

       XMLWriter entriesXMLWriter = new XMLWriter();
        entriesXMLWriter.setFilePath(RESOURCE_FILE);
        try {
            entriesXMLWriter.save(name, age);
        } catch (Exception e) {
            e.printStackTrace();
        }


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
