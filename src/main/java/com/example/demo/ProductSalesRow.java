package com.example.demo;

public class ProductSalesRow {
    private Integer productId; // <- Integer, not Long
    private String productName;
    private Long qty;      // SUM(Integer) -> Long (keep as Long)
    private Double net;    // Double
    private Double vat;    // Double
    private Double gross;  // Double

    public ProductSalesRow(Integer productId, String productName,
                           Long qty, Double net, Double vat, Double gross) {
        this.productId = productId;
        this.productName = productName;
        this.qty = qty;
        this.net = net;
        this.vat = vat;
        this.gross = gross;
    }

    public Integer getProductId()   { return productId; }
    public String getProductName()  { return productName; }
    public Long getQty()            { return qty; }
    public Double getNet()          { return net; }
    public Double getVat()          { return vat; }
    public Double getGross()        { return gross; }
}
