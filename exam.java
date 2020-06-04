package JDBC;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;

public class exam {
	public static final String ITEM_LIST = "itemList";// 테이블 이름
	public static final String ITEM_NAME = "name";// 컬럼name
	public static final String ITEM_WEIGHT = "weight";// 컬럼weight
	public static final String ITEM_DISPLAY = "display";// 컬럼display
	public static final String ITEM_VOLUME = "volume";// 컬럼volume
	public static final String ITEM_ETC = "etc";// 컬럼etc
	public static final String ITEM_PRICE = "price";// 컬럼price
	static Scanner scnew = new Scanner(System.in);// scanner를 static으로 이용
	static Statement statement = null;//
	static ResultSet resultSet = null;// 물품비교에 사용되고 비교대상A의 데이터
	static ResultSet resultSet2 = null;// 물품비교에 사용되고 비교대상B의 데이터
	static ResultSet resultSet3 = null;// 물품비교확인을 위해 사용되는 데이터
	static ResultSetMetaData rsmd;// 칼럼 수를 읽어들이기 위한 변수
	static PreparedStatement pstmt;// 쿼리문 을 실행하기 위해 사용

	// conneciton연결을 하기위한 메소드
	public static void connectionClose(Connection connection) {
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
		}
	}

	// connection연결을 해지하기 위한 메소드
	public static Connection connection() {
		Connection connection = null;
		String id = "root";
		String password = "jack!3595";

		try {
			connection = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/class?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Seoul&useSSL=false",
					id, password);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}

	// 내 예산에 구매가능한 item 출력
	public static void buyableItem() throws SQLException {
		Connection connection = exam.connection();
		System.out.print("내 예산입력--------> ");
		int money = scnew.nextInt();// 내 예산입력
		System.out.println("<<내 예산에 구매가능한 item보기>>");
		pstmt = connection.prepareStatement("SELECT NAME,PRICE FROM ITEMLIST WHERE PRICE<=?");// 물품명과 가격 선택
		pstmt.setInt(1, money);
		resultSet = pstmt.executeQuery();
		while (resultSet.next()) {
			for (int i = 1; i <= 2; i++) {
				String str = resultSet.getString(i);
				if (i == 1) {
					System.out.print(str + " ");// item명 출력후 한칸 띄우기
				} else {
					System.out.print(str);// price 출력
				}
			}
			System.out.print("만원");// 글자 '만원' 붙이기
			System.out.println();
		}

		// 연결해지
		exam.connectionClose(connection);
		pstmt.close();
		resultSet.close();
	}

	// item을 삽입하기 위한 메소드
	public static void itemInsert() throws SQLException {
		Connection connection = exam.connection();
		String name = "";
		int weight = 0;
		int display = 0;
		int volume = 0;
		String etc = "";
		int price = 0;
		int k = 1;
		int j = 0;
		System.out.println("새로입력할 아이템의 정보를 입력합니다.");
		// 아래 변수들에 입력할 물품의 정보를 입력
		System.out.print("아이템 이름-->");
		name = scnew.next();
		System.out.print("아이템 무게-->");
		weight = scnew.nextInt();
		System.out.print("아이템 화면-->");
		display = scnew.nextInt();
		System.out.print("아이템 용량-->");
		volume = scnew.nextInt();
		System.out.print("아이템 설명(비고)-->");
		etc = scnew.next();
		System.out.print("아이템 가격-->");
		price = scnew.nextInt();
		// 입력한 물품의 정보를 가지고 쿼리문 실행
		pstmt = connection.prepareStatement(
				"INSERT INTO ITEMLIST" + " (name,weight,display,volume,etc,price) " + " VALUES (?, ?, ?, ?, ?, ?)");
		pstmt.setString(1, name);// 1번 물음표에 name 삽입
		pstmt.setInt(2, weight);// 2번 물음표에 weight 삽입
		pstmt.setInt(3, display);// 3번 물음표에 display 삽입
		pstmt.setInt(4, volume);// 4번 물음표에 volume 삽입
		pstmt.setString(5, etc);// 5번 물음표에 etc 삽입
		pstmt.setInt(6, price);// 6번 물음표에 price 삽입
		pstmt.executeUpdate();// 테이블에 삽입

		System.out.println("새로운 아이템이 삽입되었습니다.");

		// 연결들 해지
		exam.connectionClose(connection);
		pstmt.close();

	}

	// item의 정보를 수정하기 위한 메소드
	public static void itemChange() throws SQLException, InterruptedException {
		Connection connection = exam.connection();// 연결설정
		String update = "";// update변수에수정할 칼럼 값을 넣는다.
		int k = 1;
		int no = 0;

		pstmt = connection.prepareStatement("SELECT * FROM ITEMLIST ");
		resultSet = pstmt.executeQuery();

		// 컬럼의 갯수를 구하여 테이블 출력
		rsmd = resultSet.getMetaData();
		int cols = rsmd.getColumnCount();
		while (resultSet.next()) {
			System.out.print("(" + k + ")" + " ");
			for (int i = 2; i <= cols; i++) {
				String str = resultSet.getString(i);

				System.out.print(str + " ");
			}
			System.out.println();
			k++;
		}

		while (true) {
			System.out.println("수정할 아이템의 번호를 입력해주세요");
			no = scnew.nextInt();
			if (no <= k) {
				break;
			}
		}
		System.out.println("수정할 컬럼을 고르시오([1]이름 [2]무게 [3]화면 [4]디스크용량 [5]비고 [6]가격");
		// 선택한 숫자에 따라update변수에 수정할 컬럼명을 넣는다.
		int select = scnew.nextInt();
		if (select == 1) {

			update = ITEM_NAME;
		} else if (select == 2) {

			update = ITEM_WEIGHT;
		} else if (select == 3) {

			update = ITEM_DISPLAY;
		} else if (select == 4) {

			update = ITEM_VOLUME;
		} else if (select == 5) {

			update = ITEM_ETC;
		} else {

			update = ITEM_PRICE;
		}

		// switch문을 통해 물품을 수정
		switch (select) {
		case 1:
			System.out.println("새로운 물품명을 정하세요(New)");
			String newName = scnew.next();
			pstmt = connection.prepareStatement("UPDATE ITEMLIST SET  " + update + " = ? WHERE NO=?");
			pstmt.setString(1, newName);
			pstmt.setInt(2, no);
			pstmt.executeUpdate();
			System.out.println("새로운 물품명으로 바뀌는중 입니다.");
			break;
		case 2:
			System.out.println("새로운 무게를 입력하세요(New)");
			int newWeight = scnew.nextInt();
			pstmt = connection.prepareStatement("UPDATE ITEMLIST SET  " + update + " = ? WHERE NO=?");
			pstmt.setInt(1, newWeight);
			pstmt.setInt(2, no);
			pstmt.executeUpdate();
			System.out.println("새로운 무게로 바뀌는중 입니다.");
			break;
		case 3:
			System.out.println("새로운 화면을 입력하세요(New)");
			String newDisplay = scnew.next();
			pstmt = connection.prepareStatement("UPDATE ITEMLIST SET  " + update + " = ? WHERE NO=?");
			pstmt.setString(1, newDisplay);
			pstmt.setInt(2, no);
			pstmt.executeUpdate();
			System.out.println("새로운 화면로 바뀌는중 입니다.");
			break;
		case 4:
			System.out.println("새로운 디스크 용량의 크기 입력하세요(New)");
			int newVolume = scnew.nextInt();
			pstmt = connection.prepareStatement("UPDATE ITEMLIST SET  " + update + " = ? WHERE NO=?");
			pstmt.setInt(1, newVolume);
			pstmt.setInt(2, no);
			pstmt.executeUpdate();
			System.out.println("새로운 디스크 용량으로 바뀌는중 입니다.");
			break;
		case 5:
			System.out.println("비고에 새로운 내용을 입력하세요(New)");
			String newEtc = scnew.next();
			pstmt = connection.prepareStatement("UPDATE ITEMLIST SET  " + update + " = ? WHERE NO=?");
			pstmt.setString(1, newEtc);
			pstmt.setInt(2, no);
			pstmt.executeUpdate();
			System.out.println("새로운 비고내용이 입력되는중 입니다.");
			break;
		default:
			System.out.println("새로운 가격을 입력하세요(New)");
			int newPrice = scnew.nextInt();
			pstmt = connection.prepareStatement("UPDATE ITEMLIST SET  " + update + " = ? WHERE NO=?");
			pstmt.setInt(1, newPrice);
			pstmt.setInt(2, no);
			pstmt.executeUpdate();
			System.out.println("새로운 가격으로로 바뀌는중 입니다.");
		}
		System.out.println("--------------수정중--------------");
		Thread.sleep(1000);
		System.out.println("itemList가 수정되었습니다!");

		// 연결해지
		exam.connectionClose(connection);
		pstmt.close();
		resultSet.close();
	}

	// itemcompare메소드에서 보낸 값을 토대로 item비교
	// boolean값을 리턴하는 이유는 selectcompareitem메소드에서 리턴한 boolean값을 토대로 계속 비교를 진행할지 말지를
	// 결정
	public static boolean selectCompareItem(int a, int b) throws SQLException {
		Connection connection = exam.connection();// 연결 설정

		StringBuilder sb1 = new StringBuilder();// stringbuilder를 사용해서 마지막에 한줄에 출력
		StringBuilder sb2 = new StringBuilder();
		boolean rflag = true;// 리턴할 boolean 변수
		String str = "";
		String str2 = "";
		String[] column = { "무게", "화면", "디스크용량", "가격" };
		System.out.println("###물품 비교결과###");
		int k = 0;

		// itemcompare에서 가져온a값으로 특정 데이터 추출
		pstmt = connection.prepareStatement("SELECT * FROM ITEMLIST " + "WHERE NO = " + "?");
		pstmt.setInt(1, a);
		resultSet = pstmt.executeQuery();

		// 선택한 테이블출력(a,b)을 위한 resultSet3
		pstmt = connection.prepareStatement("SELECT * FROM ITEMLIST " + "WHERE NO = " + "? OR NO =?");
		pstmt.setInt(1, a);
		pstmt.setInt(2, b);
		resultSet3 = pstmt.executeQuery();

		// itemcompare에서 가져온b값으로 특정 데이터 추출
		pstmt = connection.prepareStatement("SELECT * FROM ITEMLIST " + "WHERE NO = " + "?");
		pstmt.setInt(1, b);
		resultSet2 = pstmt.executeQuery();

		// 컬럼의 갯수를 계산
		rsmd = resultSet.getMetaData();
		int cols = rsmd.getColumnCount();

		// 인자로 받은 a,b에 해당하는 테이블 출력
		while (resultSet3.next()) {
			System.out.print(a + " ");
			for (int i = 2; i <= cols; i++) {
				String str3 = resultSet3.getString(i);

				System.out.print(str3 + " ");
			}
			System.out.println();
			k++;
			a = b;// a와b를 가지고 선택한 item의 행번호만을 선택하여 출력가능
		}
		k = 0;
		// 비교를 통해 값을 저장할 변수
		int c1 = 0;
		int c2 = 0;

		while (resultSet.next() && resultSet2.next()) {// no=a ,no=b인 resultSet을 가지고 비교를 진행하고 추천 item을 선정
			for (int i = 3; i <= cols; i++) {
				if (i == 6) {// 6번째 컬럼은 비교대상 불가 컬럼이므로 제외
					continue;
				}
				int com1 = resultSet.getInt(i);
				int com2 = resultSet2.getInt(i);
				str = resultSet.getString(2);
				str2 = resultSet2.getString(2);

				/*
				 * k=0 무게비교 k=1 화면비교 k=2 디스크용량비교 k=3가격비교
				 */
				if (com1 > com2) {// 값을 비교
					System.out.println("*" + column[k] + ": " + str + " > " + str2);
					if (k == 0) {// 무게비교
						c2 += 20;// 더 가벼운 노트북에 +
						sb2.append(" 조건" + (k + 1) + "(20)+");// 나중에 출력을 위해 append
					} else if (k == 3) {// 가격비교
						c2 += 40;
						sb2.append(" 조건4(40)");// 나중에 출력을 위해 append
					} else {// 화면,디스크비교
						c1 += 20;
						sb1.append(" 조건" + (k + 1) + "(20)+");// 나중에 출력을 위해 append
					}
				} else if (com1 < com2) {
					System.out.println("*" + column[k] + ": " + str + " < " + str2);
					if (k == 0) {// 무게비교
						c1 += 20;
						sb1.append(" 조건" + (k + 1) + "(20)+");// 나중에 출력을 위해 append
					} else if (k == 3) {// 가격비교
						c1 += 40;
						sb1.append(" 조건4(40)");// 나중에 출력을 위해 append
					} else {// 화면,디스크비교
						c2 += 20;
						sb2.append(" 조건" + (k + 1) + "(20)+");// 나중에 출력을 위해 append
					}
				} else {
					System.out.println("*" + column[k] + ": " + str + " = " + str2);
				}
				k++;
			}
		}
		System.out.println("추천: 조건1(20점), 조건2(20점), 조건3(20점), 가격(40점)");
		System.out.println(str + ": " + sb1 + " = " + c1 + "점");// append한 내용을 전부 출력, 점수도 출력
		System.out.println(str2 + ": " + sb2 + " = " + c2 + "점");// append한 내용을 전부 출력, 점수도 출력
		// 큰값을 출력한다.
		if (c1 > c2) {
			System.out.println("최종 추천: " + str);
		} else {
			System.out.println("최종 추천: " + str2);
		}

		// 연결해지
		exam.connectionClose(connection);
		resultSet.close();
		resultSet2.close();
		resultSet3.close();
		pstmt.close();

		// 더 실행할지말지 여부를 묻는다.
		System.out.println("더 비교하시겠습니까?(1:예, 2:아니요,-상위메뉴로 이동)");
		int more = scnew.nextInt();
		if (more == 2) {
			rflag = false;// 2이면 false를 itemcompare메소드에 보내서 메소드를 끝낸다.
		}
		return rflag;// 아니면 true를 보내서 루프를 돌린다.
	}

	// 비교를 위하여 item을 정하는 메소드
	public static void itemCompare() throws SQLException, InterruptedException {
		Connection connection = exam.connection();
		System.out.println("###물품 리스트###");
		String a = "";
		String b = "";
		int k = 1;
		boolean flag = true;
		boolean flag2 = true;

		// 목록을 전부출력하여 사용자가 확인할 수 있게 한다.
		while (flag) {
			Scanner sc = new Scanner(System.in);
			pstmt = connection.prepareStatement("SELECT * FROM ITEMLIST");
			resultSet = pstmt.executeQuery();
			System.out.println("No 이름 무게(g) 화면(인치) 디스크용량(기가바이트) 비고 가격(만원)");
			rsmd = resultSet.getMetaData();
			int cols = rsmd.getColumnCount();
			while (resultSet.next()) {
				System.out.print(k + " ");
				for (int i = 2; i <= cols; i++) {
					String str = resultSet.getString(i);
					System.out.print(str + " ");
				}
				System.out.println();
				k++;
			}

			// 이전메뉴로 갈건지,비교대상을 선택할지를 고른다.
			try {
				System.out.println("(상위 메뉴로 가시려면 -1을 입력하시오)");
				System.out.print("비교할 item A의 번호를 입력하시오 -->");
				while (flag2) {
					try {
						a = sc.next();
						if (Integer.parseInt(a) == -1) {
							flag = false;
						} else {
							flag2 = false;
						}
						System.out.print("비교할 item B의 번호를 입력하시오 -->");
						b = sc.next();
						if (Integer.parseInt(b) == -1) {
							flag = false;
						} else {
							flag2 = false;
						}
					} catch (NumberFormatException e) {
						System.out.println("다시 입력하세요");
						System.out.print("비교할 item의 번호를  다시 입력하시오 -->");
					}
				}
				System.out.println();
				k = 1;// 다시 실행시
				flag = selectCompareItem(Integer.parseInt(a), Integer.parseInt(b));// selectcompareitem메소드에 a,b의 값을 보내고
																					// 나중에 flag값을 받아 게속 실행할지 말지를 정한다.
			} catch (InputMismatchException e) {
				System.out.println("잘못된 입력 다시 입력");

			}

		}
		// 연결해지
		exam.connectionClose(connection);
		resultSet.close();
		pstmt.close();
	}

	// main
	public static void main(String[] args) throws IOException, SQLException, InterruptedException {
		while (true) {
			Scanner sc = new Scanner(System.in);

			System.out.println("###메뉴###");
			System.out.println("1. 물품 비교하기");
			System.out.println("2. 물품 수정하기");
			System.out.println("3. 물품 입력하기");
			System.out.println("4.구매가능 물품 보기");
			System.out.print("원하시는 메뉴를 선택하세요(0은 초기메뉴)-->");
			try {
				int num = sc.nextInt();
				switch (num) {
				case 1:// 1일경우 물품비교
					System.out.println();
					itemCompare();
					break;
				case 2:// 2.물품 수정
					itemChange();
					break;
				case 3:// 물품 입력
					itemInsert();
					break;
				case 4:
					buyableItem();
					break;
				default:
					System.out.println("다시 입력");
				}

			} catch (InputMismatchException e) {
				System.out.println("제대로 입력하세요");
			}

		}
	}
}
