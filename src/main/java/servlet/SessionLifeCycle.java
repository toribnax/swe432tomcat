// From "Professional Java Server Programming", Patzer et al.,

// Import Servlet Libraries
import javax.servlet.*;
import javax.servlet.http.*;

// Import Java Libraries
import java.io.*;
import java.util.Date;
import javax.servlet.annotation.WebServlet;

@WebServlet(name = "session", urlPatterns = {"/sessionLifeCycle"})
public class SessionLifeCycle extends HttpServlet
{
 // The link will have a parameter builtin
      String lifeCycleURL = "/sessionLifeCycle";
public void doGet (HttpServletRequest request, HttpServletResponse response)
       throws ServletException, IOException
{
   String action = request.getParameter("action");

   if (action != null && action.equals("invalidate"))
   {  // Called from the invalidate button, kill the session.
      // Get session object
      HttpSession session = request.getSession();
      session.invalidate();

      response.setContentType("text/html");
      PrintWriter out = response.getWriter();

      out.println("<html>");
      out.println("<head>");
      out.println(" <title>Session lifecycle</title>");
      out.println("</head>");
      out.println("");
      out.println("<body>");

      out.println("<p>Your session has been invalidated.</P>");

      // Create a link so the user can create a new session.
    
      out.println("<a href=\"" + lifeCycleURL + "?action=newSession\">");
      out.println("Create new session</A>");

      out.println("</body>");
      out.println("</html>");
      out.close();
   } //end if
   else
   {  // We were called either directly or through the reload button.
      // Get session object
      HttpSession session = request.getSession();

      response.setContentType("text/html");
      PrintWriter out = response.getWriter();

      out.println("<html>");
      // no-cache lets the page reload by clicking on the reload link
      out.println("<meta http-equiv=\"Pragma\" content=\"no-cache\">");
      out.println("<head>");
      out.println(" <title>Session lifecycle</title>");
      out.println("</head>");
      out.println("");

      out.println("<body>");
      out.println("<h1><center>Session life cycle</center></h1>");
      out.print  ("<BR>Session status: ");
      if (session.isNew())
      {
         out.println ("New session.");
      }
      else
      {
         out.println ("Old session.");
      }
      // Get the session ID
      out.print  ("<br>Session ID: ");
      out.println(session.getId());
      // Get the created time, convert it to a Date object
      out.print  ("<br>Creation time: ");
      out.println(new Date (session.getCreationTime()));
      // Get the last time it was accessed
      out.print  ("<br>Last accessed time: ");
      out.println(new Date(session.getLastAccessedTime()));
      // Get the max-inactive-interval setting
      out.print  ("<br>Maximum inactive interval (seconds): ");
      out.println(session.getMaxInactiveInterval());

      out.print  ("<br><br><a href=\"" + lifeCycleURL + "?action=invalidate\">");
      out.println("Invalidate the session</a>");
      out.print  ("<br><a href=\"" + lifeCycleURL + "\">");
      out.println("Reload this page</a>");

      out.println("</body>");
      out.println("</html>");
      out.close();
   } //end else
} // End doGet
} //End  sessionLifeCycle
