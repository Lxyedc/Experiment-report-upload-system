package servlet;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;

import bean.Classes;
import bean.Project;
import bean.PageBean;
import bean.Report;
import bean.User;
import dao.DbConnection;

@SuppressWarnings("serial")
public class reportManagerServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String type = request.getParameter("type");
		if(type!=null){
			 if(type.equals("selProject")){
				//查看实验项目列表-学生
				int pageNum = Integer.valueOf(request.getParameter("pageNum"));
				int id = Integer.valueOf(request.getParameter("id"));
				String searchName = "all";
				if(request.getParameter("searchName")!=null){
					searchName = request.getParameter("searchName");
					request.setAttribute("searchName", searchName);
				}
				PageBean<Project> projectList = this.getProjectByUserList(id,searchName,pageNum);
				request.setAttribute("projectList", projectList);
				request.getRequestDispatcher("/system/projectByUserList.jsp").forward(request,response);
			}else if(type.equals("selAllProject")){
				//查看实验项目列表-教师
				int pageNum = Integer.valueOf(request.getParameter("pageNum"));
				int id = Integer.valueOf(request.getParameter("id"));
				String searchName = "all";
				if(request.getParameter("searchName")!=null){
					searchName = request.getParameter("searchName");
					request.setAttribute("searchName", searchName);
				}
				PageBean<Project> projectList = this.getProjectByTeacherList(id,searchName,pageNum);
				request.setAttribute("projectList", projectList);
				request.getRequestDispatcher("/system/projectByTeacherList.jsp").forward(request,response);	
			}else if(type.equals("selReport")){
				//查看实验报告列表
				int pageNum = Integer.valueOf(request.getParameter("pageNum"));
				int projectId = Integer.valueOf(request.getParameter("id"));
				String searchName = "all";
				if(request.getParameter("searchName")!=null){
					searchName = request.getParameter("searchName");
					request.setAttribute("searchName", searchName);
				}
				PageBean<Report> reportList = this.getReportByUserList(projectId,searchName,pageNum);
				request.setAttribute("reportList", reportList);
				request.getRequestDispatcher("/system/reportList.jsp").forward(request,response);	
			}else if(type.equals("selSigle")){
				//查看个人实验报告列表
				int pageNum = Integer.valueOf(request.getParameter("pageNum"));
				int studentId = Integer.valueOf(request.getParameter("id"));
				String searchName = "all";
				if(request.getParameter("searchName")!=null){
					searchName = request.getParameter("searchName");
					request.setAttribute("searchName", searchName);
				}
				PageBean<Report> reportList = this.getReportBySigleList(studentId,searchName,pageNum);
				request.setAttribute("reportList", reportList);
				request.getRequestDispatcher("/system/reportSigleList.jsp").forward(request,response);	
			}else if(type.equals("downloadReport")){
				//下载对应的实验报告
				String url = new String(request.getParameter("url").getBytes("iso-8859-1"),"utf-8");
				String path = "D:/baobiao/fileUploadSystem/WebContent/report";
			    this.downloadReport(path,url,response);
			}else if(type.equals("uploadReport")){
				//上传对应的实验报告
				int projectId = Integer.valueOf(request.getParameter("projectId"));
				int studentId = Integer.valueOf(request.getParameter("id"));
				Classes classes = this.getClassesByProjectId(projectId);
				Project project = this.getProjectById(projectId);
				if(classes!=null && project!=null){
					String name= classes.getName() + classes.getGrade();
					User user = this.getStudentById(studentId);
					if(user!=null){
						String fileName = user.getUid()+user.getName()+project.getName();
						this.uploadReportData(projectId,studentId,fileName,name,request,response);
					}
				}
			}
		}
	}
	
	/**
	 * 获取课程信息
	 * @param projectId
	 * @return
	 */
	private Classes getClassesByProjectId(int projectId){
		Classes classes = null;
		DbConnection dbConnection = new DbConnection();
		try{
			String sql = "select c.name,c.grade from classes as c "
					+ " left join project as p on p.classesId=c.id"
					+ " where p.id="+projectId;
			ResultSet resultSet =dbConnection.query(sql);
			if(resultSet!=null){
				while(resultSet.next()){
					classes = new Classes();
					classes.setName(resultSet.getString("name"));
					classes.setGrade(resultSet.getString("grade"));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dbConnection.close();
		}
		return classes;
	}
	

	/**
	 * 获取项目信息
	 * @param projectId
	 * @return
	 */
	private Project getProjectById(int projectId){
		Project project = null;
		DbConnection dbConnection = new DbConnection();
		try{
			String sql = "select * from project where id="+projectId;
			ResultSet resultSet =dbConnection.query(sql);
			if(resultSet!=null){
				while(resultSet.next()){
					project = new Project();
					project.setName(resultSet.getString("name"));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dbConnection.close();
		}
		return project;
	}

	/**
	 * 获取学生信息
	 * @param studentId
	 * @return
	 */
	private User getStudentById(int studentId){
		User user = null;
		DbConnection dbConnection = new DbConnection();
		try{
			String sql = "select * from user where id="+studentId;
			ResultSet resultSet =dbConnection.query(sql);
			if(resultSet!=null){
				while(resultSet.next()){
					user = new User();
					user.setName(resultSet.getString("name"));
					user.setUid(resultSet.getString("uid"));
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
	 * 获取学生对应的实验项目
	 * @param studentId
	 * @param searchName
	 * @param pageNum
	 * @return
	 */
	private PageBean<Project> getProjectByUserList(int studentId,String searchName,int pageNum) {
		
		DbConnection dbConnection = new DbConnection();
		PageBean<Project> page  = null;
		try {
			String selectsql = "select p.id,p.name,c.name as cname,c.grade,p.time"
							+ " from project as p"
							+ " left join classes as c on c.id=p.classesId"
							+ "	left join classestouser as ctu on c.id=ctu.classesId"
							+ " left join user as u on ctu.studentId=u.id"
							+ " and (p.name like '%"+searchName+"%' or '"+searchName+"'='all') "
							+ " where u.id="+studentId;
			ResultSet resultSet = dbConnection.query(selectsql);
			List<Project> projectList = new ArrayList<Project>();
			if(resultSet!=null){
				while(resultSet.next()){
					Project project = new Project();
					project.setId(resultSet.getInt("id"));
					project.setName(resultSet.getString("name"));
					String time = resultSet.getString("time");
					String stopFlag = "false";
					if(this.dateCompare(time)){
						stopFlag = "true";
					}
					project.setStopFlag(stopFlag);
					project.setTime(time);
					Classes classes = new Classes();
					classes.setName(resultSet.getString("cname"));
					classes.setGrade(resultSet.getString("grade"));
					project.setClasses(classes);
					projectList.add(project);
				}
			}
			int totalRecord = projectList.size();
			
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
				page  = new PageBean<Project>(pageNum,pageSize,totalRecord);
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
				page.setList(projectList.subList(startIndex, endIndex));
			}else{
				page = new PageBean<Project>(pageNum,pageSize,0);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbConnection.close();
		}
		return page;
	}
	

	/**
	 * 获取教师对应的实验项目
	 * @param teacherId
	 * @param searchName
	 * @param pageNum
	 * @return
	 */
	private PageBean<Project> getProjectByTeacherList(int teacherId,String searchName,int pageNum) {
		
		DbConnection dbConnection = new DbConnection();
		PageBean<Project> page  = null;
		try {
			String selectsql = "select p.id,p.name,c.name as cname,c.grade,p.time"
							+ " from project as p"
							+ " left join classes as c on c.id=p.classesId"
							+ " left join user as u on c.teacherId=u.id"
							+ " and (p.name like '%"+searchName+"%' or '"+searchName+"'='all') "
							+ " where u.id="+teacherId;
			ResultSet resultSet = dbConnection.query(selectsql);
			List<Project> projectList = new ArrayList<Project>();
			if(resultSet!=null){
				while(resultSet.next()){
					Project project = new Project();
					project.setId(resultSet.getInt("id"));
					project.setName(resultSet.getString("name"));
					String time = resultSet.getString("time");
					String stopFlag = "false";
					if(this.dateCompare(time)){
						stopFlag = "true";
					}
					project.setStopFlag(stopFlag);
					project.setTime(time);
					Classes classes = new Classes();
					classes.setName(resultSet.getString("cname"));
					classes.setGrade(resultSet.getString("grade"));
					project.setClasses(classes);
					projectList.add(project);
				}
			}
			int totalRecord = projectList.size();
			
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
				page  = new PageBean<Project>(pageNum,pageSize,totalRecord);
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
				page.setList(projectList.subList(startIndex, endIndex));
			}else{
				page = new PageBean<Project>(pageNum,pageSize,0);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbConnection.close();
		}
		return page;
	}
	
	/**
	 * 获取学生对应的实验报告
	 * @param studentId
	 * @param searchName
	 * @param pageNum
	 * @return
	 */
	private PageBean<Report> getReportBySigleList(int studentId,String searchName,int pageNum) {
		
		DbConnection dbConnection = new DbConnection();
		PageBean<Report> page  = null;
		try {
			String selectsql = "select p.name,u.name as uname,u.uid,r.url"
							+ " from report as r"
							+ " left join project as p on r.projectId=p.id"
							+ " left join user as u on r.studentId=u.id"
							+ " and (p.name like '%"+searchName+"%' or '"+searchName+"'='all') "
							+ " where u.id="+studentId;
			ResultSet resultSet = dbConnection.query(selectsql);
			List<Report> reportList = new ArrayList<Report>();
			if(resultSet!=null){
				while(resultSet.next()){
					Report report = new Report();
					report.setUrl(resultSet.getString("url"));
					User student = new User();
					student.setName(resultSet.getString("uname"));
					student.setUid(resultSet.getString("uid"));
					report.setUser(student);
					Project project = new Project();
					project.setName(resultSet.getString("name"));
					report.setProject(project);
					reportList.add(report);
				}
			}
			int totalRecord = reportList.size();
			
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
				page = new PageBean<Report>(pageNum,pageSize,totalRecord);
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
				page.setList(reportList.subList(startIndex, endIndex));
			}else{
				page = new PageBean<Report>(pageNum,pageSize,0);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbConnection.close();
		}
		return page;
	}
	
	/**
	 * 获取实验项目对应的报告
	 * @param projectId
	 * @param searchName
	 * @param pageNum
	 * @return
	 */
	private PageBean<Report> getReportByUserList(int projectId,String searchName,int pageNum) {
		
		DbConnection dbConnection = new DbConnection();
		PageBean<Report> page  = null;
		try {
			String selectsql = "select p.name,u.name as uname,u.uid,r.url"
							+ " from report as r"
							+ " left join project as p on r.projectId=p.id"
							+ " left join user as u on r.studentId=u.id"
							+ " and (u.name like '%"+searchName+"%' "
							+ " or u.uid like '%"+searchName+"%' or '"+searchName+"'='all') "
							+ " where p.id="+projectId;
			ResultSet resultSet = dbConnection.query(selectsql);
			List<Report> reportList = new ArrayList<Report>();
			if(resultSet!=null){
				while(resultSet.next()){
					Report report = new Report();
					report.setUrl(resultSet.getString("url"));
					User student = new User();
					student.setName(resultSet.getString("uname"));
					student.setUid(resultSet.getString("uid"));
					report.setUser(student);
					Project project = new Project();
					project.setName(resultSet.getString("name"));
					report.setProject(project);
					reportList.add(report);
				}
			}
			int totalRecord = reportList.size();
			
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
				page = new PageBean<Report>(pageNum,pageSize,totalRecord);
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
				page.setList(reportList.subList(startIndex, endIndex));
			}else{
				page = new PageBean<Report>(pageNum,pageSize,0);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbConnection.close();
		}
		return page;
	}
	
	/**
	 * 下载实验报告
	 * @param path
	 * @param url
	 */
	private void downloadReport(String path,String url,HttpServletResponse response){
		OutputStream out = null;
		try{
			File file = new File(path+url);
		    response.reset(); 
			String fileName = url.substring(url.lastIndexOf("/")+1);
			PDDocument document = PDDocument.load(file);
			PDDocumentInformation info = document.getDocumentInformation();
			info.setTitle(fileName);
			fileName = URLEncoder.encode(fileName,"UTF-8");
			response.setContentType("application/pdf;charset=UTF-8;");
			out = response.getOutputStream();
			response.setHeader("Content-Disposition", "inline;fileName=" + fileName);
			document.setDocumentInformation(info);
			document.save(out);
			document.close();
			out.flush();
			out.close();
	    }catch(Exception e){
	        e.printStackTrace();
	    }finally{
	    	if(out != null){
	    		try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
	    }
	}
	
	/**
	 * 上传项目报告
	 * @param projectId
	 * @param studnetId
	 * @param fileName
	 * @param name
	 * @param request
	 * @param response
	 */
	private void uploadReportData(int projectId,int studnetId,String fileName,String name,HttpServletRequest request,HttpServletResponse response) {
		
		//返回信息
		String message = "";
		DbConnection dbConnection = new DbConnection();
		try {
			request.setCharacterEncoding("utf-8");
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
	       
	        String path = "D:/baobiao/fileUploadSystem/WebContent/report/"+ name + "/";
	        //上传实验报告文件名：学号+姓名 +实验项目名.pdf
	        path = path + fileName + ".pdf";
	        File reportFile = new File(path);
	        OutputStream os = new FileOutputStream(reportFile);
	        int len = 0;
	        byte[] b = new byte[1024];
	        while((len = inputStream.read(b))!=-1){
	        	os.write(b,0,len);
	        }
	        inputStream.close();
	        os.close();            
            String url = "/" + name + "/" + fileName + ".pdf";
            //判断是否已经上传，存在则不创建记录
            String sqlString = "select * from report where studentId="+studnetId + " and projectId="+projectId;
            ResultSet resultSet = dbConnection.query(sqlString);
            if(resultSet == null && !resultSet.next()){
            	sqlString = "insert into report(projectId,url,studentId) "
       				 + "values("+projectId+",'"+ url +"',"+studnetId+")";
            	dbConnection.update(sqlString);
            }
            message = "报告上传成功";
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
	
	/**
	 * 比较当前日期和指定日期 return boolean 如果当前日期在指定日期之后返回true否则返回flase
	 * @param str
	 * @return
	 */
    public boolean dateCompare(String str) {
        boolean bea = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String isDate = sdf.format(new Date());
        Date date1;
        Date date0;
        try {
            date1 = sdf.parse(str);
            date0 = sdf.parse(isDate);
            if (date0.after(date1)) {
                bea = true;
            }
        } catch (Exception e) {
            bea = false;
        }
        return bea;
    }

}
