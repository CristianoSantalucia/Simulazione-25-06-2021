package it.polito.tdp.yelp.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.yelp.model.Business;
import it.polito.tdp.yelp.model.Review;
import it.polito.tdp.yelp.model.User;

public class YelpDao
{

	public List<Business> getAllBusiness()
	{
		String sql = "SELECT * FROM Business";
		List<Business> result = new ArrayList<Business>();
		Connection conn = DBConnect.getConnection();

		try
		{
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next())
			{

				Business business = new Business(res.getString("business_id"), res.getString("full_address"),
						res.getString("active"), res.getString("categories"), res.getString("city"),
						res.getInt("review_count"), res.getString("business_name"), res.getString("neighborhoods"),
						res.getDouble("latitude"), res.getDouble("longitude"), res.getString("state"),
						res.getDouble("stars"));
				result.add(business);
			}
			res.close();
			st.close();
			conn.close();
			return result;

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public List<Review> getAllReviews()
	{
		String sql = "SELECT * FROM Reviews";
		List<Review> result = new ArrayList<Review>();
		Connection conn = DBConnect.getConnection();

		try
		{
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next())
			{

				Review review = new Review(res.getString("review_id"), res.getString("business_id"),
						res.getString("user_id"), res.getDouble("stars"), res.getDate("review_date").toLocalDate(),
						res.getInt("votes_funny"), res.getInt("votes_useful"), res.getInt("votes_cool"),
						res.getString("review_text"));
				result.add(review);
			}
			res.close();
			st.close();
			conn.close();
			return result;

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public List<User> getAllUsers()
	{
		String sql = "SELECT * FROM Users";
		List<User> result = new ArrayList<User>();
		Connection conn = DBConnect.getConnection();

		try
		{
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next())
			{

				User user = new User(res.getString("user_id"), res.getInt("votes_funny"), res.getInt("votes_useful"),
						res.getInt("votes_cool"), res.getString("name"), res.getDouble("average_stars"),
						res.getInt("review_count"));

				result.add(user);
			}
			res.close();
			st.close();
			conn.close();
			return result;

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public List<String> getAllCities()
	{
		String sql = "SELECT DISTINCT city " + "FROM business " + "ORDER BY city ";

		List<String> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try
		{
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next())
				result.add(res.getString("city"));
			res.close();
			st.close();
			conn.close();
			return result;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public void getVertici(Map<String, Business> idMap, String city, Integer year)
	{
		String sql = "SELECT * "
					+ "FROM business b, reviews r "
					+ "WHERE r.business_id = b.business_id "
					+ "AND b.city = ? "
					+ "AND YEAR(r.review_date) = ? "
					+ "GROUP BY b.business_id "
					+ "ORDER BY b.business_id";

		Connection conn = DBConnect.getConnection();

		try
		{
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, city);
			st.setInt(2, year);
			ResultSet res = st.executeQuery();
			while (res.next())
			{
				Business business = new Business(res.getString("business_id"), res.getString("full_address"),
						res.getString("active"), res.getString("categories"), res.getString("city"),
						res.getInt("review_count"), res.getString("business_name"), res.getString("neighborhoods"),
						res.getDouble("latitude"), res.getDouble("longitude"), res.getString("state"),
						res.getDouble("stars"));
				idMap.putIfAbsent(business.getBusinessId(), business); 
			}
			res.close();
			st.close();
			conn.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public List<Adiacenza> getAdiacenze(String city, Integer year)
	{
		String sql = "SELECT t1.bId as b1, t2.bId as b2, (t1.media-t2.media) AS diff "
						+ "FROM (SELECT b.business_id AS bId, AVG(r.stars) AS media "
						+ "			FROM business b, reviews r "
						+ "			WHERE r.business_id = b.business_id "
						+ "			AND b.city = ? "
						+ "			AND YEAR(r.review_date) = ? "
						+ "			GROUP BY bId "
						+ "			ORDER BY b.business_id) AS t1, "
						+ "		(SELECT b.business_id AS bId, AVG(r.stars) AS media "
						+ "			FROM business b, reviews r "
						+ "			WHERE r.business_id = b.business_id "
						+ "			AND b.city = ? "
						+ "			AND YEAR(r.review_date) = ? "
						+ "			GROUP BY bId "
						+ "			ORDER BY b.business_id) AS t2 "
						+ "WHERE t1.bId < t2.bId "
						+ "GROUP BY b1, b2 "
						+ "HAVING diff <> 0";
	
		Connection conn = DBConnect.getConnection();
		List<Adiacenza> result = new ArrayList<>();
		try
		{
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, city);
			st.setInt(2, year);
			st.setString(3, city);
			st.setInt(4, year);
			ResultSet res = st.executeQuery();
			while (res.next())
			{
				Adiacenza a = new Adiacenza(res.getString("b1"), res.getString("b2"), res.getDouble("diff")); 
				if(!result.contains(a))
					result.add(a); 
			}
			res.close();
			st.close();
			conn.close();
			return result;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
