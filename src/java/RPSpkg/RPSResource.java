/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RPSpkg;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author JESUSTAKEY
 * RockPaperScissors game
 */
public class RPSResource {

    private String id;

    /**
     * Creates a new instance of RPSResource
     */
    private RPSResource(String id) {
        this.id = id;
    }

    /**
     * Get instance of the RPSResource
     */
    public static RPSResource getInstance(String id) {
        // The user may use some kind of persistence mechanism
        // to store and restore instances of RPSResource class.
        return new RPSResource(id);
    }

    /**
     * Retrieves representation of an instance of RPSpkg.RPSResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXml() {
        //TODO return proper representation object
        //throw new UnsupportedOperationException();
        return "<h1>RockPaperScissors Game.</h1>";
    }

    /**
     * PUT method for updating or creating an instance of RPSResource
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }

    /**
     * DELETE method for resource RPSResource
     */
    @DELETE
    public void delete() {
    }
}
