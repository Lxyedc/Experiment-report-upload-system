package bean;

//ʵ����Ŀ��
public class Project {
	
	private int id;
	private Classes classes;//�γ�
	private String name;//��Ŀ����
	private String time;//�ϴ�����ʱ��
	private String stopFlag;//���ڱ�ʶ
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Classes getClasses() {
		return classes;
	}
	public void setClasses(Classes classes) {
		this.classes = classes;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getStopFlag() {
		return stopFlag;
	}
	public void setStopFlag(String stopFlag) {
		this.stopFlag = stopFlag;
	}
	
}
