package com.ngomalalibo.challenge.maximus;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class MaximumPairs
{
    private static Scanner scanner = new Scanner(System.in);
    
    private static int maximumPairs = 0;
    
    private static int noOfWashes;
    private static List<Colors> cleanPile = new ArrayList<Colors>();
    private static List<Colors> dirtyPile = new ArrayList<Colors>();
    
    private static Map<Colors, Long> cleanSockTotalMap = new HashMap<Colors, Long>();
    private static Map<Colors, Long> dirtySockTotalMap = new HashMap<Colors, Long>();
    
    /**
     * Answer: Get Maximum number of Pairs Anna can take on her trip
     */
    private static int getMaximumPair()
    {
        //
        for (Colors color : Colors.values())
        {
            // organizing the pile of clean socks in the map. The colors and total and stored in the map
            Long cleanSockCount = cleanPile.stream().filter(sock -> sock == color).count();
            cleanSockTotalMap.put(color, cleanSockCount);
            
            // organizing the pile of dirty socks in the map. The colors and total and stored in the map
            Long dirtySockCount = dirtyPile.stream().filter(d -> d == color).count();
            dirtySockTotalMap.put(color, dirtySockCount);
        }
        
        //we select what to wash in order to have maximum pairs
        selectSocksToWash();
        
        // we proceed to gather the pairs from all the colors we have after washing
        for (Colors c : Colors.values())
        {
            Long totalClean = cleanSockTotalMap.get(c);
            maximumPairs += totalClean / 2;
        }
        
        return maximumPairs;
    }
    
    /**
     * Run method to start program
     */
    public static void main(String[] args)
    {
        log.info("Enter no of washes (K): ");
        
        noOfWashes = scanner.nextInt();
        
        //adding sample sock to the clean and dirty piles
        cleanPile.addAll(Arrays.asList(Colors.GREEN, Colors.BLUE, Colors.GREEN, Colors.GREEN));
        dirtyPile.addAll(Arrays.asList(Colors.GREEN, Colors.YELLOW, Colors.RED, Colors.BLUE, Colors.YELLOW));
        
        // get maximum pair of socks
        int maximumPair = getMaximumPair();
        log.info("The maximum number of pair of socks that Anna can take on the trip is " + maximumPair);
        
    }
    
    
    /**
     * [GREEN, BLUE, GREEN, GREEN]
     * [GREEN, YELLOW, RED, BLUE, YELLOW]
     */
    // Select which socks to wash to ensure maximum pairs are available to Anna
    public static void selectSocksToWash()
    {
        Set<Colors> dirtyColors = dirtySockTotalMap.keySet(); // get of colors in the dirty pile
        
        // washing socks from the dirty pile to match single socks in clean pile
        cleanSockTotalMap.keySet().forEach(color ->
                                           {
                                               // check for single clean socks to wash and check if there is a dirty socks of same color. Then wash
                                               Long totalClean = cleanSockTotalMap.get(color);
                                               if (totalClean % 2 != 0)
                                               {
                                                   // check if there are dirty socks that match the same color
                                                   Long totalDirty = dirtySockTotalMap.get(color);
                                                   if (totalDirty != null && totalDirty > 0)
                                                   {
                                                       //wash dirty socks that match
                                                       if (noOfWashes > 0)
                                                       {
                                                           wash(color);
                                                       }
                                                   }
                                               }
                                           });
        
        // checking dirty socks for pairs on order to wash them. ensuring that we can wash at least twice
        if (noOfWashes >= 2)
        {
            // finding matching colors in dirty pile to wash
            dirtySockTotalMap.keySet().forEach(color ->
                                               {
                                                   // get no of dirty socks for each color
                                                   Long dirtyTotal = dirtySockTotalMap.get(color);
                
                                                   //ensure there is a total above two to pair
                                                   if (dirtyTotal != null && dirtyTotal >= 2)
                                                   {
                                                       // get no of pairs to wash from total
                                                       long noOfPairs = dirtyTotal / 2;
                                                       for (int i = 0; i < noOfPairs; i++)
                                                       {
                                                           if (noOfWashes > 0)
                                                           {
                                                               // wash a pair
                                                               wash(color);
                                                               wash(color);
                                                           }
                                                       }
                                                   }
                                               });
        }
    }
    
    // Move socks between clean and dirty piles
    public static void wash(Colors sock)
    {
        Long noOfDirtySock = dirtySockTotalMap.get(sock); // get total dirty socks for selected color from map
        
        // ensure there is a value above zero
        if (noOfDirtySock != null && noOfDirtySock > 0)
        {
            dirtySockTotalMap.put(sock, --noOfDirtySock); //remove dirty socks from dirty pile record
            Long noOfCleanSock = cleanSockTotalMap.get(sock); // get total number of clean socks
            if (noOfCleanSock == null) // if no previous clean pair of socks then initialize sock to zero
            {
                noOfCleanSock = 0L;
            }
            cleanSockTotalMap.put(sock, ++noOfCleanSock); // add sock to clean pile after washing
            
            noOfWashes--; // update no of washes after each wash
        }
    }
    
    //identifying socks with colors using an enumeration
    enum Colors
    {
        GREEN, BLUE, RED, YELLOW
    }
    
}
