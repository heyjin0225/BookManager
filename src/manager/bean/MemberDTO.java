package manager.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class MemberDTO implements Serializable {
	private static final long serialVersionUID = 3L;
	
	private int seq;
	private String id;
	private String pw;
	private String name;
	private String tel;
	private String email;
	private String command;
	private boolean check;//ȸ������ ���� ����
	private ArrayList<MemberDTO> list;//�����ü�� ���������� ���
	
	private String permission;	//���������� ���������
	
	public String getPermission() {
		return permission;
	}
	public void setPermission(String permission) {
		this.permission = permission;
	}
	public int getSeq() {
		return seq;
	}
	public String getId() {
		return id;
	}
	public String getPw() {
		return pw;
	}
	public String getName() {
		return name;
	}
	public String getTel() {
		return tel;
	}
	public String getEmail() {
		return email;
	}
	public String getCommand() {
		return command;
	}
	public boolean isCheck() {
		return check;
	}
	public ArrayList<MemberDTO> getList() {
		return list;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setPw(String pw) {
		this.pw = pw;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public void setCheck(boolean check) {
		this.check = check;
	}
	public void setList(ArrayList<MemberDTO> list) {
		this.list = list;
	}
	
	@Override
	public String toString() {
		return "MemberDTO";
	}
	
}
