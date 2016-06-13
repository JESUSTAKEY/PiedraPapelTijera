/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RPSpkg;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.xml.bind.annotation.*;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author JESUSTAKEY
 * RockPaperScissors game
 */
@Path("/top")
//(/api/championship/top)
public class RPSsResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of RPSsResource
     */
    public RPSsResource() {
    }

    /**
     * Retrieves representation of an instance of RPSpkg.RPSsResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("text/xml")//("text/html")
    public String getXml(@QueryParam("count") String count) {
        if(count == null){
            count = "10";
        }
        
        String res = "";
        
        
        
        //Database connection
        try {
            String host = "jdbc:derby://localhost:1527/RPSwinners";
            String username = "jesus";
            String password = "rpspass";
            System.out.println("Abriendo conexion getConnection("+host+", " + username + ", "+ password + ")");
            Connection con = DriverManager.getConnection( host, username, password );
            Statement stmt = con.createStatement( );
            
            //String SQL = "SELECT TOP " + count + " * FROM winners ORDER BY points DESC";
            String SQL = "SELECT * FROM winners ORDER BY points DESC";
            System.out.println(SQL);
            ResultSet rs = stmt.executeQuery( SQL );
            
            int n = 1;
            while(rs.next()&&n<=Integer.parseInt(count)){//Recorre el resultado por rows
                String playerFound = rs.getString("Name");
                int playerPoints = rs.getInt("Points");
                System.out.println(n + " - " + playerFound + ". " + playerPoints + " points.");
                res = res + "<p>" + n + " - " + playerFound + ". " + playerPoints + " points." + "</p>";
                n++;
            }
        }
        catch ( SQLException err ) {
        System.out.println( err.getMessage( ) );
        }
        
        
        
        
        return "<html lang=\"en\"><body>"
                + "<h1>Top " + count + " winners</h1>"
                + res
                + "<a href=\"http://localhost:20483/api\">Home</a>"
                + "</body></html>";
        //throw new UnsupportedOperationException();
    }
    
    /**
     * POST method for creating an instance of RPSResource
     * @param content representation for the new resource
     * @return an HTTP response with content of the created resource
     */
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response postXml(String content) {
        //TODO
        //return Response.ok().build();
        return Response.created(context.getAbsolutePath()).build();
    }

    /**
     * Sub-resource locator method for {id}
     */
    @Path("{id}")
    public RPSResource getRPSResource(@PathParam("id") String id) {
        return RPSResource.getInstance(id);
    }
}
