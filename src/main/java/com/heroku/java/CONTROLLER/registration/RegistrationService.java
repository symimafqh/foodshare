// package com.heroku.java.CONTROLLER.registration;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.stereotype.Controller;
// import org.springframework.stereotype.Repository;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.ModelAttribute;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.stereotype.Service;
// import org.springframework.jdbc.core.JdbcTemplate;

// import com.heroku.java.MODEL.student.StudentBean;
// import com.heroku.java.MODEL.registration.RegistrationBean;

// import jakarta.servlet.http.HttpSession;

// import java.sql.*;
// import javax.sql.DataSource;
// import java.util.ArrayList;
// import java.util.Map;

// import java.util.List;

// @Service
// public class RegistrationService {
    
//     private final DataSource dataSource;

//     @Autowired
//     public RegistrationService(DataSource dataSource) {
//         this.dataSource = dataSource;
//     }

//     public boolean isStudentAlreadyRegistered(String studentIC) {
//         try (Connection con = dataSource.getConnection();
//              final var statement = con.prepareStatement("SELECT COUNT(*) FROM registration WHERE studentic = ?")) {

//             statement.setString(1, studentIC);

//             try (final var resultSet = statement.executeQuery()) {
//                 return resultSet.next() && resultSet.getInt(1) > 0;
//             }
//         } catch (SQLException e) {
//             e.printStackTrace(); // Handle the exception based on your application's requirements
//             return false;
//         }
//     }

//     // Check if the quota is available
// 	public boolean isQuotaAvailable(Connection con, int activityID) throws SQLException {
// 		int currentQuota = getQuotaForActivityUnit(con, activityID);
// 		System.out.println("current quota "+ activityID +" is "+currentQuota);
// 		return currentQuota > 0;
// 	}

//     	// GET QUOTA
// 	private int getQuotaForActivityUnit(Connection con, int activityID) throws SQLException {
// 		String sql = "SELECT uniformquota FROM uniform WHERE activityid = ?";
// 		try (PreparedStatement pstmt = con.prepareStatement(sql)) {
// 			pstmt.setInt(1, activityID);
// 			try (ResultSet rs = pstmt.executeQuery()) {
// 				if (rs.next()) {
// 					return rs.getInt("uniformquota");
					
// 				}
// 			}
// 		}
// 		return 0; // Default to 0 if activityID not found (you may want to handle this
// 					// differently)
// 	}

//     // Check if the quota is available
// 	public boolean isQuotaAvailableClub(Connection con, int activityID) throws SQLException {
// 		int currentQuota = getQuotaForActivityClub(con, activityID);
// 		System.out.println("current quota "+ activityID +" is "+currentQuota);
// 		return currentQuota > 0;
// 	}

//     // GET QUOTA
// 	private int getQuotaForActivityClub(Connection con, int activityID) throws SQLException {
// 		String sql = "SELECT clubquota FROM club WHERE activityid = ?";
// 		try (PreparedStatement pstmt = con.prepareStatement(sql)) {
// 			pstmt.setInt(1, activityID);
// 			try (ResultSet rs = pstmt.executeQuery()) {
// 				if (rs.next()) {
// 					return rs.getInt("clubquota");
					
// 				}
// 			}
// 		}
// 		return 0; // Default to 0 if activityID not found (you may want to handle this
// 					// differently)
// 	}

//     // Check if the quota is available
// 	public boolean isQuotaAvailableSport(Connection con, int activityID) throws SQLException {
// 		int currentQuota = getQuotaForActivitySport(con, activityID);
// 		System.out.println("current quota "+ activityID +" is "+currentQuota);
// 		return currentQuota > 0;
// 	}

//     // GET QUOTA
// 	private int getQuotaForActivitySport(Connection con, int activityID) throws SQLException {
// 		String sql = "SELECT sportquota FROM sport WHERE activityid = ?";
// 		try (PreparedStatement pstmt = con.prepareStatement(sql)) {
// 			pstmt.setInt(1, activityID);
// 			try (ResultSet rs = pstmt.executeQuery()) {
// 				if (rs.next()) {
// 					 int quota = rs.getInt("sportquota");
// 		                System.out.println("Quota for activityID " + activityID + ": " + quota);
// 		                return quota;
					
// 				}
// 			}
// 		}
// 		return 0; // Default to 0 if activityID not found (you may want to handle this
// 					// differently)
// 	}

//     public void insertRegistrationUnit(Connection con, String studentIC, int activityID) {
// 	    try {
// 	        // Check if the quota is available
// 	        if (isQuotaAvailable(con, activityID)) {
// 	            try (PreparedStatement ps = con.prepareStatement("INSERT INTO registration(studentic, activityid) VALUES (?,?)")) {
// 	                ps.setString(1, studentIC);
// 	                ps.setInt(2, activityID);

