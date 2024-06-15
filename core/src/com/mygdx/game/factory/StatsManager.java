package com.mygdx.game.factory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class StatsManager {
    private static final String FILE_PATH = "stats.txt";
    private final Map<String, CharacterStats> characterStatsMap = new HashMap<>();

    public StatsManager() {
        loadStats();
    }

    public synchronized void incrementGamesPlayed(String characterName) {
        CharacterStats stats = characterStatsMap.getOrDefault(characterName, new CharacterStats(characterName, 0, 0));
        stats.incrementGamesPlayed();
        characterStatsMap.put(characterName, stats);
        saveStats();
    }

    public synchronized void incrementWins(String characterName) {
        CharacterStats stats = characterStatsMap.getOrDefault(characterName, new CharacterStats(characterName, 0, 0));
        stats.incrementWins();
        characterStatsMap.put(characterName, stats);
        saveStats();
    }

    private synchronized void loadStats() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    System.out.println("File created: " + file.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 3) {
                    String name = parts[0];
                    int gamesPlayed = Integer.parseInt(parts[1]);
                    int wins = Integer.parseInt(parts[2]);
                    characterStatsMap.put(name, new CharacterStats(name, gamesPlayed, wins));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void saveStats() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (CharacterStats stats : characterStatsMap.values()) {
                writer.write(stats.getName() + " " + stats.getGamesPlayed() + " " + stats.getWins());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class CharacterStats {
        private String name;
        private int gamesPlayed;
        private int wins;

        public CharacterStats(String name, int gamesPlayed, int wins) {
            this.name = name;
            this.gamesPlayed = gamesPlayed;
            this.wins = wins;
        }

        public String getName() {
            return name;
        }

        public int getGamesPlayed() {
            return gamesPlayed;
        }

        public void incrementGamesPlayed() {
            this.gamesPlayed++;
        }

        public int getWins() {
            return wins;
        }

        public void incrementWins() {
            this.wins++;
        }
    }
}