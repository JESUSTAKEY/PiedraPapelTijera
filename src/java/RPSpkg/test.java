/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RPSpkg;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
/**
 *
 * @author JESUSTAKEY
 */
@Path("/test")
public class test {
    
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
    
    
    @POST
    //@Path("/it")
    @Produces("text/html")//("text/html")
    public Response responseMsg(@FormParam("file") String file ) throws FileNotFoundException, IOException {
        System.out.println("File: \"" + file + "\".");
        String output = "<div><p style=\"color:#B97332\"><Strong>Input file: </Strong></p><p>\"" + file + "\".</p><p>&nbsp;</p>";
        String fileData;
        BufferedReader br = new BufferedReader(new FileReader(file));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                //sb.append(System.lineSeparator());
                line = br.readLine();
            }
            fileData = sb.toString();
        } finally {
            br.close();
        }
        
        output = output + "<p style=\"color:#B97332\"><Strong>Tournament description in file: </Strong></p><p>" + fileData +"</p><p>&nbsp;</p>";
        
        String winner = "";
                if(fileData == null){
                    winner = "No valid game. Add 2 players and the respective moves.";
                }
                else{
                    winner = evaluateTournament(fileData);
                }
                output = output + "<p style=\"color:#B97332\"><Strong>Resultado: </Strong></p><p>" + winner + "</p></div>";
                output = output + "<p>&nbsp;</p><p><a href=\'http://localhost:20483/api\'>Home</a></p>";
                
                
        String player = winner;
        player = player.replaceAll("\\s*\\[","[");
        player = player.replaceAll("\\]\\s*","]");
        player = player.replaceAll("'\\s*,\\s*'","', '");
        player = player.replaceAll("\"\\s*,\\s*\"","\", \"");
        player = player.replaceAll("\\[","");
        player = player.replaceAll("\\]","");
        String[] playerInfo = player.split(",");
        player = playerInfo[0];
        player = player.replaceAll("'","");
        player = player.replaceAll("\"","");
        String savePoints = playerInfo[1].toUpperCase();
        
        int playerPoints = 3;
        int pointsPosition = 3;
        //Database connection
        if(!savePoints.equals("ERROR")){
            try {
                String host = "jdbc:derby://localhost:1527/RPSwinners";
                String username = "jesus";
                String password = "rpspass";
                System.out.println("Abriendo conexion getConnection("+host+", " + username + ", "+ password + ")");
                Connection con = DriverManager.getConnection( host, username, password );
                Statement stmt = con.createStatement( );

                String SQL = "SELECT * FROM winners where name = '" + player + "'";
                ResultSet rs = stmt.executeQuery( SQL );

                if(rs.next()){//Check if querry returns value
                    String playerFound = rs.getString("Name");
                    System.out.println("Jugador si existia (" + playerFound + "), se actualizan los puntos...");
                    playerPoints = rs.getInt("points");//Obtain actual points
                    playerPoints += pointsPosition;

                    System.out.println("UPDATE winners SET points=" + playerPoints + " WHERE Name='" + player + "'");
                    SQL = "UPDATE winners SET points=" + playerPoints + " WHERE Name='" + player + "'";
                    stmt.execute( SQL );
                }
                else{
                    System.out.println("Jugador no existia, se agrega...");
                    SQL = "INSERT INTO winners (Name,Points) VALUES ('" + player + "'," + playerPoints + ")";
                    stmt.execute( SQL );
                }
            }
            catch ( SQLException err ) {
            System.out.println( err.getMessage( ) );
            }
        }
        return Response.status(200).entity(output).build();
    }
}
