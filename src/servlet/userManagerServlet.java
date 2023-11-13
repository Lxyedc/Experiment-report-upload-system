package servlet;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.WorkbookSettings;
import jxl.write.WritableWorkbook;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import bean.PageBean;
import bean.User;
import dao.DbConnection;

@SuppressWarnings("serial")
public class userManagerServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");
		String type = request.getParameter("type");
		if(type.equals("mod")){
			//修改学生信息
			int id = Integer.valueOf(request.getParameter("id"));
			String uid = request.getParameter("uid");
			String name = request.getParameter("name");
			String password = request.getParameter("password");
			String message = this.updateStudent(id,uid,name,password);
			User studentData = this.getStudentById(id);
			request.setAttribute("studentData",studentData);
			request.setAttribute("msg", message);
			request.getRequestDispatcher("/system/studentData.jsp").forward(request,response);		
		}else if(type.equals("info")){
			//获取学生详情
			int userId = Integer.valueOf(request.getParameter("userId"));
			User studentData = this.getStudentById(userId);
			request.setAttribute("studentData",studentData);
			request.getRequestDispatcher("/system/studentData.jsp").forward(request,response);
		}else if(type.equals("sel")){
			//查询学生信息
			String searchName = "all";
			if(request.getParameter("searchName")!=null){
				searchName = request.getParameter("searchName");
				request.setAttribute("searchName", searchName);
			}
			int pageNum = Integer.valueOf(request.getParameter("pageNum"));
			PageBean<User> studentList = this.getStudentList(searchName,pageNum);
			request.setAttribute("studentList",studentList);
			request.getRequestDispatcher("/system/studentList.jsp").forward(request,response);
		}
	}


	/**
	 * 修改学生信息
	 * @param id
	 * @param uid
	 * @param name
	 * @param password
	 * @return
	 */
	private String updateStudent(int id,String uid,String name,String password){
		String message = "";
		DbConnection dbConnection = new DbConnection();
		try{
			String sql = "update user set uid='"+uid+"',"
					  + " password='"+password+"',name='"+name+"'"
					  + " where id="+id;
			dbConnection.update(sql);
			message = "修改成功";
		}catch(Exception e){
			e.printStackTrace();
			message = "修改失败，请重新提交";
		}finally{
			dbConnection.close();
		}
		return message;
	}
	
	
	/**
	 * 获取学生信息
	 * @param id
	 * @return
	 */
	private User getStudentById(int id){
		
		DbConnection dbConnection = new DbConnection();
		User user = new User();
		try{
			String sql = "select * from user where id="+id;
			ResultSet resultSet =dbConnection.query(sql);
			if(resultSet!=null){
				while(resultSet.next()){
					user.setId(resultSet.getInt("id"));
					user.setUid(resultSet.getString("uid"));
					user.setName(resultSet.getString("name"));
					user.setPassword(resultSet.getString("password"));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dbConnection.close();
		}
		return user;
	}
	
	/**
	 * 获取学生信息列表
	 * @param pageNum
	 * @param searchName
	 * @return
	 */
	private PageBean<User> getStudentList(String searchName,int pageNum) {
		
		DbConnection dbConnection = new DbConnection();
		PageBean<User> page  = null;
		try {
			String selectsql = "select * from user "
							+ " where (name like '%"+searchName+"%' "
							+ " or uid like '%"+searchName+"%'"
							+ " or '"+searchName+"'='all')"
							+ " and role='student'";
			ResultSet resultSet = dbConnection.query(selectsql);
			List<User> userList = new ArrayList<User>();
			if(resultSet!=null){
				while(resultSet.next()){
					User user = new User();
					user.setId(resultSet.getInt("id"));
					user.setName(resultSet.getString("name"));
					user.setUid(resultSet.getString("uid"));
					user.setPassword(resultSet.getString("password"));
					userList.add(user);
				}
			}
			int totalRecord = userList.size();
			
			//每页显示的数量
			int pageSize = 0;
			if(totalRecord<1){
				//每页显示的数量
				pageSize = totalRecord;
			}else{
				//每页显示的数量
				pageSize = 5;
			}
			if(totalRecord >0){
				page  = new PageBean<User>(pageNum,pageSize,totalRecord);
				int startIndex = page.getStartIndex();
				if(pageNum<totalRecord){
					page.setStart(pageNum+1);
				}else{
					page.setStart(totalRecord);
				}
				
				int nextIndex = (pageNum * pageSize);
				
				if(nextIndex > totalRecord){
					nextIndex = totalRecord;
				}
				
				int endIndex = startIndex + nextIndex;
				if(endIndex > totalRecord){
					endIndex = totalRecord;
				}
				page.setList(userList.subList(startIndex, endIndex));
			}else{
				page = new PageBean<User>(pageNum,pageSize,0);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbConnection.close();
		}
		return page;
	}
}
