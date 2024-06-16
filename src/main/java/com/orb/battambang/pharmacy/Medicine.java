package com.orb.battambang.pharmacy;

public class Medicine {
    private Integer id;
    private String name;
    private Integer quantityInMilligrams;
    private Integer stockLeft;


    // Constructor
    public Medicine(Integer id, String name, Integer quantity, Integer stock) {
        this.id = id;
        this.name = name;
        this.quantityInMilligrams = quantity;
        this.stockLeft = stock;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getQuantityInMilligrams() {
        return quantityInMilligrams;
    }

    public Integer getStockLeft() {
        return stockLeft;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantityInMilligrams(Integer quantity) {
        this.quantityInMilligrams = quantity;
    }

    public void setStockLeft(Integer stock) {
        this.stockLeft = stock;
    }
}
