package edu.ait.assignment.dao;

import edu.ait.assignment.models.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    public String createOrder(String id, Order order) throws SQLException {
        Connection con = getConnection();
        PreparedStatement statement = con.prepareStatement("INSERT INTO order_tbl(id, order_date, items) VALUES (?, ?, ?)");
        statement.setString(1, id);
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
        return id;
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

    public List<Order> getAllOrders() throws SQLException {
        Connection con = getConnection();
        PreparedStatement statement = con.prepareStatement("select * from order_tbl");
        ResultSet res =  statement.executeQuery();
        List<Order> orders = new ArrayList<>();
        while (res.next()){
            Order o =  new Order();
            o.setId(res.getString(1));
            o.setDate(res.getDate(2));
            o.setItems(Arrays.asList(res.getString(3).split(",")));
            orders.add(o);

        }return orders;
    }

    public String deleteOrder(String orderId) throws SQLException {
        Connection con = getConnection();
        PreparedStatement statement = con.prepareStatement("DELETE from order_tbl where id = ?");
        statement.setString(1, orderId);
        statement.executeUpdate();
        return orderId;
    }

    public void deleteAllOrders() throws SQLException {
        Connection con = getConnection();
        PreparedStatement statement = con.prepareStatement("DELETE from order_tbl");
        statement.executeUpdate();
    }
}
