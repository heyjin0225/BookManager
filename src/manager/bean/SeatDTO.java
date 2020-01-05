package manager.bean;
import java.io.Serializable;
import java.util.ArrayList;

public class SeatDTO implements Serializable{
	private static final long serialVersionUID = 5L;
	
	private int seatNumber;
	private String seatName;
	private String id;
	private boolean isHere;
	private String command;
	private ArrayList<SeatDTO> reserved;
	
	
	public String getSeatName() {
		return seatName;
	}
	public void setSeatName(String seatName) {
		this.seatName = seatName;
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
	public ArrayList<SeatDTO> getReserved() {
		return reserved;
	}
	public void setReserved(ArrayList<SeatDTO> reserved) {
		this.reserved = reserved;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public int getSeatNumber() {
		return seatNumber;
	}
	public boolean isHere() {
		return isHere;
	}
	public void setHere(boolean isHere) {
		this.isHere = isHere;
	}
	public void setSeatNumber(int seatNumber) {
		this.seatNumber = seatNumber;
	}
	
	@Override
	public String toString() {
		
		return "SeatDTO";
	}
}
