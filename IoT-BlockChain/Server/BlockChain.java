import java.sql.*;

public class BlockChain {
	public static int row;
	

	public static String[] Block(String Num, String key, String Command, String Prkey) {
		while (true) {
			try {
				int j = 0;
				String Result[] = { "Num", "key", "Command", "Prkey" };
				String chk = null;
				Class.forName("com.mysql.cj.jdbc.Driver");
				Connection con = DriverManager.getConnection(
						"jdbc:mysql://172.30.98.123:3306/IoT?characterEncoding=UTF-8&serverTimezone=UTC", "ehdjs2134",
						"Adpebajs12!");
				String qu = "select * from hash";
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(qu);

				rs.last();
				row = rs.getRow();
				rs.beforeFirst();
				//이부분에서 에러나는거같음 
				String[][] hash;
				hash = new String[row][4];
				while (rs.next()) {
					for (int i = 0; i < 4; i++) {
						hash[j][i] = rs.getString(Result[i]);
					}
					j++;
				}

				if (hash.length == row) {
					System.out.println("DB데이터 가져오기 성공");
					for (int a = 0; a < row; a++) {
						chk = SHA256.SHA256(hash[a][0] + hash[a][2] + hash[a][3]);
						if (hash[a][1].equals(chk)) {
							System.out.println((a + 1) + "번째 블럭이 이상이 없습니다");
							if (a + 1 != hash.length) {
								if (chk.equals(hash[a + 1][3])) {
									System.out.println("다음블럭과 이어져 있습니다");
								} else {
									System.out.println("다음블럭과 이어져있지 않습니다.");
								}
							} else if (a + 1 == hash.length) {
								System.out.println("다음블럭이 없습니다.");
								System.out.println("무결성 검증중");
								if (key.equals(hash[a][1])) {
									System.out.println("무결성 검증완료");
									Num = hash[a][0];
									int Cnt = Integer.parseInt(Num);
									Cnt = Cnt + 1;
									Num = String.valueOf(Cnt);
									key = hash[a][1];
									Command = hash[a][2];
									Prkey = hash[a][3];
								} else if (!key.equals(hash[a][1])) {
									System.out.println("해쉬값이 변조 되었습니다.");
									break;
								}
								rs.close();
								st.close();
								con.close();
							
							}
						} else {
							System.out.println("해쉬값이 변조 되었습니다.");
						}
					}
				} else
					System.out.println("DB데이터 가져오기 실패");
			
			} catch (Exception e) {
				System.out.println("fail");
				System.err.println(e.getMessage());
			}
			return new String[] { Num, key, Command, Prkey };
		}
	}

	public synchronized static String Hash(String tNum, String tkey, String tCommand, String tPrkey) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://172.30.98.123:3306/IoT?characterEncoding=UTF-8&serverTimezone=UTC", "ehdjs2134",
					"Adpebajs12!");
			String qu = "INSERT hash VALUES('" + tNum + "','" + tkey + "','" + tCommand + "','" + tPrkey + "')";
			Statement st = con.createStatement();
			st.executeUpdate(qu);

			st.close();
			con.close();
		} catch (Exception e) {
			System.out.println("fail");
			System.err.println(e.getMessage());
		}
		return null;

	}
}
