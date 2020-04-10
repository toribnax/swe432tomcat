package servlet;

import com.google.gson.Gson;

import java.io.PrintWriter;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "EchoServlet", urlPatterns = {"/echo"})
public class EchoServlet extends HttpServlet{
  @Override
   protected void doPost  (HttpServletRequest req, HttpServletResponse res)
          throws ServletException, IOException{

     res.setContentType ("aplication/json");
     res.setHeader("Access-Control-Allow-Origin", "*");
     res.setHeader("Access-Control-Allow-Methods", "POST");
     res.setHeader("Access-Control-Allow-Headers", "*");

     PrintWriter out = res.getWriter();

     Map<String, String[]> parameterMap = req.getParameterMap();
     Map<String, String> data = new HashMap<String, String>();
     for (String key: parameterMap.keySet()) {
         String parameter = parameterMap.get(key)[0];
         data.put(key, parameter);
     }
     out.print(new Gson().toJson(data));
     out.flush();
     out.close();
    }
}
