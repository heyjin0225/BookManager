package manager.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class BookDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private int seq;
	private String bookName;
	private String writer;
	private String publisher;
	private String genre;
	private int rentState;//대여가능 0 대여불가능 1
	private String cover;
	private String command;
	private ArrayList<BookDTO> list;
	
	
	public int getSeq() {
		return seq;
	}
	public String getBookName() {
		return bookName;
	}
	public String getWriter() {
		return writer;
	}
	public String getPublisher() {
		return publisher;
	}
	public String getGenre() {
		return genre;
	}
	public int getRentState() {
		return rentState;
	}
	public String getCover() {
		return cover;
	}
	public String getCommand() {
		return command;
	}
	public ArrayList<BookDTO> getList() {
		return list;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	public void setWriter(String writer) {
		this.writer = writer;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public void setRentState(int rentState) {
		this.rentState = rentState;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public void setList(ArrayList<BookDTO> list) {
		this.list = list;
	}
	@Override
	public String toString() {
		return "BookDTO";
	}
	
	
}
