package edu.ait.assignment.client;

import edu.ait.assignment.models.Order;
import org.glassfish.jersey.client.ClientProperties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderClient {

    public static void main(String[] args) {
        OrderClient client = new OrderClient();
        Order newOrder = new Order();
        newOrder.setDate(new Date());
        List<String> items = new ArrayList<String>();
        items.add("1");
        items.add("2");
        items.add("3");
        newOrder.setItems(items);

        String id = client.createOrderCall(newOrder).getId();
        Order order = client.getOrderCall(id);
        System.out.println("Order create: " + order);
        order.setDate(new Date(System.currentTimeMillis() + 3600 * 1000));
        Order updatedOrder = client.updateOrderCall(order);
        System.out.println("Order update: " + updatedOrder);
        order = client.getOrderCall(id);
        System.out.println("Get Order updated with GET method: " + order);
        id = client.deleteOrderCall(id);
        System.out.println("Order removed: " + id);
        order = client.getOrderCall(id);
        System.out.println("Order should be null, real value is : " + order);

        //get all orders part
        Order newOrder1 = new Order();
        newOrder1.setDate(new Date());
        items = new ArrayList<String>();
        items.add("11");
        items.add("12");
        items.add("13");
        newOrder1.setItems(items);

        Order newOrder2 = new Order();
        newOrder2.setDate(new Date());
        items = new ArrayList<String>();
        items.add("21");
        items.add("22");
        items.add("23");
        newOrder2.setItems(items);

        Order newOrder3 = new Order();
        newOrder3.setDate(new Date());
        items = new ArrayList<String>();
        items.add("31");
        items.add("32");
        items.add("33");
        newOrder3.setItems(items);
        client.createOrderCall(newOrder1);
        client.createOrderCall(newOrder2);
        client.createOrderCall(newOrder3);

        List<Order> orders = client.getAllOrdersCall();
        System.out.println("All orders: " + orders);

        client.deleteAllOrdersCall();
        orders = client.getAllOrdersCall();
        System.out.println("All orders after deletion: " + orders);
    }


    private List<Order> getAllOrdersCall() {
        try {
            Client client = ClientBuilder.newClient();
            return client.target("http://localhost:8080/rest/orders/").
                    request(MediaType.APPLICATION_JSON).get().readEntity(List.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void deleteAllOrdersCall() {
        try {
            Client client = ClientBuilder.newClient();
            client.target("http://localhost:8080/rest/orders/").
                    request(MediaType.APPLICATION_JSON).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Order createOrderCall(Order order) {
        try {
            Client client = ClientBuilder.newClient();

            Response response
                    = client.target("http://localhost:8080/rest/orders/pending").
                    request(MediaType.APPLICATION_JSON).post(Entity.entity("", MediaType.APPLICATION_JSON));
            String id = response.readEntity(String.class);
            System.out.println(response.getHeaderString(HttpHeaders.LOCATION));
            System.out.println(id);

            response = client.target(response.getHeaderString("location")).property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE).
                    request(MediaType.APPLICATION_JSON).put(Entity.entity(order, MediaType.APPLICATION_JSON));
            return response.readEntity(Order.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private Order updateOrderCall(Order order) {
        try {
            Client client = ClientBuilder.newClient();
            return client.target("http://localhost:8080/rest/orders/pending").path(order.getId()).
                    property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE).
                    request(MediaType.APPLICATION_JSON).put(Entity.entity(order, MediaType.APPLICATION_JSON)).readEntity(Order.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private Order getOrderCall(String id) {
        Client client = ClientBuilder.newClient();
        return client.target("http://localhost:8080/rest/orders/").path(id).
                request(MediaType.APPLICATION_JSON).get(Order.class);
    }

    private String deleteOrderCall(String id) {
        Client client = ClientBuilder.newClient();
        return client.target("http://localhost:8080/rest/orders/").path(id).
                request(MediaType.APPLICATION_JSON).delete(String.class);
    }
}
