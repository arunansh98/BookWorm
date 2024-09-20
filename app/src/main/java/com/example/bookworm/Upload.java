package com.example.bookworm;

import java.io.Serializable;

public class Upload implements Serializable
{
    private String URL;
    private String Quantity;
    private String Name;
    private String ISBN;
    private String Description;
    private String Author;


    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getName() {

        return Name;
    }

    public void setName(String name) {

        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getURL() {

        return URL;
    }

    public void setURL(String URL)
    {
         this.URL = URL;
    }
    public String getQuantity()
    {
        return Quantity;
    }
public void setQuantity(String Quantity){
        this.Quantity=Quantity;
}
    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }
}