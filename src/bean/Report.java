package bean;

//ʵ�鱨����
public class Report {
	
	private int id;
	private Project project;//ʵ����Ŀ
	private String url;//�����ַ
	private User user;//ѧ����Ϣ
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
}
