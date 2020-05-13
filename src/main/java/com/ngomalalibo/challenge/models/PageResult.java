package com.ngomalalibo.challenge.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class PageResult
{//*page, per_page, total, total_pages, data
    
    /**
     * page: The current page of the result
     * per_page: The maximum number of users returned per page
     * total: The total number of users on all pages of the result
     * total_pages: The total number of pages with results
     * data: An array of objects containing users returned on the requested page
     */
    private String pageIndex;
    private int usersPerPage;
    private int totalNoOfUsers;
    private int totalNoOfPages;
    private List<User> listOfUsersOnPage = new ArrayList<>();
    
}
