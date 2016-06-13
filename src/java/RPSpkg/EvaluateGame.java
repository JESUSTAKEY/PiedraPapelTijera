/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RPSpkg;
import javax.xml.bind.annotation.*;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
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
@Path("/resultSingle")
//(/api/championship/resultSingle)
public class EvaluateGame {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of RPSsResource
     */
    public EvaluateGame() {
    }
    
    public static String[] evaluateSingleGame(String[] play1, String[] play2){
        System.out.println("Evaluando juego simple...");
        play1[1] = play1[1].toUpperCase();
        play2[1] = play2[1].toUpperCase();
        String game = play1[1] + play2[1]; //Get the strategies
        String exception = "Strategy type exception";// '" + game + "'";
        String[] winner = {exception, "error"};
        switch(game){

            // If both players use the same strategy, the first player is the winner.
            case "RR" :
            case "PP" :
            case "SS" :
                    winner = play1;
                    break;

            // Paper beats Rock
            case "PR" :
                    winner = play1;
                    break;
            case "RP" :
                    winner = play2;
                    break;

            //Rock beats Scissors
            case "RS" :
                    winner = play1;
                    break;
            case "SR" :
                    winner = play2;
                    break;

            //Scissors beats Paper
            case "SP" :
                    winner = play1;
                    break;
            case "PS" :
                    winner = play2;
                    break;

            //Invalid strategy				
            //default :
               //winner = {"Strategy type exception", "error"};
        }
        System.out.println("Resultado: \"['" + winner[0].toString() + "', '" + winner[1].toString() + "']\".");
        return winner;
    }
    
    public String formatAndEvaluate(String game){
        String result = "";
        String[] res = {};
        String formatMove = game;
        //formatMove = formatMove.replace('[', '(');
        //formatMove = formatMove.replace(']', ')');
        formatMove = formatMove.replace('[', ' ');
        formatMove = formatMove.replace(']', ' ');
        formatMove = formatMove.replace('\'', ' ');
        formatMove = formatMove.replace('\"', ' ');
        formatMove = formatMove.trim();
        String[] sections = {};
        sections = formatMove.split(",");
        if(sections.length == 4){
            String[] player1 = {sections[0].trim(), sections[1].trim()};
            String[] player2 = {sections[2].trim(), sections[3].trim()};
            res = evaluateSingleGame(player1, player2);
        }
        if(res.length == 2){
            result = "[\"" + res[0].toString() + "\", \"" + res[1].toString() + "\"]";
        }
        else{
            result = "[\"Line exception\", \"error\"]";
        }
        return result;
    }
    
    public String[] getSplitRegExp(int depth){
        depth--;
        String left = "";
        String right = "";
        String first = "";
        String last = "";
        while(depth > 0){
            left += "\\]";
            right += "\\[";
            first += "]";
            last += "[";
            depth--;
        }
        String[] res = {(left + "," + right),first,last};
        return res;
    }
    
    public String evaluateTournament(String game){
        System.out.println("Evaluando torneo con: \"" + game + "\".");
        int i = 0;
        int depth = 0;
        
        //Delete extra white characters
        game = game.replaceAll("\\s*\\[","[");
        game = game.replaceAll("\\]\\s*","]");
        game = game.replaceAll("'\\s*,\\s*'","', '");
        game = game.replaceAll("\"\\s*,\\s*\"","\", \"");

        while (i<game.length()) {
            if(game.charAt(i) == '['){
                depth++;
                i++;
            }
            else if(game.charAt(i) == ' '){
                i++;
            }
            else{
                i = game.length();
            }
        }
        System.out.println("Profundidad: " + depth + ".");
        if(depth < 2){
            System.out.println("ERROR: Profundidad menor a 2.");
            return "[\"Tournament definition exception\", \"error\"]";
        }
        if(depth == 2){
            System.out.println("Se evaluara juego simple: \"" + game + "\".");
            return formatAndEvaluate(game);
        }
        else{
            System.out.println("Se divide el torneo en subtorneos.");
            String[] splitRegExp = getSplitRegExp(depth);
            String[] subTournament = game.split(splitRegExp[0]);
            String subTournamentA = subTournament[0] + splitRegExp[1];//Add brackets loosed in split
            subTournamentA = subTournamentA.replaceAll("^\\[","");//delete first [
            System.out.println("SubTorneoA: \"" + subTournamentA + "\".");
            String subTournamentB = splitRegExp[2] + subTournament[1];//Add brackets loosed in split
            subTournamentB = subTournamentB.replaceAll("\\]$","");//delete last ]
            System.out.println("SubTorneoB: \"" + subTournamentB + "\".");
            
            return evaluateTournament("[" + evaluateTournament(subTournamentA) + "," + evaluateTournament(subTournamentB) + "]");
        }
        //return "[\"Unknown exception\", \"error\"]";
    }

    /**
     * Retrieves representation of an instance of RPSpkg.EvaluateGame
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("text/xml")//("text/html")
    public String getXml(@QueryParam("game") String game) {
        String winner = "";
        if(game == null){
            winner = "No valid game. Add 2 players and the respective moves.";
        }
        //winner = formatAndEvaluate(game);
        winner = evaluateTournament(game);
        return "<html lang=\"en\"><body>"
                + "<h1>" + winner + "</h1>"
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
