package com.saintsrobotics;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class ScoutingParser {

    public static void main(String[] args) throws FileNotFoundException {
        JSONObject teams = new JSONObject(new JSONTokener(new FileInputStream("frc-scouting-export.json")))
                .getJSONObject("Teams");
        
        StringBuilder output = new StringBuilder();
        output.append(",Bins Moved,")
                .append("Moved,")
                .append("Totes Moved,")
                .append("Totes Stacked,")
                .append("Litter Chuted,")
                .append("Litter Landfilled,")
                .append("Totes Chuted,")
                .append("Litter Binned,")
                .append("Totes Stacked,")
                .append("Num Bins,")
                .append("Max Bin Height\n");
        
        Iterator<String> teamIter = teams.keys();
        
        String[] teamArr = teams.keySet().toArray(new String[0]);
        Arrays.sort(teamArr, (String s1, String s2) -> {
            return Integer.compare(Integer.valueOf(s1), Integer.valueOf(s2));
        });
        
        for (String teamNum : teamArr) {
            try {
                JSONObject team = (JSONObject) teams.get(teamNum);
                JSONObject matches = team.getJSONObject("Matches").getJSONObject("Auburn");

                output.append(teamNum).append("\n");

                Iterator<String> matchIter = matches.keys();
                String[] matchArr = matches.keySet().toArray(new String[0]);
                Arrays.sort(matchArr, (String s1, String s2) -> {
                    s1 = s1.substring(1);
                    s2 = s2.substring(1);
                    return Integer.compare(Integer.valueOf(s1), Integer.valueOf(s2));
                });
                for (String matchNum : matchArr){
                    output.append(matchNum);
                    try {
                        JSONObject match = (JSONObject) matches.get(matchNum);
                        try {output.append(",").append(match.getJSONObject("Auton").getInt("binsMoved")); } catch(JSONException e) { }
                        try {output.append(",").append(match.getJSONObject("Auton").getBoolean("moved")); } catch(JSONException e) { }
                        try {output.append(",").append(match.getJSONObject("Auton").getInt("totesMoved")); } catch(JSONException e) { }
                        try {output.append(",").append(match.getJSONObject("Auton").getInt("totesStacked")); } catch(JSONException e) { }
                        JSONObject teleop = match.getJSONObject("teleop");
                        try {output.append(",").append(teleop.getJSONObject("human").getInt("litterChuted")); } catch(JSONException e) { }
                        try {output.append(",").append(teleop.getJSONObject("human").getInt("litterThrownLandfill")); } catch(JSONException e) { }
                        try {output.append(",").append(teleop.getJSONObject("human").getInt("totesChuted")); } catch(JSONException e) { }
                        try {output.append(",").append(teleop.getJSONObject("litter").getInt("binned")); } catch(JSONException e) { }
                        try {output.append(",").append(teleop.getJSONObject("totes").getInt("count")); } catch(JSONException e) { }
                        output.append(",").append(teleop.getJSONObject("bins").length());
                        int max = 0;
                        for (String s : teleop.getJSONObject("bins").keySet()) {
                            if (teleop.getJSONObject("bins").getInt(s) > max)
                                max = teleop.getJSONObject("bins").getInt(s);
                        }
                        output.append(",").append(max);
                    } catch (JSONException e) {}
                    output.append("\n");
                }
            } catch (JSONException e) {
                System.out.println(e);
            }
            output.append("\n");
        }
        
        PrintWriter writer = new PrintWriter("processed.csv");
        writer.println(output.toString());
        writer.close();
    }
}
