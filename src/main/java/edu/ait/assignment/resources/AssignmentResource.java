package edu.ait.assignment.resources;

import edu.ait.assignment.dao.OrderDao;
import edu.ait.assignment.models.Order;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

@Path("/orders")
public class AssignmentResource {
    private OrderDao orderDao = new OrderDao();
    @Context
    private UriInfo context;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrder(@PathParam("id") String id) throws SQLException {
        String output = "Jersey say : " + id;
        return Response.status(200).entity(orderDao.getOrder(id)).build();

    }

    @POST
    @Path("/pending")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOrder() throws SQLException {
        UUID uuid = UUID.randomUUID();
        while (!orderDao.orderExists(uuid.toString())){
            uuid = UUID.randomUUID();
        }
        return Response.status(201).header("location", String.format("%s/%s", context.getAbsolutePath(), uuid.toString())).entity(uuid.toString()).build();
    }

    @PUT
    @Path("/pending/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateOrder(@PathParam("id") String id, Order order) throws SQLException {
        if(orderDao.orderExists(id)){
           orderDao.updateOrder(id, order);
        } else {
            orderDao.createOrder(order);
        }
        return Response.status(303).header("location", String.format("%s/%s", context.getAbsolutePath(), id)).entity(id).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateOrder(@PathParam("id") String id) throws SQLException {
        return Response.status(200).entity(orderDao.deleteOrder(id)).build();

    }


}
