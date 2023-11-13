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
				//�����γ�
				String name = request.getParameter("name");
				String grade = request.getParameter("grade");
				int teacherId = Integer.valueOf(request.getParameter("teacherId"));
				String message = this.saveClasses(name,grade,teacherId);
				 //������Ӧ���ļ���(���ƣ��γ���+�༶)
				name = name + grade;
		        String path = "D:/baobiao/fileUploadSystem/WebContent/report/"+ name;
		        File file = new File(path);
		        if(!file.exists()){
		        	file.mkdir();
		        }
				request.setAttribute("msg", message);
				request.getRequestDispatcher("/system/addClasses.jsp").forward(request,response);		
			}else if(type.equals("sel")){
				//�鿴�γ��б�
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
				//�鿴�γ̶�Ӧ��ѧ���б�
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
				//ɾ���γ��е�ѧ��
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
				//ɾ���γ�
				int id = Integer.valueOf(request.getParameter("id"));
				int num = this.getClassesByUserNum(id);
				String message = "";
				if(num == 0){
					message = this.deleteClasses(id);
				}else{
					message = "�ÿγ̻���ѧ�����ݴ��ڣ��������ѧ�����ݣ���ɾ����";
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
				//���ؿγ�ѧ���ϴ�ģ��
				 String path = request.getSession().getServletContext().getRealPath("/template");
				 this.downloadData(path,response);
			}else if(type.equals("export")){
				//�ϴ��γ�ѧ��
				this.exportClassesUserData(request,response);
			}else if(type.equals("selClasses")){
				//��ȡ���пγ���Ϣ
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
				//ɾ���γ�
				int id = Integer.valueOf(request.getParameter("id"));
				int num = this.getClassesByProjectNum(id);
				String message = "";
				if(num == 0){
					message = this.deleteClasses(id);
				}else{
					message = "�ÿγ̻���ʵ����Ŀ���ڣ��������ʵ����Ŀ����ɾ����";
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
	 * �����γ���Ϣ
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
			message = "¼��ɹ�";
		}catch(Exception e){
			e.printStackTrace();
			message = "¼��ʧ�ܣ��������ύ";
		}finally{
			dbConnection.close();
		}
		return message;
	}
	
	
	/**
	 * ��ȡ�γ�ѧ����Ϣ
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
			
			//ÿҳ��ʾ������
			int pageSize = 0;
			if(totalRecord<1){
				//ÿҳ��ʾ������
				pageSize = totalRecord;
			}else{
				//ÿҳ��ʾ������
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
	 * ��ȡ�γ���Ϣ
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
			
			//ÿҳ��ʾ������
			int pageSize = 0;
			if(totalRecord<1){
				//ÿҳ��ʾ������
				pageSize = totalRecord;
			}else{
				//ÿҳ��ʾ������
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
	 * ��ȡ�γ̶�Ӧ��ѧ����Ϣ
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
			
			//ÿҳ��ʾ������
			int pageSize = 0;
			if(totalRecord<1){
				//ÿҳ��ʾ������
				pageSize = totalRecord;
			}else{
				//ÿҳ��ʾ������
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
	 * ɾ���γ̶�Ӧ��ѧ����Ϣ
	 * @param classesByUserId
	 * @return
	 */
	private String delClassesByUser(int classesByUserId) {
		String message = "";
		DbConnection dbConnection = new DbConnection();
		try{
			String updateSql = "delete from classestoUser where id="+classesByUserId;
			dbConnection.update(updateSql);		
			message = "ɾ���ɹ�";
		}catch(Exception e){
			e.printStackTrace();
			message = "ɾ��ʧ��";
		}finally{
			dbConnection.close();
		}
		return message;
	}

	
	
	/**
	 * ��ȡ�γ̵�ѧ������
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
	 * ��ȡ�γ̵�ʵ����Ŀ����
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
	 * ɾ���γ���Ϣ
	 * @param classesId
	 * @return
	 */
	private String deleteClasses(int classesId) {
		String message = "";
		DbConnection dbConnection = new DbConnection();
		try{
			String updateSql = "delete from classes where id="+classesId;
			dbConnection.update(updateSql);		
			message = "ɾ���ɹ�";
		}catch(Exception e){
			e.printStackTrace();
			message = "ɾ��ʧ��";
		}finally{
			dbConnection.close();
		}
		return message;
	}
	
	/**
	 * ���ؿγ�ѧ������ģ��
	 * @param path
	 * @param response
	 * @return
	 */
	private String downloadData(String path,HttpServletResponse response) {
		
		try{
            File templateFile = new File(path+"/classesuserTemplate.xls");
            // ȡ�������
            OutputStream os = response.getOutputStream();
            // ��������
            response.reset();
            String fileName = "classesuserTemplate.xls";
            // �趨����ļ�ͷ
            response.setHeader("Content-disposition", "attachment; filename="+fileName+"");
            // �����������
            response.setContentType("application/msexcel");
            //��ȡexcel�ļ�
            jxl.Workbook workbook = jxl.Workbook.getWorkbook(templateFile);
            WorkbookSettings wbSettings = new WorkbookSettings();  
            wbSettings.setWriteAccess(null); 
            WritableWorkbook wbook = jxl.Workbook.createWorkbook(os,workbook,wbSettings);
            // �����������ɽ���
            wbook.write(); // д���ļ�
            wbook.close();
            os.close(); // �ر���
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
	}	
	
	/**
	 * �ϴ��γ�ѧ��
	 * @param request
	 * @param response
	 * @return
	 */
	private void exportClassesUserData(HttpServletRequest request,HttpServletResponse response) {
		
		//������Ϣ
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
	         * ��ȡ�ϴ��ļ�
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
            //ȡ��һ��tab
            Sheet sheet = wb.getSheetAt(0);
            int totalRows = sheet.getPhysicalNumberOfRows();
            //������
            for (int r = 1; r < totalRows; r++){
            	String selectSql = "";
                Row row = sheet.getRow(r);
                String uid = "";
                String name = "";
                String password = "";
                int studentId = 0;
                //ѧ��
                if(!"".equals(String.valueOf(row.getCell(0))) && row.getCell(0) != null){
                    row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
                    uid = row.getCell(0).getStringCellValue().trim();
                    selectSql = "select * from user where uid='"+uid+"'";
                    resultSet = dbConnection.query(selectSql);
                    if(resultSet!=null && resultSet.next()){
                    	studentId = resultSet.getInt("id");
                    }
                }else{
                	message = message + "��" + r + "�е�ѧ��Ϊ��;";
                	continue;
                }
                //����
                if(!"".equals(String.valueOf(row.getCell(1))) && row.getCell(1) != null){
                    row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
                    name = row.getCell(1).getStringCellValue().trim();
                }else{
                	message = message + "��" + r + "�е�����Ϊ��;";
                	continue;
                }
                //��¼����
                if(!"".equals(String.valueOf(row.getCell(2))) && row.getCell(2) != null){
                    row.getCell(2).setCellType(Cell.CELL_TYPE_STRING);
                    password = row.getCell(2).getStringCellValue().trim();
                }else{
                	message = message + "��" + r + "�еĵ�¼����Ϊ��;";
                	continue;
                }
                if(studentId != 0){
	                //�жϵ����ѧ���Ƿ�ѡ�˸ÿγ�
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
                	//����ѧ����Ϣ
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
            	//�ޱ�����Ϣ
            	message = "�ϴ��ɹ�,����������";
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
