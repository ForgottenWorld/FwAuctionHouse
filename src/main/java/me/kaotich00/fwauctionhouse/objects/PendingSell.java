package me.kaotich00.fwauctionhouse.objects;

import org.bukkit.inventory.ItemStack;

public class PendingSell {

    private int listingId;
    private ItemStack itemStack;
    private String buyerName;
    private float totalCost;

    public PendingSell(int listingId, ItemStack itemStack, String buyerName, float totalCost) {
        this.listingId = listingId;
        this.itemStack = itemStack;
        this.buyerName = buyerName;
        this.totalCost = totalCost;
    }

    public int getListingId() {
        return listingId;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public float getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(float totalCost) {
        this.totalCost = totalCost;
    }

}
