package edu.ait.assignment.resources;

import edu.ait.assignment.dao.OrderDao;
import edu.ait.assignment.models.Order;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/order")
public class AssignmentResource {
    OrderDao orderDao;

    @GET
    @Path("/{id}")
    public Response getOrder(@PathParam("id") String id) {

        String output = "Jersey say : " + id;

        return Response.status(200).entity(orderDao.getOrder(id)).build();

    }

    @POST
    @Path("/")
    public Response createOrder() {
        return Response.status(200).entity(orderDao.createEmptyOrder()).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateOrder(@PathParam("id") String id, Order order) {
        if(orderDao.orderExists(id)){
            return Response.status(200).entity(orderDao.updateOrder(order)).build();
        } else {
            return Response.status(200).entity(orderDao.createOrder(order)).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response updateOrder(@PathParam("id") String id) {
        return Response.status(200).entity(orderDao.deleteOrder(id)).build();

    }


}
