package manager.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.print.attribute.standard.ReferenceUriSchemesSupported;

import manager.bean.*;

public class BookDAO {

	private static BookDAO instance;

	public static BookDAO getInstance() {
		if (instance == null) {
			synchronized (BookDAO.class) {
				instance = new BookDAO();
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

	public BookDAO() {
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

	public int getSeq() {
		getConnection();
		int seq = 0;
		String sql = "select seq_book.nextval from dual";

		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			rs.next();
			seq = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return seq;
	}
	// -----------------------------------------------------------------시퀀스값 서버에서 관리

	public BookDTO addBook(BookDTO dto) {
		getConnection();
		dto.setRentState(0);
		String sql = "insert into book values(seq_book.nextval,?,?,?,?,0)";

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getBookName());
			pstmt.setString(2, dto.getWriter());
			pstmt.setString(3, dto.getPublisher());
			pstmt.setString(4, dto.getGenre());
			int su = pstmt.executeUpdate();
			if (su == 1) {
				System.out.println("책 추가 완료");
			} else {
				System.out.println("책 추가 실패");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			resourceCloser();
		}
		return dto;
	}
	// -----------------------------------------------------------------혜진이 책 추가할 때

	public ArrayList<BookDTO> getBookList() {
		getConnection();
		String sql = "select * from book";
		ArrayList<BookDTO> list = new ArrayList<>();

		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				BookDTO dto = new BookDTO();
				dto.setSeq(rs.getInt("seq"));
				dto.setBookName(rs.getString("bookName"));
				dto.setWriter(rs.getString("writer"));
				dto.setPublisher(rs.getString("publisher"));
				dto.setGenre(rs.getString("genre"));
				dto.setRentState(rs.getInt("rentstate"));
				dto.setCover("test");
				list.add(dto);
			}
			System.out.println(list.size());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			resourceCloser();
		}
		return list;
	}

	// ----------------------------------------------------------------- 설명↙
	
	public void getUpdateBook(BookDTO dto) {
		System.out.println("업데이트북");
		getConnection();
		String sql = "update book set bookname=?,"
				+ "writer = ?, publisher = ?, genre = ?, rentstate = ? where seq = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getBookName());
			pstmt.setString(2, dto.getWriter());
			pstmt.setString(3, dto.getPublisher());
			pstmt.setString(4, dto.getGenre());
			pstmt.setInt(5, dto.getRentState());
			pstmt.setInt(6, dto.getSeq());
			int su = pstmt.executeUpdate();
			if (su == 1) {
				System.out.println("책 업데이트 완료");
			} else {
				System.out.println("책 업데이트 실패");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			resourceCloser();
		}
	}

	
	// ----------------------------------------------------------------- 설명↙
	public void deleteBook(BookDTO dto) {
		System.out.println("딜리트북");
		getConnection();
		String sql ="delete book where seq = ?";
		
		try {
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, dto.getSeq());
			int su = pstmt.executeUpdate();
			if (su == 1) {
				System.out.println("책 삭제완료");
			} else {
				System.out.println("책 삭제 실패");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			resourceCloser();
		}
		
	}
	
	public BookDTO findBook(BookDTO dto) {
		getConnection();
		
		String sql = "select * from book where seq = ?";
		try {
			pstmt= conn.prepareStatement(sql);
			pstmt.setInt(1, dto.getSeq());
			rs = pstmt.executeQuery();
			while(rs.next()) {
				dto.setBookName(rs.getString("bookName"));
				dto.setWriter(rs.getString("writer"));
				dto.setPublisher(rs.getString("publisher"));
				dto.setGenre(rs.getString("genre"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			resourceCloser();
		}
		
		
		return dto;
	}

	// ----------------------------------------------------------------- 설명↙

	/*
	 * <관리자> - 로그인 성공시 관리자 혼자에게 - 관리자가 책 추가 할 시 모두에게 <사용자> - 로그인 성공시 사용자 혼자에게
	 * 
	 */

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
