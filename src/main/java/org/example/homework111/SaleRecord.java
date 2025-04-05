package org.example.homework111;

import java.util.Date;

public class SaleRecord {
    int id;
    String productName;
    double price;
    int quantity;
    double totalSale;
    Date saleDate;

    SaleRecord(int id, String productName, double price, int quantity, double totalSale, Date saleDate) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.totalSale = totalSale;
        this.saleDate = saleDate;
    }
}
