package edu.ait.assignment.resources;

import edu.ait.assignment.dao.OrderDao;
import edu.ait.assignment.models.Order;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.net.URISyntaxException;
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
        try {
            String output = "Jersey say : " + id;
            return Response.status(Response.Status.OK).entity(orderDao.getOrder(id)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage()).build();
        }

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllOrder() throws SQLException {
        try {
            return Response.status(Response.Status.OK).entity(orderDao.getAllOrders()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletetAllOrder() throws SQLException {
        try {
            orderDao.deleteAllOrders();
            return Response.status(Response.Status.OK).build();
        } catch (Exception e) {
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/pending")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOrder() throws SQLException {
        try {
            UUID uuid = UUID.randomUUID();
            while (orderDao.orderExists(uuid.toString())) {
                uuid = UUID.randomUUID();
            }
            return Response.status(Response.Status.CREATED).header(HttpHeaders.LOCATION, String.format("%s/%s", context.getAbsolutePath(), uuid.toString())).entity(uuid.toString()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/pending/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateOrder(@PathParam("id") String id, Order order) throws SQLException, URISyntaxException {
        try {
            if (orderDao.orderExists(id)) {
                orderDao.updateOrder(id, order);
            } else {
                orderDao.createOrder(id, order);
            }
            return Response.status(303).header("location", String.format("%s%s/%s", context.getBaseUri(), "orders", id)).allow("GET").entity(id).build();
            //return Response.seeOther(URI.create(String.format("%s%s/%s", context.getBaseUri(), "orders", id))).build();
        } catch (Exception e) {
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateOrder(@PathParam("id") String id) throws SQLException {
        try {
            return Response.status(Response.Status.OK).entity(orderDao.deleteOrder(id)).entity(id).build();
        } catch (Exception e) {
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage()).build();
        }

    }


}