// 	                // execute update for insert operation
// 	                ps.executeUpdate();
// 	                System.out.println("Successfully inserted registration for " + studentIC + " and activityID " + activityID);
	                
// 	             // Update the quota (decrement by 1)
// 	                int newQuota = updateQuotaForActivity(con, activityID, getQuotaForActivityUnit(con, activityID) - 1);
// 	                System.out.println("Dah decrement satu untuk " + activityID + ". New quota: " + newQuota);
// 	            }
// 	        }
// 	    } catch (SQLException e) {
// 	        e.printStackTrace();
// 	    }
// 	}

//     // UPDATE QUOTA
// 	private int updateQuotaForActivity(Connection con, int activityID, int newQuota) throws SQLException {
// 		String sql = "UPDATE uniform SET unitquota = ? WHERE activityid = ?";
// 		try (PreparedStatement pstmt = con.prepareStatement(sql)) {
// 			pstmt.setInt(1, newQuota);
// 			pstmt.setInt(2, activityID);
// 			pstmt.executeUpdate();
// 		}
// 		 //System.out.println("Quota updated for activityID " + activityID + ": " + newQuota);
// 		    return newQuota;
// 	}

//     public void insertRegistrationClub(Connection con, String studentIC, int activityID) {
// 	    try {
// 	        // Check if the quota is available
// 	        if (isQuotaAvailableClub(con, activityID)) {
// 	            try (PreparedStatement ps = con.prepareStatement("INSERT INTO registration(studentic, activityid) VALUES (?,?)")) {
// 	                ps.setString(1, studentIC);
// 	                ps.setInt(2, activityID);

// 	                // execute update for insert operation
// 	                ps.executeUpdate();
// 	                System.out.println("Successfully inserted registration for " + studentIC + " and activityID " + activityID);
	                
// 	             // Update the quota (decrement by 1)
// 	                int newQuota = updateQuotaForActivityClub(con, activityID, getQuotaForActivityClub(con, activityID) - 1);
// 	                System.out.println("Dah decrement satu untuk " + activityID + ". New quota: " + newQuota);
// 	            }
// 	        }
// 	    } catch (SQLException e) {
// 	        e.printStackTrace();
// 	    }
// 	}

//     	// UPDATE QUOTA
// 	private int updateQuotaForActivityClub(Connection con, int activityID, int newQuota) throws SQLException {
// 		String sql = "UPDATE club SET clubquota = ? WHERE activityid = ?";
// 		try (PreparedStatement pstmt = con.prepareStatement(sql)) {
// 			pstmt.setInt(1, newQuota);
// 			pstmt.setInt(2, activityID);
// 			pstmt.executeUpdate();
// 		}
// 		 //System.out.println("Quota updated for activityID " + activityID + ": " + newQuota);
// 		    return newQuota;
// 	}
	

//     	//sport
// 	public void insertRegistrationSport(Connection con, String studentIC, int activityID) {
// 	    try {
// 	        // Check if the quota is available
// 	        if (isQuotaAvailableSport(con, activityID)) {
// 	            try (PreparedStatement ps = con.prepareStatement("INSERT INTO registration(studentic, activityid) VALUES (?,?)")) {
// 	                ps.setString(1, studentIC);
// 	                ps.setInt(2, activityID);

// 	                // execute update for insert operation
// 	                ps.executeUpdate();
// 	                System.out.println("Successfully inserted registration for " + studentIC + " and activityID " + activityID);
	                
// 	             // Update the quota (decrement by 1)
// 	                int newQuota= updateQuotaForActivitySport(con, activityID, getQuotaForActivitySport(con, activityID) - 1);
// 	                System.out.println("Dah decrement satu untuk " + activityID + ". New quota: " + newQuota);
// 	            }
// 	        }
// 	    } catch (SQLException e) {
// 	        e.printStackTrace();
// 	    }
// 	}

//     // UPDATE QUOTA
// 	private int updateQuotaForActivitySport(Connection con, int activityID, int newQuota) throws SQLException {
// 		String sql = "UPDATE sport SET sportquota = ? WHERE activityid = ?";
// 		try (PreparedStatement pstmt = con.prepareStatement(sql)) {
// 			pstmt.setInt(1, newQuota);
// 			pstmt.setInt(2, activityID);
// 			pstmt.executeUpdate();
// 		}
// 		 //System.out.println("Quota updated for activityID " + activityID + ": " + newQuota);
// 		    return newQuota;
// 	}


// }
