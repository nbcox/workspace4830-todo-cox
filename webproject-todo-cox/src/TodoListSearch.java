import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/TodoListSearch")
public class TodoListSearch extends HttpServlet {
   private static final long serialVersionUID = 1L;

   public TodoListSearch() {
      super();
   }

   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String keyword1 = request.getParameter("keyword1");
      String keyword2 = request.getParameter("keyword2");
      search(keyword1, keyword2, response);
   }

   void search(String keyword1, String keyword2, HttpServletResponse response) throws IOException {
      response.setContentType("text/html");
      PrintWriter out = response.getWriter();
      String title = "Database Result";
      String docType = "<!doctype html public \"-//w3c//dtd html 4.0 " + //
            "transitional//en\">\n"; //
      out.println(docType + //
            "<html>\n" + //
            "<head><title>" + title + "</title></head>\n" + //
            "<body bgcolor=\"#f0f0f0\">\n" + //
            "<h1 align=\"center\">" + title + "</h1>\n");

      Connection connection = null;
      PreparedStatement preparedStatement = null;
      try {
         DBConnection.getDBConnection(getServletContext());
         connection = DBConnection.connection;

         if (keyword1.isEmpty() && keyword2.isEmpty()) {
            String selectSQL = "SELECT * FROM todoList";
            preparedStatement = connection.prepareStatement(selectSQL);
         } else {
        	 if (!keyword1.isEmpty() && keyword2.isEmpty()) {
        		 String selectSQL = "SELECT * FROM todoList WHERE TASKNAME LIKE ?";
        		 String theTask = keyword1 + "%";
        		 preparedStatement = connection.prepareStatement(selectSQL);
                 preparedStatement.setString(1, theTask);
        	 }
        	 else if (keyword1.isEmpty() && !keyword2.isEmpty()) {
        		 String selectSQL = "SELECT * FROM todoList WHERE DATE LIKE ?";
                 String theDate = keyword2 + "%";
                 preparedStatement = connection.prepareStatement(selectSQL);
                 preparedStatement.setString(1, theDate);
        	 }
        	 else {
        		 String selectSQL = "SELECT * FROM todoList WHERE TASKNAME LIKE ? OR DATE LIKE ?";
                 String theTask = keyword1 + "%";
                 String theDate = keyword2 + "%";
                 preparedStatement = connection.prepareStatement(selectSQL);
                 preparedStatement.setString(1, theTask);
                 preparedStatement.setString(2, theDate);
        	 }
         }
         ResultSet rs = preparedStatement.executeQuery();

         while (rs.next()) {
            int id = rs.getInt("id");
            String taskName = rs.getString("taskName").trim();
            String date = rs.getString("date").trim();

            if ((keyword1.isEmpty() && keyword2.isEmpty()) || (taskName.contains(keyword1) || date.contains(keyword2))) {
               out.println("Task Name: " + taskName + ", ");
               out.println("Due Date: " + date + "<br>");
            }
         }
         out.println("<a href=/webproject-todo-cox/todoSearch.html>Search Again</a> <br>");
         out.println("<a href=/webproject-todo-cox/todo.html>Home</a> <br>");
         out.println("</body></html>");
         rs.close();
         preparedStatement.close();
         connection.close();
      } catch (SQLException se) {
         se.printStackTrace();
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         try {
            if (preparedStatement != null)
               preparedStatement.close();
         } catch (SQLException se2) {
         }
         try {
            if (connection != null)
               connection.close();
         } catch (SQLException se) {
            se.printStackTrace();
         }
      }
   }

   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      doGet(request, response);
   }

}
