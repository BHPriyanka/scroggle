package edu.neu.madcourse.priyankabh.note2map.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;

@IgnoreExtraProperties
public class Note implements Serializable{
    public String noteId; //initialized with timestamp
    public String noteType;
    public String noteDate;
    public String duration;
    public String startTime;
    public String noteReceived;
    public String owner; //Note's owner's username. Owner can delete/edit the note
    public ArrayList<String> targetedUsers;
    public ArrayList<NoteContent> noteContents;
    public String location;

    public String getLocation() {
        return location;
    }

    public Note(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Note(String noteType, String noteDate, String startTime, String duration, String noteReceived, String owner, ArrayList<NoteContent> noteContent, ArrayList<String> targetedUsers, String location) {
        this.noteId = Long.toString(System.currentTimeMillis());
        this.noteType = noteType;
        this.noteDate = noteDate;
        this.startTime = startTime;
        this.duration = duration;
        this.noteReceived = noteReceived;
        this.location = location;
        this.owner = owner;
        this.noteContents = noteContent;
        this.targetedUsers = targetedUsers;
    }

    public String getNoteType() {
        return noteType;
    }

    public String getNoteDate() {
        return noteDate;
    }

    public String getDuration() {
        return duration;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getNoteReceived() {
        return noteReceived;
    }

    public String getOwner() {
        return owner;
    }

    public ArrayList<String> getTargetedUsers() {
        return targetedUsers;
    }

    public String getNoteId() {
        return noteId;
    }

}

