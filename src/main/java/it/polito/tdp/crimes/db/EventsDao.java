package it.polito.tdp.crimes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.crimes.Arco;
import it.polito.tdp.crimes.model.Event;


public class EventsDao {
	
	public void listAllEvents(Map<Long, Event> idMap){
		String sql = "SELECT * FROM events " ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					if(!idMap.containsKey(res.getLong("incident_id"))) {
					Event e=new Event(res.getLong("incident_id"),
							res.getInt("offense_code"),
							res.getInt("offense_code_extension"), 
							res.getString("offense_type_id"), 
							res.getString("offense_category_id"),
							res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"),
							res.getDouble("geo_lon"),
							res.getDouble("geo_lat"),
							res.getInt("district_id"),
							res.getInt("precinct_id"), 
							res.getString("neighborhood_id"),
							res.getInt("is_crime"),
							res.getInt("is_traffic"));
					idMap.put(e.getIncident_id(), e);
					}
				} catch (Throwable t) {
					t.printStackTrace();
					
				}
			}
			
			conn.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		}
	}
	
	public List<Integer> getAnno(){
		String sql="SELECT YEAR(e.reported_date) as anno " + 
				"FROM events as e " + 
				"GROUP BY YEAR(e.reported_date) " + 
				"ORDER BY YEAR(e.reported_date) ASC ";
			List<Integer> result=new ArrayList<>();
			
			try {
				Connection conn = DBConnect.getConnection() ;

				PreparedStatement st = conn.prepareStatement(sql) ;
				
				ResultSet res = st.executeQuery() ;
				
				while(res.next()) {
					try {
						result.add(res.getInt("anno"));
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
				
				conn.close();
				return result ;

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null ;
			}
			
	}
	public List<String> getCategory(){
		String sql="SELECT DISTINCT(e.offense_category_id) as cat " + 
				"FROM events as e " + 
				"GROUP BY e.offense_category_id " + 
				"ORDER BY e.offense_category_id ASC ";
			List<String> result=new ArrayList<>();
			
			try {
				Connection conn = DBConnect.getConnection() ;

				PreparedStatement st = conn.prepareStatement(sql) ;
				
				ResultSet res = st.executeQuery() ;
				
				while(res.next()) {
					try {
						result.add(res.getString("cat"));
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
				
				conn.close();
				return result ;

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null ;
			}
			
	}
	
	

	public List<String> getVertici(String category, int anno, Map<Long, Event> idMap) {
		String sql="SELECT e.offense_type_id as reato, e.incident_id as id " + 
				"FROM events as e " + 
				"WHERE YEAR(e.reported_date)=? and e.offense_category_id=? " + 
				"GROUP BY e.offense_type_id, id";
		List<String> result=new ArrayList<>();
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, anno);
			st.setString(2, category);
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					if(idMap.containsKey(res.getLong("id"))) {
						
					result.add(res.getString("reato"));
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			
			conn.close();
			return result ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

	public List<Arco> getArchi(String category, int anno) {
		String sql="SELECT e1.offense_type_id AS id1, e2.offense_type_id AS id2, COUNT(DISTINCT e1.district_id) AS peso " + 
				"FROM events AS e1, events AS e2 " + 
				"WHERE e1.offense_type_id != e2.offense_type_id AND e1.district_id = e2.district_id AND " + 
				"e1.offense_category_id = ? AND e2.offense_category_id = ? AND " + 
				"YEAR(e1.reported_date) = ? AND YEAR(e2.reported_date) = ? " + 
				"GROUP BY e1.offense_type_id, e2.offense_type_id ";
		
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setString(1, category);
			st.setString(2, category);
			st.setInt(3, anno);
			st.setInt(4, anno);
			List<Arco> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				list.add(new Arco(res.getString("id1"), res.getString("id2"), res.getDouble("peso")));
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
				}

}
