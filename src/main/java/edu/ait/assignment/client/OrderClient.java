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
             String id = client.createOrderCall().getId();
             Order order = client.getOrderCall(id);
             System.out.println("Order create: " + order);
             order.setDate(new Date(System.currentTimeMillis() + 3600 * 1000));
             client.updateOrderCall(order);
            System.out.println("Order update: " + order);
            order = client.getOrderCall(id);
            id = client.deleteOrderCall(id);
            System.out.println("Order removed: " + id);
            order = client.getOrderCall(id);
            System.out.println("Order should be null, real value is : " + order);
        }


        private Order createOrderCall(){
            try {
                Client client = ClientBuilder.newClient();

                Response response
                        = client.target("http://localhost:8080/rest/orders/pending").
                        request(MediaType.APPLICATION_JSON).post(Entity.entity("", MediaType.APPLICATION_JSON));
                String id  = response.readEntity(String.class) ;
                System.out.println(response.getHeaderString(HttpHeaders.LOCATION));
                System.out.println(id);
                Order order = new Order();
                order.setDate(new Date());
                List<String> items = new ArrayList<String>();
                items.add("1");
                items.add("2");
                items.add("3");
                order.setItems(items);
                response = client.target(response.getHeaderString("location")).property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE).
                        request(MediaType.APPLICATION_JSON).put(Entity.entity(order, MediaType.APPLICATION_JSON));
                return response.readEntity(Order.class);
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }
    private String updateOrderCall(Order order){
        try {
            Client client = ClientBuilder.newClient();
            return client.target("http://localhost:8080/rest/orders/pending").path(order.getId()).
                    request(MediaType.APPLICATION_JSON).put(Entity.entity(order, MediaType.APPLICATION_JSON)).readEntity(String.class);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    private Order getOrderCall(String id){
            Client client = ClientBuilder.newClient();
            return client.target("http://localhost:8080/rest/orders/").path(id).
                    request(MediaType.APPLICATION_JSON).get(Order.class);
    }

    private String deleteOrderCall(String id){
        Client client = ClientBuilder.newClient();
        return client.target("http://localhost:8080/rest/orders/").path(id).
                request(MediaType.APPLICATION_JSON).delete(String.class);
    }
}
