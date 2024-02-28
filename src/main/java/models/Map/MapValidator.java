package models.Map;

import models.Continent.Continent;
import models.Country.Country;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MapValidator {

    /**
     * Validates the integrity of a map represented by continents and countries.
     * The validation checks include:
     * - Ensuring the map is not empty and contains continents and countries.</li>
     * - Checking if there is at least one country in each continent.</li>
     * - Verifying that the continents form a connected graph.</li>
     * - Ensuring all continents are connected sub-graphs.</li>
     * - Checking for unique country names within the map.</li>
     * If any of the validation checks fail, corresponding error messages are printed.
     *
     *
     * @param p_map The map to be validated, represented as an object of type Map.
     * @return true if the map is valid; false otherwise.
     */
    public static boolean validateMap(Map p_map) {

        // Retrieve continents and countries from the given map
        ArrayList<Continent> l_continents = p_map.getContinents();
        ArrayList<Country> l_countries = p_map.getCountries();

        if(l_continents.isEmpty()){
            System.out.println("\nThe map is currently empty or has not been loaded yet.");
            return false;
        }

        // Check if there is at least one country in a continent
        if(!hasCountriesInContinents(l_continents, l_countries)){
            return false;
        }

        // Check if the continents are connected or not
        if(!isConnectedGraph(l_continents, l_countries)) {
            System.out.println("The map is a disconnected graph.");
            return false;
        }

        // Check if all continents are connected sub-graphs
        if (!areContinentsConnectedSubgraphs(l_continents, l_countries)) {
            System.out.println("One or more continents are disconnected sub-graphs.");
            return false;
        }

        // Check if country names are unique
        if (!areCountryNamesUnique(l_countries)) {
            System.out.println("Duplicate country names found.");
            return false;
        }

        // If all validation checks pass, the map is considered valid
        return true;
    }

    /**
     * Checks whether each continent has at least one country associated with it.
     * If a continent is found without any country, it prints an error message.
     *
     * @param p_continents List of continents to check for the presence of countries.
     * @param p_countries  List of all countries in the map.
     * @return true if each continent has at least one country; false otherwise.
     */
    private static boolean hasCountriesInContinents(ArrayList<Continent> p_continents, ArrayList<Country> p_countries) {
        for (Continent continent : p_continents) {
            boolean hasCountry = false;

            for (Country country : p_countries) {
                if (country.getContinentID() == continent.getID()) {
                    hasCountry = true;
                    break;
                }
            }

            if (!hasCountry) {
                System.out.println("Continent without any country found: " + continent.getName());
                return false;
            }
        }

        return true; // All continents have at least one country
    }


    /**
     * Checks whether the given graph, represented by a collection of continents and countries,
     * forms a connected graph. A connected graph implies that there is at least one connection
     * between countries in different continents.
     *
     * If there is only one continent, it is considered connected by default. Otherwise, it iterates
     * through each continent and checks if there is at least one connection between a country in that
     * continent and a country in another continent.
     *
     * @param p_continents The list of continents in the graph.
     * @param p_countries The list of countries in the graph.
     * @return true if the graph is connected; false otherwise.
     */
    private static boolean isConnectedGraph(ArrayList<Continent> p_continents, ArrayList<Country> p_countries) {

        // If there is only one continent, it is always connected
        if (p_continents.size() == 1) {
            return true;
        }

        // Iterate through each continent
        for (Continent continent : p_continents) {
            boolean isConnected = false;

            // Iterate through each country in the current continent
            for (Country country : p_countries) {
                if (country.getContinentID() == continent.getID()) {
                    // Check if the country has a neighbor in another continent
                    for (Country neighbor : country.getNeighbours()) {
                        if (neighbor.getContinentID() != continent.getID()) {
                            isConnected = true;
                            break;
                        }
                    }

                    if (isConnected) {
                        break;
                    }
                }
            }

            if (!isConnected) {
                System.out.println(continent.getName() + " is not connected.");
                return false;
            }
        }

        return true;
    }


    /**
     * Checks whether all countries within each continent are connected.
     *
     * @param p_continents          List of continents to check for connectivity.
     * @param p_countries           List of all countries in the map.
     * @return true if all continents are connected sub-graphs; false otherwise.
     */
    private static boolean areContinentsConnectedSubgraphs(ArrayList<Continent> p_continents, ArrayList<Country> p_countries) {
        for (Continent continent : p_continents) {
            Set<Country> l_visited = new HashSet<>();
            ArrayList<Country> l_continentCountries = new ArrayList<>();

            // Collect countries belonging to the current continent
            for (Country country : p_countries) {
                if (country.getContinentID() == continent.getID()) {
                    l_continentCountries.add(country);
                }
            }

            // Perform DFS traversal for the continent
            if (!l_continentCountries.isEmpty()) {
                dfsContinent(l_continentCountries.get(0), l_visited, continent.getID());
            }

            // Check if all countries in the continent are visited
            if (l_visited.size() != l_continentCountries.size()) {
                System.out.println("Continent with disconnected sub-graphs found: " + continent.getName());
                System.out.println("Visited countries: " + l_visited.size() + ", Total countries: " + l_continentCountries.size());
                return false;
            }
        }
        return true;
    }

    /**
     * Performs a depth-first search (DFS) traversal of countries within a specific continent.
     * Marks visited countries in the provided set and ensures traversal only within the given continent.
     *
     * @param p_country      The starting country for DFS traversal.
     * @param p_visited      Set of visited countries during DFS traversal.
     * @param p_continentID  ID of the continent being traversed.
     */
    private static void dfsContinent(Country p_country, Set<Country> p_visited, int p_continentID) {
        p_visited.add(p_country);
        for (Country neighbor : p_country.getNeighbours()) {
            if (!p_visited.contains(neighbor) && neighbor.getContinentID() == p_continentID) {
                dfsContinent(neighbor, p_visited, p_continentID);
            }
        }
    }

    /**
     * Checks whether country names in the list are unique.
     *
     * @param p_countries List of countries to check for unique names.
     * @return true if all country names are unique; false otherwise.
     */
    private static boolean areCountryNamesUnique(ArrayList<Country> p_countries) {
        Set<String> l_countryNames = new HashSet<>();
        for (Country country : p_countries) {
            if (!l_countryNames.add(country.getName())) {
                return false;
            }
        }
        return true;
    }
}
