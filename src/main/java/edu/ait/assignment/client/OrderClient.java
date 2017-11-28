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

    private static String REST_URI = "http://localhost:8080/rest/orders/";

    public static void main(String[] args) {
        OrderClient client = new OrderClient();
        Order newOrder = new Order();
        newOrder.setDate(new Date());
        List<String> items = new ArrayList<String>();
        items.add("1");
        items.add("2");
        items.add("3");
        newOrder.setItems(items);
        //Create order using POST-PUT technique described in article
        String id = client.createOrderCall(newOrder).getId();
        //Get created order
        Order order = client.getOrderCall(id);
        System.out.println("Order create: " + order);
        order.setDate(new Date(System.currentTimeMillis() + 3600 * 1000));
        //Update time of the order
        Order updatedOrder = client.updateOrderCall(order);
        System.out.println("Order update: " + updatedOrder);
        //get Updated order
        order = client.getOrderCall(id);
        System.out.println("Get Order updated with GET method: " + order);
        //delete Order
        id = client.deleteOrderCall(id);
        System.out.println("Order removed: " + id);
        //Check that order is deleted
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
        //create 3 orders
        client.createOrderCall(newOrder1);
        client.createOrderCall(newOrder2);
        client.createOrderCall(newOrder3);

        //get all arders
        List<Order> orders = client.getAllOrdersCall();
        System.out.println("All orders: " + orders);

        //delete all orders
        client.deleteAllOrdersCall();
        //check that all orders deleted
        orders = client.getAllOrdersCall();
        System.out.println("All orders after deletion: " + orders);
    }


    /**
     * Get request for all orders
     * @return
     */
    private List<Order> getAllOrdersCall() {
        try {
            Client client = ClientBuilder.newClient();
            return client.target(REST_URI).
                    request(MediaType.APPLICATION_JSON).get().readEntity(List.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Delete request for all orders
     */
    private void deleteAllOrdersCall() {
        try {
            Client client = ClientBuilder.newClient();
            client.target(REST_URI).
                    request(MediaType.APPLICATION_JSON).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method uses post to receive unique link via POST request and then uses PUT to create or Update object
     * @param order
     * @return
     */
    private Order createOrderCall(Order order) {
        try {
            Client client = ClientBuilder.newClient();

            Response response
                    = client.target(REST_URI +"pending").
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

    /**
     * Method Uses PUT to update Order
     * @param order
     * @return
     */
    private Order updateOrderCall(Order order) {
        try {
            Client client = ClientBuilder.newClient();
            return client.target(REST_URI + "pending").path(order.getId()).
                    property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE).
                    request(MediaType.APPLICATION_JSON).put(Entity.entity(order, MediaType.APPLICATION_JSON)).readEntity(Order.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Get request for specific order
     * @param id
     * @return
     */
    private Order getOrderCall(String id) {
        Client client = ClientBuilder.newClient();
        return client.target(REST_URI).path(id).
                request(MediaType.APPLICATION_JSON).get(Order.class);
    }

    /***
     * delete request for specific order
     * @param id
     * @return
     */
    private String deleteOrderCall(String id) {
        Client client = ClientBuilder.newClient();
        return client.target(REST_URI).path(id).
                request(MediaType.APPLICATION_JSON).delete(String.class);
    }
}
