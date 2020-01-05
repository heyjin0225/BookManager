package manager.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.*;

public class RentDTO implements Serializable {
	private static final long serialVersionUID = 4L;
	
	private int seq;
	private String id;
	private String bookName;
	private String name;
	private String tel;
	private String email;
	private Date date;
	private String cover;
	private String command;
	private ArrayList<RentDTO> list;
	private int counting;
	
	public int getCounting() {
		return counting;
	}
	public void setCounting(int counting) {
		this.counting = counting;
	}
	public int getSeq() {
		return seq;
	}
	public String getBookName() {
		return bookName;
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
	public String getCover() {
		return cover;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
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
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public ArrayList<RentDTO> getList() {
		return list;
	}
	public void setList(ArrayList<RentDTO> list) {
		this.list = list;
	}
	@Override
	public String toString() {
		return "RentDTO";
	}
	
	
}
