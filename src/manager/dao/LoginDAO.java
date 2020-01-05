package manager.dao;


import java.sql.*;
import java.util.ArrayList;

import manager.bean.MemberDTO;


public class LoginDAO {
	
	public static LoginDAO instance;
	
	public static LoginDAO getInstance(){
		if(instance == null){
			synchronized(LoginDAO.class){
				instance = new LoginDAO();
			}
		}
		return instance;
	}
	//-----------------------------------------------------------------싱글톤
	
	private String driver = "oracle.jdbc.driver.OracleDriver";
	private String url = "jdbc:oracle:thin:@localhost:1521:xe";
	private String username = "dbdb";
	private String password = "itbank";
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	
	public Connection getConnection() {
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	//-----------------------------------------------------------------드라이버 로딩
	public MemberDTO joinMember(MemberDTO dto) {
		getConnection();
		String sql ="insert into member values(seq_member.nextVal,?,?,?,?,?)";
		try {
			MemberDTO dto_send = dto;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getId());
			pstmt.setString(2, dto.getPw());
			pstmt.setString(3, dto.getName());
			pstmt.setString(4, dto.getTel());
			pstmt.setString(5, dto.getEmail());
			int su = pstmt.executeUpdate();
			if(su == 1) {
				dto_send.setCheck(true);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			resourceCloser();
		}
		return dto;
	}
	//-----------------------------------------------------------------회원가입
	
	private int getSeq() {	//접근제어자 변경 필요할 수 도 있음
		int seq = 0;
		getConnection();
		String sql = "select seq_member.nextval from dual";
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			//next전에는 아무것도 안가리킴
			rs.next();
			seq = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return seq;
	}
	//-----------------------------------------------------------------시퀀스값 서버에서 관리
	
	public ArrayList<MemberDTO> getMembers() {
		ArrayList<MemberDTO> list = new ArrayList<MemberDTO>();
		
		getConnection();
		String sql ="select * from member order by seq asc";
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				int seq = rs.getInt("seq");
				String id = rs.getString("id");
				String pw = rs.getString("pw");
				String name = rs.getString("name");
				String tel = rs.getString("tel");
				String email = rs.getString("email");
				
				MemberDTO dto = new MemberDTO();
				dto.setSeq(seq);
				dto.setId(id);
				dto.setPw(pw);
				dto.setName(name);
				dto.setTel(tel);
				dto.setEmail(email);
				
				list.add(dto);
			}//while
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			resourceCloser();
		}
		return list;
	}
	
	public MemberDTO needMember(MemberDTO dtoL) {
		getConnection();
		MemberDTO returnDTO = new MemberDTO();
		String sql = "select * from member where id = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dtoL.getId());
			System.out.println("needMember null?"+dtoL.getId());
			rs = pstmt.executeQuery();
			if(rs.next()) {
				returnDTO.setSeq(rs.getInt("seq"));
				returnDTO.setId(rs.getString("id"));
				returnDTO.setPw(rs.getString("pw"));
				returnDTO.setName(rs.getString("name"));
				returnDTO.setTel(rs.getString("tel"));
				returnDTO.setEmail(rs.getString("email"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			resourceCloser();
		}
		
		return returnDTO;
	}
	
	
	public void updateMember(MemberDTO dto) {
		getConnection();
		
		String sql = "update member set id=?, pw=?, name=?, tel=?, email=? where seq=?";
		
		try {
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1, dto.getId());
			pstmt.setString(2, dto.getPw());
			pstmt.setString(3, dto.getName());
			pstmt.setString(4, dto.getTel());
			pstmt.setString(5, dto.getEmail());
			pstmt.setInt(6, dto.getSeq());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			resourceCloser();
		}
	}
	
	public void userUpdateMember(MemberDTO dto) {
		getConnection();
		System.out.println("@@@@@@@@@유저업데이트 @@@@@@@@");
		String sql = "update member set pw=?, name=?, tel=?, email=? where id=?";
		
		try {
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1, dto.getPw());
			pstmt.setString(2, dto.getName());
			pstmt.setString(3, dto.getTel());
			pstmt.setString(4, dto.getEmail());
			pstmt.setString(5, dto.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			resourceCloser();
		}
	}
	
	//-----------------------------------------------------------------DB에서 멤버값 삭제
	public void deleteMember(MemberDTO dto) {
		getConnection();
		
		String sql = "delete member where seq = ?";
		
		try {
			pstmt= conn.prepareStatement(sql);
			pstmt.setInt(1, dto.getSeq());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			resourceCloser();
		}
	}
	//-----------------------------------------------------------------DB에 저장된 모든 회원 반환
	
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
