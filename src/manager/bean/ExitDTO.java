package manager.bean;

import java.io.Serializable;

public class ExitDTO implements Serializable {
	private static final long serialVersionUID = 2L;
	
	private String exit;
	private String id;
	private String adminId;

	public String getAdminId() {
		return adminId;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getExit() {
		return exit;
	}

	public void setExit(String exit) {
		this.exit = exit;
	}

	@Override
	public String toString() {
		return "ExitDTO";
	}
	
}
