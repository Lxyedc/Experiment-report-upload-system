package servlet;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

import bean.Classes;
import bean.PageBean;
import bean.Project;
import bean.User;
import dao.DbConnection;

@SuppressWarnings("serial")
public class classesManagerServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String type = request.getParameter("type");
		if(type!=null){
			if(type.equals("add")){
				//新增课程
				String name = request.getParameter("name");
				String grade = request.getParameter("grade");
				int teacherId = Integer.valueOf(request.getParameter("teacherId"));
				String message = this.saveClasses(name,grade,teacherId);
				 //创建对应的文件夹(名称：课程名+班级)
				name = name + grade;
		        String path = "D:/baobiao/fileUploadSystem/WebContent/report/"+ name;
		        File file = new File(path);
		        if(!file.exists()){
		        	file.mkdir();
		        }
				request.setAttribute("msg", message);
				request.getRequestDispatcher("/system/addClasses.jsp").forward(request,response);		
			}else if(type.equals("sel")){
				//查看课程列表
				int teacherId = Integer.valueOf(request.getParameter("id"));
				int pageNum = Integer.valueOf(request.getParameter("pageNum"));
				String searchName = "all";
				if(request.getParameter("searchName")!=null){
					searchName = request.getParameter("searchName");
					request.setAttribute("searchName", searchName);
				}
				PageBean<Classes> classesList = this.getClassesStudentList(teacherId,searchName,pageNum);
				request.setAttribute("classesList", classesList);
				request.getRequestDispatcher("/system/classesList.jsp").forward(request,response);
			}else if(type.equals("info")){
				//查看课程对应的学生列表
				int classesId = Integer.valueOf(request.getParameter("id"));
				int pageNum = Integer.valueOf(request.getParameter("pageNum"));
				String searchName = "all";
				if(request.getParameter("searchName")!=null){
					searchName = request.getParameter("searchName");
					request.setAttribute("searchName", searchName);
				}
				PageBean<User> userList = this.getClassesUserList(classesId,searchName,pageNum);
				request.setAttribute("userList", userList);
				request.getRequestDispatcher("/system/classesUserList.jsp").forward(request,response);
			}else if(type.equals("delUser")){
				//删除课程中的学生
				int id = Integer.valueOf(request.getParameter("id"));
				String message = this.delClassesByUser(id);
				int classesId = Integer.valueOf(request.getParameter("classesId"));
				int pageNum = Integer.valueOf(request.getParameter("pageNum"));
				String searchName = "all";
				if(request.getParameter("searchName")!=null){
					searchName = request.getParameter("searchName");
					request.setAttribute("searchName", searchName);
				}
				PageBean<User> userList = this.getClassesUserList(classesId,searchName,pageNum);
				request.setAttribute("msg", message);
				request.setAttribute("userList", userList);
				request.getRequestDispatcher("/system/classesUserList.jsp").forward(request,response);
			}else if(type.equals("del")){
				//删除课程
				int id = Integer.valueOf(request.getParameter("id"));
				int num = this.getClassesByUserNum(id);
				String message = "";
				if(num == 0){
					message = this.deleteClasses(id);
				}else{
					message = "该课程还有学生数据存在，请先清空学生数据，再删除！";
				}
				int teacherId = Integer.valueOf(request.getParameter("teacherId"));
				int pageNum = Integer.valueOf(request.getParameter("pageNum"));
				String searchName = "all";
				if(request.getParameter("searchName")!=null){
					searchName = request.getParameter("searchName");
					request.setAttribute("searchName", searchName);
				}
				request.setAttribute("msg", message);
				PageBean<Classes> classesList = this.getClassesStudentList(teacherId,searchName,pageNum);
				request.setAttribute("classesList", classesList);
				request.getRequestDispatcher("/system/classesList.jsp").forward(request,response);
			}else if(type.equals("download")){
				//下载课程学生上传模板
				 String path = request.getSession().getServletContext().getRealPath("/template");
				 this.downloadData(path,response);
			}else if(type.equals("export")){
				//上传课程学生
				this.exportClassesUserData(request,response);
			}else if(type.equals("selClasses")){
				//获取所有课程信息
				int teacherId = Integer.valueOf(request.getParameter("id"));
				int pageNum = Integer.valueOf(request.getParameter("pageNum"));
				String searchName = "all";
				if(request.getParameter("searchName")!=null){
					searchName = request.getParameter("searchName");
					request.setAttribute("searchName", searchName);
				}
				PageBean<Classes> classesList = this.getClassesList(teacherId,searchName,pageNum);
				request.setAttribute("classesList", classesList);
				request.getRequestDispatcher("/system/classesByProjectList.jsp").forward(request,response);
			}else if(type.equals("delClasses")){
				//删除课程
				int id = Integer.valueOf(request.getParameter("id"));
				int num = this.getClassesByProjectNum(id);
				String message = "";
				if(num == 0){
					message = this.deleteClasses(id);
				}else{
					message = "该课程还有实验项目存在，请先清空实验项目，再删除！";
				}
				int teacherId = Integer.valueOf(request.getParameter("id"));
				int pageNum = Integer.valueOf(request.getParameter("pageNum"));
				String searchName = "all";
				if(request.getParameter("searchName")!=null){
					searchName = request.getParameter("searchName");
					request.setAttribute("searchName", searchName);
				}
				request.setAttribute("msg", message);
				PageBean<Classes> classesList = this.getClassesList(teacherId,searchName,pageNum);
				request.setAttribute("classesList", classesList);
				request.getRequestDispatcher("/system/classesByProjectList.jsp").forward(request,response);
			}		
		}
	}

	
	/**
	 * 新增课程信息
	 * @param name
	 * @param grade
	 * @param teacherId
	 * @return
	 */
	private String saveClasses(String name,String grade,int teacherId){
		String message = "";
		DbConnection dbConnection = new DbConnection();
		try{
			String sql = "insert into classes (name,grade,teacherId) "
					   + "values('"+name+"','"+grade+"',"+teacherId+")";
			dbConnection.update(sql);
			message = "录入成功";
		}catch(Exception e){
			e.printStackTrace();
			message = "录入失败，请重新提交";
		}finally{
			dbConnection.close();
		}
		return message;
	}
	
	
	/**
	 * 获取课程学生信息
	 * @param teacherId
	 * @param searchName
	 * @param pageNum
	 * @return
	 */
	private PageBean<Classes> getClassesStudentList(int teacherId,String searchName,int pageNum) {
		
		DbConnection dbConnection = new DbConnection();
		PageBean<Classes> page  = null;
		try {
			String selectsql = "select c.id,c.name,c.grade,count(u2.id) as count"
							+ " from classes as c left join user as u "
							+ " on c.teacherId=u.id"
							+ " left join classestouser as ctu on ctu.classesId=c.id"
							+ " left join user as u2 on u2.id=ctu.studentId "
							+ " where u.id="+teacherId 
							+ " and (c.name like '%"+searchName+"%' or c.grade like '%"+searchName+"%' or '"+searchName+"'='all') "
							+ " group by c.id,c.name,c.grade";
			ResultSet resultSet = dbConnection.query(selectsql);
			List<Classes> classeslList = new ArrayList<Classes>();
			if(resultSet!=null){
				while(resultSet.next()){
					Classes classes = new Classes();
					classes.setId(resultSet.getInt("id"));
					classes.setGrade(resultSet.getString("grade"));
					classes.setName(resultSet.getString("name"));
					classes.setCount(resultSet.getInt("count"));
					classeslList.add(classes);
				}
			}
			int totalRecord = classeslList.size();
			
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
				page  = new PageBean<Classes>(pageNum,pageSize,totalRecord);
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
				page.setList(classeslList.subList(startIndex, endIndex));
			}else{
				page = new PageBean<Classes>(pageNum,pageSize,0);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbConnection.close();
		}
		return page;
	}
	
	
	/**
	 * 获取课程信息
	 * @param teacherId
	 * @param searchName
	 * @param pageNum
	 * @return
	 */
	private PageBean<Classes> getClassesList(int teacherId,String searchName,int pageNum) {
		
		DbConnection dbConnection = new DbConnection();
		PageBean<Classes> page  = null;
		try {
			String selectsql = "select c.id,c.name,c.grade,count(p.id) as count"
							+ " from classes as c left join user as u "
							+ " on c.teacherId=u.id"
							+ " left join project as p on c.id=p.classesId"
							+ " where u.id="+teacherId 
							+ " and (c.name like '%"+searchName+"%' or c.grade like '%"+searchName+"%' or '"+searchName+"'='all') "
							+ " group by c.id,c.name,c.grade";
			ResultSet resultSet = dbConnection.query(selectsql);
			List<Classes> classeslList = new ArrayList<Classes>();
			if(resultSet!=null){
				while(resultSet.next()){
					Classes classes = new Classes();
					classes.setId(resultSet.getInt("id"));
					classes.setGrade(resultSet.getString("grade"));
					classes.setName(resultSet.getString("name"));
					classes.setCount(resultSet.getInt("count"));
					classeslList.add(classes);
				}
			}
			int totalRecord = classeslList.size();
			
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
				page  = new PageBean<Classes>(pageNum,pageSize,totalRecord);
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
				page.setList(classeslList.subList(startIndex, endIndex));
			}else{
				page = new PageBean<Classes>(pageNum,pageSize,0);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbConnection.close();
		}
		return page;
	}
	
	/**
	 * 获取课程对应的学生信息
	 * @param classesId
	 * @param searchName
	 * @param pageNum
	 * @return
	 */
	private PageBean<User> getClassesUserList(int classesId,String searchName,int pageNum) {
		
		DbConnection dbConnection = new DbConnection();
		PageBean<User> page  = null;
		try {
			String selectsql = "select ctu.id,u.name,u.uid"
							+ " from user as u"
							+ " left join classestouser as ctu on ctu.studentId=u.id"
							+ " left join classes as c on c.id=ctu.classesId"
							+ " and (u.name like '%"+searchName+"%' or u.uid like '%"+searchName+"%' or '"+searchName+"'='all') "
							+ " where c.id="+classesId;
			ResultSet resultSet = dbConnection.query(selectsql);
			List<User> userList = new ArrayList<User>();
			if(resultSet!=null){
				while(resultSet.next()){
					User student = new User();
					student.setId(resultSet.getInt("id"));
					student.setName(resultSet.getString("name"));
					student.setUid(resultSet.getString("uid"));
					userList.add(student);
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
	
	/**
	 * 删除课程对应的学生信息
	 * @param classesByUserId
	 * @return
	 */
	private String delClassesByUser(int classesByUserId) {
		String message = "";
		DbConnection dbConnection = new DbConnection();
		try{
			String updateSql = "delete from classestoUser where id="+classesByUserId;
			dbConnection.update(updateSql);		
			message = "删除成功";
		}catch(Exception e){
			e.printStackTrace();
			message = "删除失败";
		}finally{
			dbConnection.close();
		}
		return message;
	}

	
	
	/**
	 * 获取课程的学生数量
	 * @param classesId
	 * @return
	 */
	private int getClassesByUserNum(int classesId)
   {
		int num = 0;
		DbConnection dbConnection = new DbConnection();
		try{
			String sql = "select count(ctu.id) as num from classestouser as ctu where ctu.classesId="+classesId;
			ResultSet resultSet = dbConnection.query(sql);
			if(resultSet!=null && resultSet.next()){
				num = resultSet.getInt("num");
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dbConnection.close();
		}
		return num;
	}
	
	/**
	 * 获取课程的实验项目数量
	 * @param classesId
	 * @return
	 */
	private int getClassesByProjectNum(int classesId)
   {
		int num = 0;
		DbConnection dbConnection = new DbConnection();
		try{
			String sql = "select count(p.id) as num from project as p where p.classesId="+classesId;
			ResultSet resultSet = dbConnection.query(sql);
			if(resultSet!=null && resultSet.next()){
				num = resultSet.getInt("num");
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dbConnection.close();
		}
		return num;
	}
	
	
	/**
	 * 删除课程信息
	 * @param classesId
	 * @return
	 */
	private String deleteClasses(int classesId) {
		String message = "";
		DbConnection dbConnection = new DbConnection();
		try{
			String updateSql = "delete from classes where id="+classesId;
			dbConnection.update(updateSql);		
			message = "删除成功";
		}catch(Exception e){
			e.printStackTrace();
			message = "删除失败";
		}finally{
			dbConnection.close();
		}
		return message;
	}
	
	/**
	 * 下载课程学生导入模板
	 * @param path
	 * @param response
	 * @return
	 */
	private String downloadData(String path,HttpServletResponse response) {
		
		try{
            File templateFile = new File(path+"/classesuserTemplate.xls");
            // 取得输出流
            OutputStream os = response.getOutputStream();
            // 清空输出流
            response.reset();
            String fileName = "classesuserTemplate.xls";
            // 设定输出文件头
            response.setHeader("Content-disposition", "attachment; filename="+fileName+"");
            // 定义输出类型
            response.setContentType("application/msexcel");
            //读取excel文件
            jxl.Workbook workbook = jxl.Workbook.getWorkbook(templateFile);
            WorkbookSettings wbSettings = new WorkbookSettings();  
            wbSettings.setWriteAccess(null); 
            WritableWorkbook wbook = jxl.Workbook.createWorkbook(os,workbook,wbSettings);
            // 主体内容生成结束
            wbook.write(); // 写入文件
            wbook.close();
            os.close(); // 关闭流
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
	}	
	
	/**
	 * 上传课程学生
	 * @param request
	 * @param response
	 * @return
	 */
	private void exportClassesUserData(HttpServletRequest request,HttpServletResponse response) {
		
		//返回信息
		String message = "";
		ResultSet resultSet = null;
		DbConnection dbConnection = new DbConnection();
		try {
			request.setCharacterEncoding("utf-8");
			int classesId = Integer.valueOf(request.getParameter("id"));
			FileItemFactory factory = new DiskFileItemFactory();
	        ServletFileUpload upload = new ServletFileUpload(factory);
	        InputStream inputStream = null;
	        /**
	         * 读取上传文件
	         */
	        List items = upload.parseRequest(request);
	        Iterator iter = items.iterator();
	        while (iter.hasNext()) {
	            FileItem item = (FileItem) iter.next();
	            if (!item.isFormField()) {
	                inputStream = item.getInputStream();
	            }
	        }
	        Workbook wb = null;
     
            wb = new HSSFWorkbook(inputStream);
            //取第一个tab
            Sheet sheet = wb.getSheetAt(0);
            int totalRows = sheet.getPhysicalNumberOfRows();
            //遍历行
            for (int r = 1; r < totalRows; r++){
            	String selectSql = "";
                Row row = sheet.getRow(r);
                String uid = "";
                String name = "";
                String password = "";
                int studentId = 0;
                //学号
                if(!"".equals(String.valueOf(row.getCell(0))) && row.getCell(0) != null){
                    row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
                    uid = row.getCell(0).getStringCellValue().trim();
                    selectSql = "select * from user where uid='"+uid+"'";
                    resultSet = dbConnection.query(selectSql);
                    if(resultSet!=null && resultSet.next()){
                    	studentId = resultSet.getInt("id");
                    }
                }else{
                	message = message + "第" + r + "行的学号为空;";
                	continue;
                }
                //姓名
                if(!"".equals(String.valueOf(row.getCell(1))) && row.getCell(1) != null){
                    row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
                    name = row.getCell(1).getStringCellValue().trim();
                }else{
                	message = message + "第" + r + "行的姓名为空;";
                	continue;
                }
                //登录密码
                if(!"".equals(String.valueOf(row.getCell(2))) && row.getCell(2) != null){
                    row.getCell(2).setCellType(Cell.CELL_TYPE_STRING);
                    password = row.getCell(2).getStringCellValue().trim();
                }else{
                	message = message + "第" + r + "行的登录密码为空;";
                	continue;
                }
                if(studentId != 0){
	                //判断导入的学生是否选了该课程
	                selectSql = "select * from classestouser "
	                		+ " where classesId="+classesId
	                		+ " and studentId="+studentId;
		            resultSet = dbConnection.query(selectSql);
		            if(resultSet!=null && resultSet.next()){
		            	continue;
		            }else{
		            	String insertSql = "insert into classestouser (studentId,classesId) values (" + studentId +  "," + classesId+")";
			            dbConnection.update(insertSql);
		            }
                }else{
                	//新增学生信息
    	            String insertSql = "insert into user (uid,name,password,role) values ('" + uid +  "','" + name + "','" + password + "','student')";
    	            dbConnection.update(insertSql);
    	            selectSql = "select * from user where uid='"+uid+"'";
                    resultSet = dbConnection.query(selectSql);
                    if(resultSet!=null && resultSet.next()){
                    	studentId = resultSet.getInt("id");
                    }
    	            insertSql = "insert into classestouser (studentId,classesId) values (" + studentId +  "," + classesId+")";
		            dbConnection.update(insertSql);
                }
            }
            if(message==""){
            	//无报错信息
            	message = "上传成功,请重新搜索";
            }
			Map<String,Object> dataMap = new HashMap<String, Object>();
			dataMap.put("message",message);
			response.setContentType("text/html;charset=UTF-8");
			JSONObject jsonObject = JSONObject.fromObject(dataMap);
			response.getWriter().print(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
        	dbConnection.close();
        }
	}
	
}
