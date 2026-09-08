package org.example;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.json.JSONObject;

import java.awt.*;

public class MazeConfigManager {
    private String wallCellColor;
    private String pathCellColor;
    private boolean drawGrid;
    private String gridColor;
    private int animationDelayMS;

    public MazeConfigManager() { //Empty on purpose. Data is populated later from the server to keep object creation fast and safe.
    }

    /**
     * Fetches the maze rendering configuration from the server via a GET request.
     * Parses the JSON response (colors, grid settings, and animation delay)
     * and stores the values directly in the class fields.
     */
    public void fetchConfigFromServer() {
        try {
            HttpResponse<String> response = Unirest.get("https://shaitest-production-3066.up.railway.app/fm1/get-render-config")
                    .asString();
            JSONObject jsonObject = new JSONObject(response.getBody());
            this.wallCellColor = jsonObject.getString("wallCellColor");
            this.pathCellColor = jsonObject.getString("pathColor");
            this.drawGrid = jsonObject.getBoolean("drawGrid");
            this.gridColor = jsonObject.getString("gridColor");
            this.animationDelayMS = jsonObject.getInt("animationDelayMs");

            System.out.println("Config loaded successfully!");

        } catch (kong.unirest.UnirestException exception) {
            System.out.println("Network error: Could not reach the server.");
            exception.printStackTrace();
        } catch (org.json.JSONException exception) {
            System.out.println("Parsing error: Failed to read JSON structure.");
            exception.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public Color getWallCellColorAsColor() { // Cast the String code of the wall cell to Color Object
        return Color.decode(wallCellColor);
    }

    public Color getPathColorAsColor() { // Cast the String code of the path cell to Color Object
        return Color.decode(pathCellColor);
    }

    public Color getGridColorAsAColor() { // Cast the String code of the Grid cell to Color Object
        return Color.decode(gridColor);
    }

    public boolean isDrawGrid() {
        return drawGrid;
    }

    public int getAnimationDelayMS() {
        return this.animationDelayMS;
    }

    public String getWallCellColorText() {
        return this.wallCellColor;
    }

    public String getPathCellColorText() {
        return this.pathCellColor;
    }

    public String getGridColorText() {
        return this.gridColor;
    }
}

