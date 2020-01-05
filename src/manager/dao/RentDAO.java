package manager.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import manager.bean.RentDTO;

public class RentDAO {
	private static RentDAO instance;

	public static RentDAO getInstance() {
		if (instance == null) {
			synchronized (RentDAO.class) {
				instance = new RentDAO();
			}
		}
		return instance;
	}
	// ----------------------------------------------------------------- 싱글톤

	private String driver = "oracle.jdbc.driver.OracleDriver";
	private String url = "jdbc:oracle:thin:@localhost:1521:xe";
	private String username = "dbdb";
	private String password = "itbank";

	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;

	public RentDAO() {
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
	// -----------------------------------------------------------------드라이버 로딩
	
	
	
	public void addRent(RentDTO dto) {
		getConnection();
		String sql = "insert into rent values(?, ?, ?, ?, ?, ?, sysdate)";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, dto.getSeq());
			pstmt.setString(2, dto.getId());
			pstmt.setString(3, dto.getBookName());
			pstmt.setString(4, dto.getName());
			pstmt.setString(5, dto.getTel());
			pstmt.setString(6, dto.getEmail());
			pstmt.executeUpdate();
			
			sql = "update book set rentstate = 1 where bookname = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getBookName());
			pstmt.executeUpdate();
			System.out.println("책랜트 db완료");
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			resourceCloser();
		}
	}
	
	public int counthing(RentDTO dto) {
		getConnection();
		String sql = "select id from rent where id = ?";
		int cnt = 0;
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getId());
			rs = pstmt.executeQuery();
			while(rs.next()) {
				cnt++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			resourceCloser();
		}
		return cnt;
	}
	
	public ArrayList<RentDTO> getRentList() {
		ArrayList<RentDTO> list = new ArrayList<RentDTO>();
		
		getConnection();
		String sql = "select * from rent";
		
		try {
			pstmt= conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				int seq = rs.getInt("seq");
				String id = rs.getString("id");
				String bookName = rs.getString("bookname");
				String name = rs.getString("name");
				String tel = rs.getString("tel");
				String email =rs.getString("email");
				Date date = rs.getDate("rent_date");
				
				RentDTO dto = new RentDTO();
				dto.setSeq(seq);
				dto.setId(id);
				dto.setBookName(bookName);
				dto.setName(name);
				dto.setTel(tel);
				dto.setEmail(email);
				dto.setDate(date);
				
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			resourceCloser();
		}
		return list;
	}
	
	public void deleteRent(RentDTO dto) {
		getConnection();//book 수정
		String sql = "update book set rentstate = 0 where seq = ?"; 
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, dto.getSeq());
			pstmt.executeUpdate();
			
			sql = "delete rent where seq = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, dto.getSeq());
			pstmt.executeUpdate();
			
			System.out.println("책랜트 db완료");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			resourceCloser();
		}
		
	}
	
	//UserRentList 개인이 빌린 책 리스트 반환
	public ArrayList<RentDTO> userRentList(RentDTO resultDTO) {
		getConnection();
		ArrayList<RentDTO> list = new ArrayList<>();
		String sql = "select * from rent where id = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, resultDTO.getId());
			rs = pstmt.executeQuery();
			while(rs.next()) {
				RentDTO dto = new RentDTO();
				dto.setSeq(rs.getInt("seq"));
				dto.setId(rs.getString("id"));
				dto.setBookName(rs.getString("bookName"));
				dto.setName(rs.getString("name"));
				dto.setTel(rs.getString("tel"));
				dto.setEmail(rs.getString("email"));
				dto.setDate(rs.getDate("rent_date"));
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			resourceCloser();
		}
		return list;
	}
	
	public void resourceCloser() {
		try {
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
}
