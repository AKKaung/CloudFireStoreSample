package com.asmaahamed.cloudfirestoresample.Entity;

import com.google.firebase.firestore.Exclude;

public class Note {

   private String documentId;
   private String title ;
   private String description;
   private int proirity;

   public Note(){

   }

    public Note(String title, String description,int proirity) {
        this.title = title;
        this.description = description;
        this.proirity=proirity;
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }


    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getProirity() {
        return proirity;
    }

    public void setProirity(int proirity) {
        this.proirity = proirity;
    }
}
