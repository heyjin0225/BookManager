package manager.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.print.attribute.standard.ReferenceUriSchemesSupported;

import manager.bean.SeatDTO;

public class SeatDAO {
	private static SeatDAO instance;

	public static SeatDAO getInstance() {
		if (instance == null) {
			synchronized (SeatDAO.class) {
				instance = new SeatDAO();
			}
		}
		return instance;
	}
	// ----------------------------------------------------------------- ½Ì±ÛÅæ

	private String driver = "oracle.jdbc.driver.OracleDriver";
	private String url = "jdbc:oracle:thin:@localhost:1521:xe";
	private String username = "dbdb";
	private String password = "itbank";

	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;

	public SeatDAO() {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void getConnection() {
		try {
			conn = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// -----------------------------------------------------------------µå¶óÀÌ¹ö ·Îµù
	
	
	public void newGuest(SeatDTO dto) {
		getConnection();
		String sql = "insert into seat values(?, ?, ?)";
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, dto.getSeatNumber());
			pstmt.setString(2, dto.getSeatName());
			pstmt.setString(3, dto.getId());
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			resourceCloser();
		}
	}
	
	public SeatDTO searchId(SeatDTO dto) {
		getConnection();
		String sql = "select * from seat where id = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getId());
			rs = pstmt.executeQuery();
			if(rs.next()) {
				dto.setHere(true);
				dto.setSeatNumber(rs.getInt("seatnum"));
				dto.setSeatName(rs.getString("seatname"));
				dto.setId(rs.getString("id"));
			}else {
				dto.setHere(false);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			resourceCloser();
		}
		return dto;
	}
	
	public void deleteGuest(SeatDTO dto) {
		getConnection();
		
		String sql = "delete seat where id = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			resourceCloser();
		}
	}
	
	public void resourceCloser() {
		try {
			if(rs!=null)rs.close();
			if(pstmt!=null) pstmt.close();
			if(conn!=null) conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
