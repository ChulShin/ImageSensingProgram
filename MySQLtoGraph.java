package com.oracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
//import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
 
public class MySQLtoGraph extends ApplicationFrame {									//MySQLtoGraph 클래스는 ApplicationFrame을 상속 받음
    static ArrayList <String> name = new ArrayList<String> ();
    static ArrayList <String> score = new ArrayList<String> ();    
    public static void main(String[] args) throws SQLException{
        // db connection
        String jdbc_driver     = "com.mysql.jdbc.Driver";
        String jdbc_url     = "jdbc:mysql://localhost:3306/kobis";
        String user         = "root";
        String pwd            = "1234";
        
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        ResultSetMetaData metaData = null;
        
        try{
            // mysql 커넥션 설정
            Class.forName(jdbc_driver);													//Class.forName() 을 이용해서 드라이버 로드
            con = DriverManager.getConnection(jdbc_url, user, pwd);						//DriverManager.getConnection() 으로 연결 얻기
            stmt = con.createStatement();												//Connection 인스턴스를 이용해서 Statement 객체 생성
            String sql = "select * from fruits3";										//MySQL에서 사용할 쿼리문 작성
            rs = stmt.executeQuery(sql);												//쿼리문 실행 결과를 ResultSet에 받기
            metaData = rs.getMetaData();												//ResultSet과 관련된 메타 데이터를 얻어 ResultSetMetaData객체에 넣기
            
            // 각 행을 읽어 리스트에 저장한다.
            int sizeOfcolumn = metaData.getColumnCount();								//getColumnCount메서드로 ResultSet의 총 필드수를 반환하여 sizOfcolumn에 저장
            String column;
            List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
            Map<String, Object> map;
            
            while(rs.next()){															//rs.next()는 ResultSet에 다음 열이 존재하는지 검사한 후 다음 열이 존재하면 True를 반환
                map = new HashMap<String,Object>();										//HashMap의 key값은 String타입으로 받고, value값은 Object타입으로 받음
                
                for(int indexOfcolumn=0; indexOfcolumn<sizeOfcolumn; indexOfcolumn++){
                    column = metaData.getColumnName(indexOfcolumn + 1);					//String 타입의 column변수에 첫번째 열이름, 즉 DB의 필드명을 입력
                    map.put(column, rs.getString(column));								//map객체에 필드명과 주어진 행과 주어진 열에 해당하는 필드의 값을 받음
                }
                list.add(map);															//리스트에 map객체를 받음
            }
 
            // 테스트 출력
            for( Map<String, Object> map1 : list ){										//리스트에 저장된 map객체를 map1으로 명명하여 가져옴
            	Iterator<String> it = map1.keySet().iterator();							//HashMap타입인 map1의 key값만 모아서 it객체에 저장
            	while(it.hasNext()){													//it객체의 커서 다음 값이 존재하면
            		String key = it.next();												//그 다음 값을 key값에 저장
            		if(key.equals("name")) {											//필드명이 name이면
            			String value = (String)map1.get(key);							//필드명이 name인 데이터 값을
            			name.add(value);												//name이라는 ArrayList에 저장
            		}
            		if(key.equals("score")) {											//필드명이 score이면
            			String value = (String)map1.get(key);							//필드명이 score인 데이터 값을
            			score.add(value);												//score이라는 ArrayList에 저장
            		}
            	}
            }
        }catch(Exception e){															//데이터베이스를 읽다가 에러 발생시
            System.out.println("데이터 베이스 연결 실패");										//연결 실패 메세지를 출력하고
            System.out.println(e.getMessage());											//오류 메세지 출력									
        }finally{																		//오류가 발생하든 말든
            con.close();																//연결종료
            stmt.close();
        }
        
        //그래프 출력
        MySQLtoGraph demo = new MySQLtoGraph("prediction results");						//MySQLtoGraph클래스로 demo객체를 생성하고 프로그램 명을 "prediction results"로 설정
        demo.setSize(560, 367);															//(가로사이즈,세로사이즈)
        RefineryUtilities.centerFrameOnScreen( demo );    								//화면 정 중앙에 띄움
        demo.setVisible( true );
    }
    
    public MySQLtoGraph( String title ) {
       super( title );
       setContentPane(createDemoPanel( ));
    }
    
    private static PieDataset createDataset( ) {
       DefaultPieDataset dataset = new DefaultPieDataset( );
       for(int i=0; i<name.size(); i++) {
       dataset.setValue( name.get(i) , Double.parseDouble(score.get(i)) );						//name이라는 ArrayList와 score라는 ArrayList 각각에 저장되어있던 과일의 이름과 예측결과값을 불러와 차트에 입력
       }
       return dataset;         
    }
    
    private static JFreeChart createChart( PieDataset dataset ) {
       JFreeChart chart = ChartFactory.createPieChart(      
          "prediction results",   // chart title												//원 그래프를 생성함 
          dataset,          // data    
          false,             // include legend   
          true, 
          false);
       ((PiePlot) chart.getPlot()).setLabelGenerator(null);									//원 그래프의 라벨을 제거하는 코드

       return chart;
    }
    
    public static JPanel createDemoPanel( ) {
       JFreeChart chart = createChart(createDataset( ) );
       return new ChartPanel( chart ); 
    }

    
}
