package manager.bean;

import java.io.Serializable;

public class ChatDTO implements Serializable{
	private static final long serialVersionUID = 8668425692093764820L;
	
	private String Command;
	private String id;
	private String msg;
	private String adminId;
	
	
	public String getAdminId() {
		return adminId;
	}
	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}
	public String getCommand() {
		return Command;
	}
	public void setCommand(String command) {
		Command = command;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	@Override
	public String toString() {
		return "ChatDTO";
	}
	
}
