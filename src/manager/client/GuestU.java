package manager.client;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import manager.bean.ExitDTO;
import manager.bean.MemberDTO;
import manager.bean.RentDTO;



public class GuestU extends JFrame {
	
	private JPanel manageP,changeMemberInfoP,myPageP;
	private SeatU SeatP;
	private JTabbedPane jtp = new JTabbedPane(SwingConstants.TOP);
	private String id;
	
	private int counting;
	
	public GuestU(ObjectOutputStream oos, ObjectInputStream ois, MemberDTO data) {
		this.id = data.getId();
		setTitle(id+"님 환영합니다");
		
		
		RentDTO dto = new RentDTO();
		dto.setId(id);
		dto.setCommand("Counting");
		try {
			oos.writeObject(dto);
			oos.flush();
			dto = (RentDTO)ois.readObject();
			counting = dto.getCounting();
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		
		manageP = new ManageU(oos, ois, id, this);
		SeatP = new SeatU(id);
		myPageP = new MyPageU(oos, ois, data, this);
		
		jtp = new JTabbedPane();
		jtp.addTab("도서관리",manageP);
		jtp.addTab("자리보기", SeatP);
		jtp.addTab("마이페이지", myPageP);
		
		
		add(jtp);
		
		jtp.setBackground(Color.PINK);
		setBounds(300, 200, 1000, 500);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		jtp.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(jtp.getSelectedIndex() == 0) {//탭누를때마다 발생하는 이벤트작성
					RentDTO dto = new RentDTO();
					dto.setId(id);
					dto.setCommand("Counting");
					try {
						oos.writeObject(dto);
						oos.flush();
						dto = (RentDTO)ois.readObject();
						counting = dto.getCounting();
					} catch (IOException e2) {
						e2.printStackTrace();
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		//이벤트
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int result = JOptionPane.showConfirmDialog(GuestU.this, "종료하시겠습니까?", 
															"프로그램종료", JOptionPane.OK_CANCEL_OPTION, 
															JOptionPane.QUESTION_MESSAGE);
				if(result==JOptionPane.OK_OPTION) {
					if(oos == null || ois == null)System.exit(0);
					try {
						SeatP.exit();
						ExitDTO dto = new ExitDTO();
						
						dto.setExit("Exit");
						oos.writeObject(dto);
						oos.flush();
						System.exit(0);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
	}

	public int getCounting() {
		return counting;
	}

	public void setCounting(int counting) {
		this.counting = counting;
	}

	public SeatU getSeatP() {
		return SeatP;
	}

	public void setSeatP(SeatU seatP) {
		SeatP = seatP;
	}





}
