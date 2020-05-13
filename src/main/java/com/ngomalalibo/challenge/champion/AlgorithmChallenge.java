package com.ngomalalibo.challenge.champion;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ngomalalibo.challenge.models.PageResult;
import com.ngomalalibo.challenge.models.User;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class AlgorithmChallenge
{
    private static Client client;
    private static WebTarget target;
    
    private static int threshold;
    
    
    private static String PAGE_URL;
    private static final String API_URL = "https://jsonmock.hackerrank.com/api/article_users/search?page=";
    
    
    protected static void setPageUrl(int pageNumber)
    {
        client = ClientBuilder.newClient();
        PAGE_URL = API_URL + pageNumber;
        target = client.target(PAGE_URL);
    }
    
    /**
     * Answer: Function One
     */
    // get username of most active users with most approved articles
    public static List<String> getUsernames(int threshold)
    {
        log.info(threshold + " most active users based on number of submitted and approved articles are: ");
        log.info("Loading...");
        List<User> completeListOfUsers = (List<User>) getPageResults().get("completeListOfUsers");
        
        return completeListOfUsers.stream().sorted(Comparator.comparingInt(User::getNoOfApprovedArticles).reversed()).limit(threshold).map(d -> d.getUsername() + " (" + d.getNoOfApprovedArticles() + ") ").collect(Collectors.toList());
        
    }
    
    /**
     * Answer: Function Two
     */
    public static String getUsernameWithHighestCommentCount()
    {
        System.out.println("\n\n\n\nUser with most comments");
        List<User> completeListOfUsers = (List<User>) getPageResults().get("completeListOfUsers");
        
        return completeListOfUsers.stream().max(Comparator.comparingInt(User::getTotalNoOfComments)).map(user -> user.getUsername() + " (" + user.getTotalNoOfComments() + ") ").orElseGet(null);
    }
    
    /**
     * Answer: Function Three
     */
    public static List<String> getUsernamesSortedByRecordDate(int threshold)
    {
        log.info(threshold + " most recently created records are: ");
        log.info("Loading...");
        
        List<User> completeListOfUsers = (List<User>) getPageResults().get("completeListOfUsers"); // get list of users from map
        
        // stream users, reverse sort on created date, get pretty printed username and created date
        return completeListOfUsers.stream().sorted(Comparator.comparing(User::getCreatedDate)).map(user -> user.getUsername() + " (" + user.getCreatedDate() + ") ").limit(threshold).collect(Collectors.toList());
    }
    
    
    /**
     * Utility Method
     */
    // get result of all pages
    public static Map<String, List<?>> getPageResults()
    {
        Map<String, List<?>> usersAndPageResults = new HashMap<>(); // map to hold data for pages and for all users
        
        List<PageResult> pageResults = new ArrayList<>();
        List<User> completeListOfUsers = new ArrayList<>();
        
        int totalNoOfPages;
        
        int page = 1; // page counting starts from one
        PageResult pageResult = getPageResult(page); // get result for first page
        totalNoOfPages = getPageResult(page).getTotalNoOfPages(); // get total no of pages from page result
        
        //do..while loop used to get users on all pages starting page page one. Not zero as indicated
        do
        {
            if (pageResult != null)
            {
                pageResults.add(pageResult);
                
                //Display all usernames
                //pageResult.getListOfUsersOnPage().stream().map(DUser::getUsername).forEach(log::info);
            }
            else
            {
                break; // if page result is not returned then end of sequential data is assumed to be reached
            }
            
            // get next page
            page++;
            pageResult = getPageResult(page);
            
        } while (page <= totalNoOfPages); // get more page results while total number has not been reached
        
        
        if (pageResults.size() > 0)
        {
            //pageResults.stream().map(e -> e.getUsersOnPage().stream().sorted(Comparator.comparingInt(DUser::getNoOfApprovedArticles)).limit(threshold).map(DUser::getUsername).collect(Collectors.toList())).collect(Collectors.toList());
            
            // get a list of all users
            pageResults.forEach(e -> completeListOfUsers.addAll(e.getListOfUsersOnPage()));
        }
        usersAndPageResults.put("pageResults", pageResults); // map to store all page results
        usersAndPageResults.put("completeListOfUsers", completeListOfUsers); // map stores all user details
        
        return usersAndPageResults;
    }
    
    /**
     * Utility method
     */
    // get page result
    public static PageResult getPageResult(int pageNumber)
    {
        // provide page number to url
        setPageUrl(pageNumber);
        
        URL url = null;
        
        //get page result with list of users
        PageResult pageResult;
        User user;
        List<User> users = new ArrayList<>();
        
        //date formatter to retrieve updatedDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        
        try
        {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonParser jp = new JsonParser();
            
            // send GET request to retrieve data from API as a request for JSON
            url = new URL(PAGE_URL);
            URLConnection yc = url.openConnection();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("ACCEPT", "application/json");
            
            // log.info("Fetching data... please wait");
            
            // confirm that response is successful with status code 200
            if (conn.getResponseCode() == 200)
            {
                // Create buffered reader to read data
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String output;
                
                JSONObject jsonObject;
                if ((output = in.readLine()) != null)
                {
                    JsonElement je = jp.parse(output);
                    String prettyJsonResponse = gson.toJson(je);
                    // log.info("output -> " + prettyJsonResponse);
                    
                    
                    //retrieving and copying JSON data into Java object using
                    jsonObject = new JSONObject(output);
                    
                    String pageIndex = jsonObject.getString("page");
                    int usersPerPage = jsonObject.getInt("per_page");
                    int totalNoOfUsers = jsonObject.getInt("total");
                    int totalNoOfPages = jsonObject.getInt("total_pages");
                    
                    JSONArray userArray = jsonObject.getJSONArray("data"); // get user as array
                    int count = 0;
                    if (userArray != null)
                    {
                        while (count < userArray.length())
                        {
                            // traverse array and store user info in user object and array
                            user = new User();
                            JSONObject jsonEmbed = userArray.getJSONObject(count);
                            
                            user.setId(jsonEmbed.getInt("id"));
                            user.setUsername(jsonEmbed.getString("username"));
                            user.setAbout(jsonEmbed.getString("about"));
                            user.setNoOfArticles(jsonEmbed.getInt("submitted"));
                            
                            LocalDateTime updatedDateTime = LocalDateTime.parse(jsonEmbed.getString("updated_at"), formatter);
                            user.setLastUpdated(updatedDateTime);
                            
                            user.setNoOfApprovedArticles(jsonEmbed.getInt("submission_count"));
                            user.setTotalNoOfComments(jsonEmbed.getInt("comment_count"));
                            
                            // converting epoch timestamp to LocalDateTime object
                            user.setCreated(jsonEmbed.getLong("created_at"));
                            LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(user.getCreated(), 0, ZoneOffset.UTC);
                            
                            user.setCreatedDate(localDateTime);
                            
                            users.add(user);
                            
                            count++;
                        }
                    }
                    
                    pageResult = new PageResult();
                    pageResult.setPageIndex(pageIndex);
                    pageResult.setUsersPerPage(usersPerPage);
                    pageResult.setTotalNoOfPages(totalNoOfPages);
                    pageResult.setTotalNoOfUsers(totalNoOfUsers);
                    
                    pageResult.setListOfUsersOnPage(users);
                    
                    // pageResults.add(pageResult);
                    
                    
                    return pageResult;
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        
        return null;
    }
    
    /**
     * Start program and run all three functions.
     */
    // Run class to execute three challenges. Provide the threshold when prompted
    public static void main(String[] args)
    {
        //Question 1
        Scanner scanner = new Scanner(System.in);
        log.info("Enter threshold to get active users: ");
        try
        {
            String entry = scanner.next();
            // ensure user enters a numeric threshold
            while (!isInteger(entry))
            {
                
                System.out.println("Re-enter an integer threshold to get most active users (or q to quit): ");
                entry = scanner.next();
                
                // exit application when q is entered
                if (entry.equalsIgnoreCase("q"))
                {
                    System.exit(0);
                }
            }
            threshold = Integer.parseInt(entry);
            List<String> usernames = getUsernames(threshold);
            if (usernames != null)
            {
                usernames.forEach(System.out::println);
            }
            
            
            //Question 2
            String username = getUsernameWithHighestCommentCount();
            log.info("User with most comments -> " + username);
            
            
            //Question 3
            System.out.print("\n\n\n\nEnter threshold to get users sorted by record creation: ");
            try
            {
                entry = scanner.next();
                while (!isInteger(entry))
                {
                    System.out.println("Re-enter an integer threshold to get users sorted by timestamp of record creation (or q to quit): ");
                    entry = scanner.next();
                    
                    if (entry.equalsIgnoreCase("q"))
                    {
                        System.exit(0);
                    }
                }
                threshold = Integer.parseInt(entry);
                usernames = getUsernamesSortedByRecordDate(threshold);
                if (usernames != null)
                {
                    usernames.forEach(System.out::println);
                }
            }
            catch (NumberFormatException e)
            {
                e.getMessage();
            }
        }
        catch (NumberFormatException e)
        {
            e.getMessage();
        }
    }
    
    
    // utility method to check is string is a number
    private static boolean isInteger(String s)
    {
        try
        {
            Integer.parseInt(s);
            return true;
        }
        catch (NumberFormatException ex)
        {
            return false;
        }
    }
}
