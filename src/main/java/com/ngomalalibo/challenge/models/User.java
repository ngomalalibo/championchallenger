package com.ngomalalibo.challenge.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class User
{
    /**
     * id: unique ID of the user
     * username: the username of the user
     * about: the about infomation of the user
     * submitted: total number of articles submitted by the user
     * updated_at: the date and time of the last update to this record
     * submission_count: the number of submitted articles that are approved
     * comment_count: the total number of comments the user made
     * created_at: the date and time when the record was created
     */
    
    private int id;
    private String username;
    private String about;
    private int noOfArticles;
    private LocalDateTime lastUpdated;
    private int noOfApprovedArticles; // most active submission_count
    private int totalNoOfComments;
    private long created;
    private LocalDateTime createdDate;
    
}
