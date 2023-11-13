package servlet;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import dao.DbConnection;
import bean.User;

@SuppressWarnings("serial")
public class loginServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		DbConnection dbConnection = new DbConnection();
		request.setCharacterEncoding("utf-8");
		String uid = request.getParameter("uid");
		String password = request.getParameter("password");
		String role = request.getParameter("role");
		String verifyCode = request.getParameter("verifyCode");
		HttpSession session = request.getSession();
		String selectsql = "";
		ResultSet resultSet = null;
		System.out.println("uid:"+uid);
		System.out.println("password:"+password);
		System.out.println("role:"+role); 
		System.out.println("verifyCode:"+verifyCode);
		try {
			if(session.getAttribute("sessioncode")!=null){
				String sessionCode = session.getAttribute("sessioncode").toString();
				if(!verifyCode.equals(sessionCode)){
					request.setAttribute("msg","输入验证码错误");
					request.getRequestDispatcher("/login.jsp").forward(request,
							response);
				}else{
					//查询用户是否存在
					selectsql = "select * from user "
							+ "where uid='" + uid + "' "
							+ "and password='" + password + "' "
							+ "and role='"+role+"'";
					resultSet = dbConnection.query(selectsql);
					if (resultSet!= null && resultSet.next()) {
						User user = new User();
						user.setId(resultSet.getInt("id"));
						user.setUid(resultSet.getString("uid"));
						user.setName(resultSet.getString("name"));
						user.setPassword(resultSet.getString("password"));
						user.setRole(resultSet.getString("role"));
						session.setAttribute("user",user);
						request.getRequestDispatcher("/system/index.jsp").forward(request,
									response);
					}else{
						request.setAttribute("msg","用户名或密码错误，请重新登录");
						request.getRequestDispatcher("/login.jsp").forward(request,
								response);
					}
				}
			}else{
				request.setAttribute("msg","输入验证码错误");
				request.getRequestDispatcher("/login.jsp").forward(request,
						response);
			}
			dbConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
