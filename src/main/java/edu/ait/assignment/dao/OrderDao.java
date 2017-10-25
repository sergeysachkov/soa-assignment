package edu.ait.assignment.dao;

import edu.ait.assignment.models.Order;

import java.sql.*;
import java.util.Arrays;
import java.util.UUID;

public class OrderDao {

    static {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver" );
            Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:soadb", "SA", "");
            c.createStatement().executeUpdate("DROP TABLE IF EXISTS order_tbl; CREATE TABLE order_tbl (id VARCHAR(50) NOT NULL ," +
                    "order_date DATE, " +
                    "items VARCHAR(255)," +
                    " PRIMARY KEY(id));");
        } catch (Exception e) {
            System.err.println("ERROR: failed to load HSQLDB JDBC driver.");
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver" );
            return DriverManager.getConnection("jdbc:hsqldb:mem:soadb", "SA", "");
        } catch (Exception e) {
            System.err.println("ERROR: failed to load HSQLDB JDBC driver.");
            e.printStackTrace();
            throw new RuntimeException("Driver Not found!");
        }
    }

    public boolean orderExists(String orderId) throws SQLException {
        Connection con = getConnection();
        PreparedStatement statement = con.prepareStatement("select * from order_tbl where id = ?");
        statement.setString(1, orderId);
        ResultSet res =  statement.executeQuery();
        return res.next();
    }

    public String createOrder(Order order) throws SQLException {
        Connection con = getConnection();
        UUID id = UUID.randomUUID();
        PreparedStatement statement = con.prepareStatement("INSERT INTO order_tbl(id, order_date, items) VALUES (?, ?, ?)");
        statement.setString(1, id.toString());
        statement.setDate(2, new java.sql.Date(order.getDate().getTime()));
        StringBuffer str = new StringBuffer();
        int index = 0;
        for(String item : order.getItems()){
            if(index != 0){
                str.append(",");
            }
            str.append(item);
            index++;
        }
        statement.setString(3, str.toString());
        statement.executeUpdate();
        return id.toString();
    }

    public String updateOrder(String id, Order order) throws SQLException {
            Connection con = getConnection();
            PreparedStatement statement = con.prepareStatement("UPDATE order_tbl set order_date = ?, items = ? where id = ?");
            statement.setDate(1, new java.sql.Date(order.getDate().getTime()));
            StringBuffer str = new StringBuffer();
            int index = 0;
            for(String item : order.getItems()){
                if(index != 0){
                    str.append(",");
                }
                str.append(item);
                index++;
            }
            statement.setString(2, str.toString());
            statement.setString(3, id);
        statement.executeUpdate();
            return id;
    }

    public Order getOrder(String orderId) throws SQLException {
        Connection con = getConnection();
        PreparedStatement statement = con.prepareStatement("select * from order_tbl where id = ?");
        statement.setString(1, orderId);
        ResultSet res =  statement.executeQuery();
        if(res.next()){
            Order o =  new Order();
            o.setId(res.getString(1));
            o.setDate(res.getDate(2));
            o.setItems(Arrays.asList(res.getString(3).split(",")));
            return o;
        }else {
            return null;
        }
    }

    public String deleteOrder(String orderId) throws SQLException {
        Connection con = getConnection();
        PreparedStatement statement = con.prepareStatement("DELETE from order_tbl where id = ?");
        statement.setString(1, orderId);
        statement.executeUpdate();
        return orderId;
    }
}
