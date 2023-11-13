package servlet;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import com.sun.org.apache.bcel.internal.generic.NEW;

import bean.Classes;
import bean.PageBean;
import bean.Project;
import bean.User;
import dao.DbConnection;

@SuppressWarnings("serial")
public class projectManagerServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String type = request.getParameter("type");
		if(type.equals("info")){
			//查看课程对应的实验项目列表
			int classesId = Integer.valueOf(request.getParameter("id"));
			int pageNum = Integer.valueOf(request.getParameter("pageNum"));
			String searchName = "all";
			if(request.getParameter("searchName")!=null){
				searchName = request.getParameter("searchName");
				request.setAttribute("searchName", searchName);
			}
			PageBean<Project> projectList = this.getClassesProjectList(classesId,searchName,pageNum);
			request.setAttribute("projectList", projectList);
			request.getRequestDispatcher("/system/projectList.jsp").forward(request,response);
		}else if(type.equals("download")){
			//下载实验项目上传模板
			 String path = request.getSession().getServletContext().getRealPath("/template");
			 this.downloadData(path,response);
		}else if(type.equals("export")){
			//上传实验项目
			this.exportClassesProjectData(request,response);
		}else if(type.equals("del")){
			//删除实验项目
			int id = Integer.valueOf(request.getParameter("id"));
			int num = this.getProjectByReportNum(id);
			String message = "";
			if(num == 0){
				message = this.deleteProject(id);
			}else{
				message = "该实验项目还有报告存在，请先清空报告，再删除！";
			}
			int classesId = Integer.valueOf(request.getParameter("classesId"));
			int pageNum = Integer.valueOf(request.getParameter("pageNum"));
			String searchName = "all";
			if(request.getParameter("searchName")!=null){
				searchName = request.getParameter("searchName");
				request.setAttribute("searchName", searchName);
			}
			PageBean<Project> projectList = this.getClassesProjectList(classesId,searchName,pageNum);
			request.setAttribute("projectList", projectList);
			request.setAttribute("msg", message);
			request.getRequestDispatcher("/system/projectList.jsp").forward(request,response);
		}	
	}
	
	
	/**
	 * 获取课程对应的实验项目信息
	 * @param classesId
	 * @param searchName
	 * @param pageNum
	 * @return
	 */
	private PageBean<Project> getClassesProjectList(int classesId,String searchName,int pageNum) {
		
		DbConnection dbConnection = new DbConnection();
		PageBean<Project> page  = null;
		try {
			String selectsql = "select p.id,p.name,c.name as cname,c.grade,p.time"
							+ " from project as p"
							+ " left join classes as c on c.id=p.classesId"
							+ " and (p.name like '%"+searchName+"%' "
							+ " or c.name like '%"+searchName+"%' "
							+ " or c.grade like '%"+searchName+"%' "
							+ " or '"+searchName+"'='all') "
							+ " where c.id="+classesId;
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
	 * 下载实验项目导入模板
	 * @param path
	 * @param response
	 * @return
	 */
	private String downloadData(String path,HttpServletResponse response) {
		
		try{
            File templateFile = new File(path+"/projectTemplate.xls");
            // 取得输出流
            OutputStream os = response.getOutputStream();
            // 清空输出流
            response.reset();
            String fileName = "projectTemplate.xls";
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
	 * 上传实验项目
	 * @param request
	 * @param response
	 * @return
	 */
	private void exportClassesProjectData(HttpServletRequest request,HttpServletResponse response) {
		
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
                String name = null;
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = sf.format(new Date());
                //实验项目名称
                if(!"".equals(String.valueOf(row.getCell(0))) && row.getCell(0) != null){
                    row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
                    name = row.getCell(0).getStringCellValue().trim();
                    //判断导入的实验项目是否已经存在该课程
                    selectSql = "select * from project where classesId="+classesId+" and name='"+name+"'";
                    resultSet = dbConnection.query(selectSql);
                    if(resultSet!=null && resultSet.next()){
                    	continue;
                    }
                }else{
                	message = message + "第" + r + "行的实验项目为空;";
                	continue;
                }
                //截止时间
                if(!"".equals(String.valueOf(row.getCell(1))) && row.getCell(1) != null){
                    row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
                    time = row.getCell(1).getStringCellValue().trim();
                }else{
                	message = message + "第" + r + "行的截止时间为空;";
                	continue;
                }
	            String insertSql = "insert into project (name,classesId,time) values ('" + name +  "'," + classesId+",'"+time+"')";
		        dbConnection.update(insertSql);
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
			dbConnection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }	
	}
	
	/**
	 * 获取实验项目对应的报告数量
	 * @param projectId
	 * @return
	 */
	private int getProjectByReportNum(int projectId)
   {
		int num = 0;
		DbConnection dbConnection = new DbConnection();
		try{
			String sql = "select count(r.id) as num from report as r where r.projectId="+projectId;
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
	 * 删除实验项目
	 * @param projectId
	 * @return
	 */
	private String deleteProject(int projectId) {
		String message = "";
		DbConnection dbConnection = new DbConnection();
		try{
			String updateSql = "delete from project where id="+projectId;
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
